package org.example.application;

import lombok.extern.slf4j.Slf4j;
import org.example.IcmpPingService;
import org.example.TcpIpRequestService;
import org.example.TraceService;
import org.example.report.ReportService;
import org.example.status.HostStatusService;

@Slf4j
public class PingApplication {
    public static void main(String[] args) {

        IcmpPingService icmpPingService = new IcmpPingService(HostStatusService.getInstance(), ReportService.getInstance());
        icmpPingService.pingHosts();

        TcpIpRequestService tcpIpRequestService = new TcpIpRequestService(HostStatusService.getInstance(), ReportService.getInstance());
        tcpIpRequestService.pingHosts();

        TraceService traceService = new TraceService(HostStatusService.getInstance());
        traceService.pingHosts();

    }
}