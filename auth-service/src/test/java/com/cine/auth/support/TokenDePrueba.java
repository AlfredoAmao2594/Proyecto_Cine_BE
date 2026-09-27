package com.cine.auth.support;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Genera JWT para las pruebas, firmados con el mismo secreto por defecto del application.yml.
 * En producción los tokens los firma solo auth-service.
 */
public final class TokenDePrueba {

    public static final String SECRETO = "U8eBYJbNrTv/G6bEsYTMTU3Qwz2/suBjMFK56ZuSz+8=";

    private TokenDePrueba() {
    }

    public static String usuario() {
        return generar("ana@gmail.com", "Ana Pérez", "USER", 3_600_000L);
    }

    public static String invitado() {
        return generar("guest-123", "Invitado", "GUEST", 3_600_000L);
    }

    public static String expirado() {
        return generar("ana@gmail.com", "Ana Pérez", "USER", -60_000L);
    }

    private static String generar(String sub, String nombre, String rol, long milisegundosDeVida) {
        long ahora = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(sub)
                .claim("name", nombre)
                .claim("role", rol)
                .setIssuedAt(new Date(ahora))
                .setExpiration(new Date(ahora + milisegundosDeVida))
                .signWith(Keys.hmacShaKeyFor(SECRETO.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }
}
