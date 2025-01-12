package org.example;

import org.example.report.ReportService;
import org.example.status.HostStatusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IcmpPingServiceTest {

    @Mock
    HostStatusService hostStatusService;

    @Mock
    ReportService reportService;

    @InjectMocks
    IcmpPingService icmpPingService;

    @Test
    void pingHosts_twoHosts_shouldStoreResults() throws InterruptedException {
        List<String> hosts = List.of("jasmin.com", "google.com");
        when(hostStatusService.getHosts()).thenReturn(hosts);
        assertTrue(icmpPingService.getHostResponses().isEmpty());

        icmpPingService.pingHosts();
        Thread.sleep(5000);

        assertFalse(icmpPingService.getHostResponses().isEmpty());
        assertEquals(2, icmpPingService.getHostResponses().size());
        assertNotNull(icmpPingService.getHostResponses().get("jasmin.com"));
        assertFalse(icmpPingService.getHostResponses().get("jasmin.com").isBlank());
        assertNotNull(icmpPingService.getHostResponses().get("google.com"));
        assertFalse(icmpPingService.getHostResponses().get("google.com").isBlank());
        verify(hostStatusService, atLeastOnce()).updateHostIcmpStatus(any(), any());
        verify(reportService, never()).triggerReport(any());
    }
    @Test
    void pingHosts_oneHostWithoutTimeOut_shouldStoreResponseAndNotCallReport() throws InterruptedException {
        List<String> hosts = List.of( "google.com");
        when(hostStatusService.getHosts()).thenReturn(hosts);
        assertTrue(icmpPingService.getHostResponses().isEmpty());

        icmpPingService.pingHosts();
        Thread.sleep(5000);

        assertFalse(icmpPingService.getHostResponses().isEmpty());
        assertEquals(1, icmpPingService.getHostResponses().size());
        assertNotNull(icmpPingService.getHostResponses().get("google.com"));
        assertFalse(icmpPingService.getHostResponses().get("google.com").isBlank());
        verify(hostStatusService, atLeastOnce()).updateHostIcmpStatus(any(), any());
        verify(reportService, never()).triggerReport(any());
    }

    @Test
    void pingHosts_hostWithTimeout_shouldStoreResponseAndCallReport() throws InterruptedException {
        List<String> hosts = List.of("oranum.com");
        when(hostStatusService.getHosts()).thenReturn(hosts);
        assertTrue(icmpPingService.getHostResponses().isEmpty());

        icmpPingService.pingHosts();
        Thread.sleep(25000);

        assertFalse(icmpPingService.getHostResponses().isEmpty());
        assertEquals(1, icmpPingService.getHostResponses().size());
        assertNotNull(icmpPingService.getHostResponses().get("oranum.com"));
        assertFalse(icmpPingService.getHostResponses().get("oranum.com").isBlank());
        verify(hostStatusService, atLeastOnce()).updateHostIcmpStatus(any(), any());
        verify(reportService, atLeastOnce()).triggerReport(any());
    }

    @Test
    void pingHosts_notExistingHost_shouldStoreResponseAndCallReport() throws InterruptedException {
        List<String> hosts = List.of("stackofervlow.com");
        when(hostStatusService.getHosts()).thenReturn(hosts);
        assertTrue(icmpPingService.getHostResponses().isEmpty());

        icmpPingService.pingHosts();
        Thread.sleep(3000);

        assertFalse(icmpPingService.getHostResponses().isEmpty());
        assertEquals(1, icmpPingService.getHostResponses().size());
        assertNotNull(icmpPingService.getHostResponses().get("stackofervlow.com"));
        assertFalse(icmpPingService.getHostResponses().get("stackofervlow.com").isBlank());
        verify(hostStatusService, atLeastOnce()).updateHostIcmpStatus(any(), any());
        verify(reportService, atLeastOnce()).triggerReport(any());
    }

}