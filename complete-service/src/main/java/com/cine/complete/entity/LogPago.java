package com.cine.complete.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogPago {
    private UUID id;
    private String codigoReferencia;
    private String estado;
    private String codigoRespuesta;
    private String idTransaccion;
    private BigDecimal monto;
    private LocalDateTime fechaCreacion;
}
