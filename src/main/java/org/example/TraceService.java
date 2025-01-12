package org.example;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.status.HostStatusService;
import org.example.util.ApplicationConfigHelper;
import org.example.util.InputStreamConverter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TraceService implements PingService {
    @Getter
    private final Map<String, String> hostResponses;
    private final Integer delay;
    private final HostStatusService hostStatusService;
    private final ScheduledExecutorService executorService;


    public TraceService(HostStatusService hostStatusService) {
        Properties appConfig = ApplicationConfigHelper.getApplicationConfig();
        String[] hosts = appConfig.getProperty("hosts").split(",");
        this.delay = Integer.valueOf(appConfig.getProperty("trace.delay"));
        this.hostResponses = new HashMap<>();
        this.hostStatusService = hostStatusService;
        executorService = Executors.newScheduledThreadPool(hosts.length);
    }

    @Override
    public void pingHosts() {
        List<String> hosts = hostStatusService.getHosts();
        hosts.forEach(host -> executorService.scheduleAtFixedRate(() -> traceHost(host), 0, delay, TimeUnit.MILLISECONDS));
    }

    private void traceHost(String host) {
        log.info("Thread " + Thread.currentThread().getName() + " is calling tracert on host: " + host);
        try {
            Process traceRt = Runtime.getRuntime().exec("tracert " + host);
            String errorResponse = InputStreamConverter.convertToString(traceRt.getErrorStream());

            if (errorResponse == null || errorResponse.isBlank()) {
                String response = InputStreamConverter.convertToString(traceRt.getInputStream());
                hostResponses.put(host, response);
                hostStatusService.updateHostTraceStatus(host, response);
                log.info("Trace route response: " + response);
            } else {
                hostResponses.put(host, errorResponse);
                hostStatusService.updateHostTraceStatus(host, errorResponse);
                log.info("Trace route response: " + errorResponse);
            }
        } catch (IOException exception) {
            String errorText = exception.getClass().getName() + " message:" + exception.getMessage();
            log.error(errorText);
        }
    }
}
