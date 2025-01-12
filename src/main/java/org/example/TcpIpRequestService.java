package org.example;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.report.ReportService;
import org.example.status.HostStatusService;
import org.example.util.ApplicationConfigHelper;
import org.example.util.TimestampHelper;
import org.example.util.UrlHelper;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TcpIpRequestService implements PingService {
    @Getter
    private final Map<String, String> hostResponses;
    private final Integer delay;
    private final Integer timeout;
    private final Integer responseTimeThreshold;
    private final HostStatusService hostStatusService;
    private final ReportService reportService;
    private final ScheduledExecutorService executorService;

    public TcpIpRequestService(HostStatusService hostStatusService, ReportService reportService) {
        Properties appConfig = ApplicationConfigHelper.getApplicationConfig();
        int numberOfHosts = appConfig.getProperty("hosts").split(",").length;
        this.delay = Integer.valueOf(appConfig.getProperty("tcp.delay"));
        this.timeout = Integer.valueOf(appConfig.getProperty("tcp.timeout"));
        this.responseTimeThreshold = Integer.valueOf(appConfig.getProperty("tcp.response.time.threshold"));
        this.hostResponses = new HashMap<>();
        this.hostStatusService = hostStatusService;
        this.reportService = reportService;
        executorService = Executors.newScheduledThreadPool(numberOfHosts);
    }

    @Override
    public void pingHosts() {
        List<String> hosts = hostStatusService.getHosts();
        hosts.forEach(host ->
                executorService.scheduleAtFixedRate(() -> sendRequest(host), 0, delay, TimeUnit.MILLISECONDS));
    }

    private void sendRequest(String host) {
        log.info("Thread " + Thread.currentThread().getName() + " is pinging host: " + host);
        HttpClient httpClient = HttpClient.newHttpClient();
        String url = UrlHelper.fixUrlPrefixes(host);
        HttpRequest request = HttpRequest.newBuilder()
                .timeout(Duration.of(timeout, ChronoUnit.MILLIS))
                .GET()
                .uri(URI.create(url))
                .build();
        try {
            log.info("Sending HTTP request to url:" + request.uri());
            long before = System.currentTimeMillis();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            long after = System.currentTimeMillis();
            long responseTime = after - before;
            log.info("Response: " + response);

            String status = TimestampHelper.addCurrentTimeStampPrefix(host + " response time: " + responseTime + "ms, status code:" + response.statusCode());
            updateHostStatus(host, status);
            if (response.statusCode() != 200 || responseTime > responseTimeThreshold) {
                reportService.triggerReport(host);
            }

        } catch (HttpConnectTimeoutException timeoutException) {
            String status = TimestampHelper.addCurrentTimeStampPrefix(host + " HTTP connect timed out");
            updateHostStatus(host, status);
            reportService.triggerReport(host);
            log.info(status);

        } catch (ConnectException connectException) {
            String status = TimestampHelper.addCurrentTimeStampPrefix(host + " HTTP connect exception");
            updateHostStatus(host, status);
            reportService.triggerReport(host);
            log.info(status);

        } catch (Exception exception) {
            String errorText = exception.getClass().getName() + " message:" + exception.getMessage();
            String status = TimestampHelper.addCurrentTimeStampPrefix(host + errorText);
            updateHostStatus(host, status);
            reportService.triggerReport(host);
            log.error(errorText);
        }
    }

    private void updateHostStatus(String host, String status) {
        hostResponses.put(host, status);
        hostStatusService.updateHostTcpIpStatus(host, status);
    }
}
