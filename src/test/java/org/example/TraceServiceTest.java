package org.example;

import org.example.report.ReportService;
import org.example.status.HostStatusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraceServiceTest {
    @Mock
    HostStatusService hostStatusService;

    @InjectMocks
    TraceService traceService;

    @Test
    void pingHosts_twoHosts_shouldStoreResponses() throws InterruptedException {
        List<String> hosts = List.of("jasmin.com", "google.com");
        when(hostStatusService.getHosts()).thenReturn(hosts);

        assertTrue(traceService.getHostResponses().isEmpty());

        traceService.pingHosts();
        Thread.sleep(40000);

        assertFalse(traceService.getHostResponses().isEmpty());
        assertEquals(2, traceService.getHostResponses().size());
        assertNotNull(traceService.getHostResponses().get("jasmin.com"));
        assertFalse(traceService.getHostResponses().get("jasmin.com").isBlank());
        assertNotNull(traceService.getHostResponses().get("google.com"));
        assertFalse(traceService.getHostResponses().get("google.com").isBlank());
        verify(hostStatusService, atLeastOnce()).updateHostTraceStatus(any(), any());
    }

    @Test
    void pingHosts_hostWithTimeout_shouldStoreResponse() throws InterruptedException {
        List<String> hosts = List.of("oranum.com");
        when(hostStatusService.getHosts()).thenReturn(hosts);

        assertTrue(traceService.getHostResponses().isEmpty());

        traceService.pingHosts();
        Thread.sleep(360000);

        assertFalse(traceService.getHostResponses().isEmpty());
        assertEquals(1, traceService.getHostResponses().size());
        assertNotNull(traceService.getHostResponses().get("oranum.com"));
        assertFalse(traceService.getHostResponses().get("oranum.com").isBlank());
        verify(hostStatusService, atLeastOnce()).updateHostTraceStatus(any(), any());
    }

    @Test
    void pingHosts_notExistingHost_shouldStoreResponse() throws InterruptedException {
        List<String> hosts = List.of("stackofervlow.com");
        when(hostStatusService.getHosts()).thenReturn(hosts);
        assertTrue(traceService.getHostResponses().isEmpty());

        traceService.pingHosts();
        Thread.sleep(3000);

        assertFalse(traceService.getHostResponses().isEmpty());
        assertEquals(1, traceService.getHostResponses().size());
        assertNotNull(traceService.getHostResponses().get("stackofervlow.com"));
        assertFalse(traceService.getHostResponses().get("stackofervlow.com").isBlank());
        verify(hostStatusService, atLeastOnce()).updateHostTraceStatus(any(), any());
    }

}