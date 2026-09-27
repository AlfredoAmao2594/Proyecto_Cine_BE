package com.cine.complete.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Compra {
    private UUID id;
    private String correo;
    private String nombreCompleto;
    private String tipoDocumento;
    private String numeroDocumento;
    private String idTransaccion;
    private Long idOrdenPayu;
    private LocalDateTime fechaOperacion;
    private BigDecimal montoTotal;
    private String estado;
    private LocalDateTime fechaCreacion;
    private List<CompraDetalle> detalles;   // no es columna: las líneas de compra_detalle
}