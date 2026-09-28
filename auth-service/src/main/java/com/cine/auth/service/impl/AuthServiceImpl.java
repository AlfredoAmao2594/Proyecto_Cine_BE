package com.cine.auth.service.impl;

import com.cine.auth.dto.AuthResponse;
import com.cine.auth.security.JwtUtil;
import com.cine.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    public static final String ROL_INVITADO = "GUEST";
    private static final String NOMBRE_INVITADO = "Invitado";

    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse loginInvitado() {
        String sub = "guest-" + UUID.randomUUID();
        log.info("Login de invitado: {}", sub);

        return AuthResponse.builder()
                .token(jwtUtil.generarToken(sub, NOMBRE_INVITADO, ROL_INVITADO))
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpiracionSegundos())
                .name(NOMBRE_INVITADO)
                .email(null)
                .role(ROL_INVITADO)
                .build();
    }
}
