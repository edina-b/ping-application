package org.example.status;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.util.ApplicationConfigHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class HostStatusService {

    @Getter
    private final Map<String, HostStatusDto> hostStatuses;
    @Getter
    private final List<String> hosts;

    private HostStatusService() {
        Properties appConfig = ApplicationConfigHelper.getApplicationConfig();
        this.hosts = Arrays.asList(appConfig.getProperty("hosts").split(","));
        this.hostStatuses = new HashMap<>();
        this.hosts.forEach(host -> hostStatuses.put(host, new HostStatusDto(host, null, null, null)));
    }

    private static class Holder {
        private static final HostStatusService INSTANCE = new HostStatusService();
    }

    public static HostStatusService getInstance() {
        return HostStatusService.Holder.INSTANCE;
    }

    public void updateHostIcmpStatus(String host, String status) {
        hostStatuses.get(host).setIcmpResponse(status);
    }

    public void updateHostTcpIpStatus(String host, String status) {
        hostStatuses.get(host).setTcpIpResponse(status);
    }

    public void updateHostTraceStatus(String host, String status) {
        hostStatuses.get(host).setTraceResponse(status);
    }

    public HostStatusDto getHostStatus(String host) {
        return hostStatuses.get(host);
    }

}
