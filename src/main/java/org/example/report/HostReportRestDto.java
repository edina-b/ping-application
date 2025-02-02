package org.example.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class HostReportRestDto {
    private String host;
    private String icmpPingResponse;
    private String tcpPingResponse;
    private String traceResponse;
}
