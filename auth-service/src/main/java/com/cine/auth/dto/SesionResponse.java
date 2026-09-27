package com.cine.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Datos de la sesión actual (GET /api/auth/me). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SesionResponse {
    private String sub;
    private String name;
    private String role;
}
