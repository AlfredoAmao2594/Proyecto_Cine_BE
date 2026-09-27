package com.cine.premieres.config;

import com.cine.premieres.security.JwtAuthFilter;
import com.cine.premieres.security.JwtAuthenticationEntryPoint;
import com.cine.premieres.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    /** Rutas que NO piden token. */
    private static final String[] RUTAS_PUBLICAS = {
            "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
            "/actuator/health"
    };

    private final JwtUtil jwtUtil;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // API REST sin sesión ni formularios: CSRF no aplica
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                // Cada request trae su token; el servidor no guarda sesión
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 401 en JSON cuando falta el token o es inválido
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .antMatchers(RUTAS_PUBLICAS).permitAll()
                        // premieres es público: Home carga sin login
                        .antMatchers(HttpMethod.GET, "/api/premieres/**").permitAll()
                        // cualquier otra ruta exige token
                        .anyRequest().authenticated())
                // Nuestro filtro se ejecuta antes del filtro de login de Spring
                .addFilterBefore(new JwtAuthFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}