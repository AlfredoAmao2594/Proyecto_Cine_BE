package com.cine.premieres.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Estrenos {
    private UUID  id;
    private String titulo;
    private String descripcion;
    private String urlImagen;
    private LocalDate fechaEstreno;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}