package org.example.status;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HostStatusServiceTest {

    public static final String TEST_HOST_1 = "jasmin.com";
    public static final String TEST_HOST_2 = "oranum.com";
    public static final String TEST_STATUS = "new status";

    private HostStatusService hostStatusService;

    @BeforeAll
    public void setup() {
        hostStatusService = HostStatusService.getInstance();
    }

    @Test
    public void testGetHostStatuses_returnsHostStatuses() {
        Map<String, HostStatusDto> hostStatuses = hostStatusService.getHostStatuses();

        assertEquals(2, hostStatuses.size());
        assertEquals(TEST_HOST_1, hostStatuses.get(TEST_HOST_1).getHost());
        assertEquals(TEST_HOST_2, hostStatuses.get(TEST_HOST_2).getHost());
    }

    @Test
    public void testUpdateHostIcmpStatus_shouldUpdateIcmpStatus() {

        Map<String, HostStatusDto> hostStatuses = hostStatusService.getHostStatuses();
        assertNull(hostStatuses.get(TEST_HOST_1).getIcmpResponse());

        hostStatusService.updateHostIcmpStatus(TEST_HOST_1, TEST_STATUS);

        assertEquals(TEST_STATUS, hostStatuses.get(TEST_HOST_1).getIcmpResponse());
    }

    @Test
    public void testUpdateHostTcpStatus_shouldUpdateTcpStatus() {
        HostStatusDto statusDto = hostStatusService.getHostStatus(TEST_HOST_2);
        assertNull(statusDto.getTcpIpResponse());

        hostStatusService.updateHostTcpIpStatus(TEST_HOST_2, TEST_STATUS);

        assertEquals(TEST_STATUS, statusDto.getTcpIpResponse());
    }

    @Test
    public void testUpdateHostTraceStatus_shouldUpdateTraceStatus() {
        HostStatusDto statusDto = hostStatusService.getHostStatus(TEST_HOST_1);
        assertNull(statusDto.getTraceResponse());

        hostStatusService.updateHostTraceStatus(TEST_HOST_1, TEST_STATUS);

        assertEquals(TEST_STATUS, statusDto.getTraceResponse());
    }
}