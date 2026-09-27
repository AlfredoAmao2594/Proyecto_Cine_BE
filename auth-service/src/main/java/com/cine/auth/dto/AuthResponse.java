package com.cine.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Respuesta de login: el token y los datos que el front muestra y usa en la pantalla Pago. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType;   // siempre "Bearer"
    private long expiresIn;     // segundos de vida del token
    private String name;
    private String email;       // null para invitados
    private String role;        // USER | GUEST
}
