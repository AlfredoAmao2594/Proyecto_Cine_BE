package com.cine.auth.service;

import com.cine.auth.dto.AuthResponse;
import com.cine.auth.security.JwtUtil;
import com.cine.auth.service.impl.AuthServiceImpl;
import com.cine.auth.support.TokenDePrueba;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthServiceImplTest {

    /** JwtUtil real: queremos comprobar el contenido del token generado. */
    private final JwtUtil jwtUtil = new JwtUtil(TokenDePrueba.SECRETO, 120);
    private final AuthServiceImpl authService = new AuthServiceImpl(jwtUtil);

    @Test
    void loginInvitado_generaTokenConRolGuest() {
        AuthResponse respuesta = authService.loginInvitado();

        Claims claims = jwtUtil.getClaims(respuesta.getToken());
        assertThat(claims.getSubject()).startsWith("guest-");
        assertThat(claims.get("role")).isEqualTo("GUEST");
        assertThat(claims.get("name")).isEqualTo("Invitado");
        assertThat(respuesta.getEmail()).isNull();
        assertThat(respuesta.getTokenType()).isEqualTo("Bearer");
        assertThat(respuesta.getExpiresIn()).isEqualTo(7200);
    }

    @Test
    void loginInvitado_cadaInvitadoTieneUnIdDistinto() {
        String sub1 = jwtUtil.getClaims(authService.loginInvitado().getToken()).getSubject();
        String sub2 = jwtUtil.getClaims(authService.loginInvitado().getToken()).getSubject();

        assertThat(sub1).isNotEqualTo(sub2);
    }
}
