package org.example.status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class HostStatusDto {
    private String host;
    private String icmpResponse;
    private String tcpIpResponse;
    private String traceResponse;
}
