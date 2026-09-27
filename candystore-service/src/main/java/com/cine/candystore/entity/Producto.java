package com.cine.candystore.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    private UUID id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String urlImagen;
    private String categoria;
    private Boolean activo;
}