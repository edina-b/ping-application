package org.example.report;

import org.example.status.HostStatusDto;
import org.example.status.HostStatusService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReportServiceTest {
    @Mock
    HostStatusService hostStatusService = Mockito.mock(HostStatusService.class);

    @Test
    void triggerReport_hostWithStatusData_shouldSendReport() throws NoSuchFieldException, IllegalAccessException {
        ReportService reportService = Mockito.spy(ReportService.getInstance());

        Field hostStatusServiceField = ReportService.class.getDeclaredField("hostStatusService");
        hostStatusServiceField.setAccessible(true);
        hostStatusServiceField.set(reportService, hostStatusService);

        when(hostStatusService.getHostStatus(any())).thenReturn(HostStatusDto.builder()
                .host("jasmin.com")
                .icmpResponse("icmp responseűőé")
                .tcpIpResponse("tcp response")
                .traceResponse("trace response")
                .build());

        reportService.triggerReport("jasmin.com");

        verify(hostStatusService).getHostStatus(any());
        verify(reportService).sendReport(any());
    }
}