package org.example;

import org.example.report.ReportService;
import org.example.status.HostStatusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TcpIpRequestServiceTest {

    @Mock
    HostStatusService hostStatusService;

    @Mock
    ReportService reportService;

    @InjectMocks
    TcpIpRequestService tcpIpRequestService;

    @Test
    void pingHosts_hostWithNoErrors_shouldStoreResponseAndNotCallReport() throws InterruptedException {
        List<String> hosts = List.of("google.com");
        when(hostStatusService.getHosts()).thenReturn(hosts);

        tcpIpRequestService.pingHosts();
        Thread.sleep(1000);

        assertEquals(1, tcpIpRequestService.getHostResponses().size());
        assertNotNull(tcpIpRequestService.getHostResponses().get("google.com"));
        assertFalse(tcpIpRequestService.getHostResponses().get("google.com").isBlank());
        verify(reportService,never()).triggerReport(any());
    }

    @Test
    void pingHosts_hostWithTimeout_shouldStoreResponseAndCallReport() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        List<String> hosts = List.of("google.com");
        when(hostStatusService.getHosts()).thenReturn(hosts);
        Field timeout = TcpIpRequestService.class.getDeclaredField("timeout");
        timeout.setAccessible(true);
        timeout.set(tcpIpRequestService,1);

        tcpIpRequestService.pingHosts();
        Thread.sleep(1000);

        assertEquals(1, tcpIpRequestService.getHostResponses().size());
        assertNotNull(tcpIpRequestService.getHostResponses().get("google.com"));
        assertFalse(tcpIpRequestService.getHostResponses().get("google.com").isBlank());
        verify(reportService,atLeastOnce()).triggerReport(any());
    }

    @Test
    void pingHosts_hostWithNot200Response_shouldStoreResponseAndCallReport() throws InterruptedException {
        List<String> hosts = List.of("oranum.com");
        when(hostStatusService.getHosts()).thenReturn(hosts);

        tcpIpRequestService.pingHosts();
        Thread.sleep(3000);

        assertEquals(1, tcpIpRequestService.getHostResponses().size());
        assertNotNull(tcpIpRequestService.getHostResponses().get("oranum.com"));
        assertFalse(tcpIpRequestService.getHostResponses().get("oranum.com").isBlank());
        verify(reportService,atLeastOnce()).triggerReport(any());
    }

    @Test
    void pingHosts_notExistingHost_shouldStoreResponseAndCallReport() throws InterruptedException {
        List<String> hosts = List.of("https://stackofervlow.com");
        when(hostStatusService.getHosts()).thenReturn(hosts);

        tcpIpRequestService.pingHosts();
        Thread.sleep(3000);

        assertEquals(1, tcpIpRequestService.getHostResponses().size());
        assertNotNull(tcpIpRequestService.getHostResponses().get("https://stackofervlow.com"));
        assertFalse(tcpIpRequestService.getHostResponses().get("https://stackofervlow.com").isBlank());
        verify(reportService,atLeastOnce()).triggerReport(any());
    }
    @Test
    void pingHosts_twoHosts_shouldStoreResults() throws InterruptedException {
        List<String> hosts = List.of("oranum.com","jasmin.com");
        when(hostStatusService.getHosts()).thenReturn(hosts);

        tcpIpRequestService.pingHosts();
        Thread.sleep(5000);

        assertEquals(2, tcpIpRequestService.getHostResponses().size());
        assertNotNull(tcpIpRequestService.getHostResponses().get("jasmin.com"));
        assertNotNull(tcpIpRequestService.getHostResponses().get("oranum.com"));
        assertFalse(tcpIpRequestService.getHostResponses().get("jasmin.com").isBlank());
        assertFalse(tcpIpRequestService.getHostResponses().get("oranum.com").isBlank());
        verify(reportService,atLeastOnce()).triggerReport(any());
    }
}