package com.cine.complete.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String ATRIBUTO_ERROR_JWT = "jwt_error";
    private static final String PREFIJO_BEARER = "Bearer ";

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(PREFIJO_BEARER)) {
            String token = header.substring(PREFIJO_BEARER.length());
            try {
                Claims claims = jwtUtil.getClaims(token);
                String rol = claims.get("role", String.class);

                UsuarioAutenticado usuario = new UsuarioAutenticado(
                        claims.getSubject(), claims.get("name", String.class), rol);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        usuario, null, List.of(new SimpleGrantedAuthority("ROLE_" + rol)));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (ExpiredJwtException e) {
                log.warn("Token expirado para {}", e.getClaims().getSubject());
                request.setAttribute(ATRIBUTO_ERROR_JWT, "El token expiró, vuelve a iniciar sesión");
            } catch (JwtException | IllegalArgumentException e) {
                log.warn("Token inválido: {}", e.getMessage());
                request.setAttribute(ATRIBUTO_ERROR_JWT, "Token inválido");
            }
        }

        chain.doFilter(request, response);
    }
}