package com.cine.complete.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatosDispositivo {
    private String ipAddress;
    private String userAgent;
    private String cookie;
    private String deviceSessionId;
}
