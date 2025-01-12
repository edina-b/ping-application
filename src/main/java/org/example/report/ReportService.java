package org.example.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import org.example.status.HostStatusDto;
import org.example.status.HostStatusService;
import org.example.util.ApplicationConfigHelper;
import org.example.util.UrlHelper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;


@Slf4j
public class ReportService {

    private final String reportUrl;
    private final HostStatusService hostStatusService;

    private ReportService(HostStatusService hostStatusService) {
        Properties appConfig = ApplicationConfigHelper.getApplicationConfig();
        this.reportUrl = appConfig.getProperty("report.url");
        this.hostStatusService = hostStatusService;
    }

    private static class Holder {
        private static final ReportService INSTANCE = new ReportService(HostStatusService.getInstance());
    }

    public static ReportService getInstance() {
        return Holder.INSTANCE;
    }

    public void triggerReport(String host) {
        HostStatusDto hostStatus = hostStatusService.getHostStatus(host);

        HostReportRestDto hostReport = HostReportRestDto.builder()
                .host(host)
                .icmpPingResponse(hostStatus.getIcmpResponse())
                .tcpPingResponse(hostStatus.getTcpIpResponse())
                .traceResponse(hostStatus.getTraceResponse())
                .build();

        sendReport(hostReport);
    }

    void sendReport(HostReportRestDto hostReportData) {
        try {
            String url = UrlHelper.fixUrlPrefixes(reportUrl);
            log.warn("Sending report to "+url+ "\n" +
                    "host: "+hostReportData.getHost()+ "\n" +
                    "latest ICMP ping response: "+hostReportData.getIcmpPingResponse() + "\n" +
                    "latest TCP/IP ping response: "+ hostReportData.getTcpPingResponse() + "\n" +
                    "latest trace route response:" + hostReportData.getTraceResponse());

            HttpClient client = HttpClient.newHttpClient();
            ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = objectWriter.writeValueAsString(hostReportData);
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("Report response: " + response.statusCode() + " " + response.body());
        } catch (Exception e) {
            log.error("Failed to send report because of exception:" + e.getClass().getName() + " " + e.getMessage());
        }
    }
}
