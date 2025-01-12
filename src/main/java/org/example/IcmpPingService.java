package org.example;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.report.ReportService;
import org.example.status.HostStatusService;
import org.example.util.ApplicationConfigHelper;
import org.example.util.InputStreamConverter;
import org.example.util.TimestampHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j

public class IcmpPingService implements PingService {
    @Getter
    private final Map<String, String> hostResponses;
    private final Integer delay;
    private final HostStatusService hostStatusService;
    private final ReportService reportService;
    private final ScheduledExecutorService executorService;

    public IcmpPingService(HostStatusService hostStatusService, ReportService reportService) {
        Properties appConfig = ApplicationConfigHelper.getApplicationConfig();
        int numberOfHosts = appConfig.getProperty("hosts").split(",").length;
        this.delay = Integer.valueOf(appConfig.getProperty("icmp.delay"));
        this.hostResponses = new HashMap<>();
        this.hostStatusService = hostStatusService;
        this.reportService = reportService;
        executorService = Executors.newScheduledThreadPool(numberOfHosts);
    }

    @Override
    public void pingHosts() {
        List<String> hosts = hostStatusService.getHosts();
        hosts.forEach(host ->
                executorService.scheduleAtFixedRate(() -> performBatchPings(host), 0, delay, TimeUnit.MILLISECONDS));
    }

    private void performBatchPings(String host) {
        log.info("Thread " + Thread.currentThread().getName() + " is pinging host: " + host);
        try {
            Process process = Runtime.getRuntime().exec("ping -n 5 " + host);

            String errorText = InputStreamConverter.convertToString(process.getErrorStream());

            if (errorText == null || errorText.isBlank()) {
                hostResponses.put(host, TimestampHelper.addCurrentTimeStampPrefix(InputStreamConverter.convertToString(process.getInputStream())));
            } else {
                hostResponses.put(host, TimestampHelper.addCurrentTimeStampPrefix(errorText));
            }

            String completeResponse = hostResponses.get(host);
            log.info("ICMP response: " + completeResponse);
            hostStatusService.updateHostIcmpStatus(host, completeResponse);
            validateResponse(host, completeResponse);

        } catch (Exception exception) {
            String errorText = exception.getClass().getName() + " message:" + exception.getMessage();
            String status = TimestampHelper.addCurrentTimeStampPrefix(host + errorText);
            hostResponses.put(host, status);
            hostStatusService.updateHostIcmpStatus(host, status);
            reportService.triggerReport(host);
            log.error(errorText);
        }
    }

    private void validateResponse(String host, String response) {
        if (!response.contains("(0% loss")) {
            reportService.triggerReport(host);
        }
    }
}


