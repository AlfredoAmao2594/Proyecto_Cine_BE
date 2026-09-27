package com.cine.auth.controller;

import com.cine.auth.dto.ApiResponse;
import com.cine.auth.dto.AuthResponse;
import com.cine.auth.dto.SesionResponse;
import com.cine.auth.security.UsuarioAutenticado;
import com.cine.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Emite el JWT para invitados")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/guest")
    @Operation(summary = "Ingresar como invitado (no requiere body)")
    public ResponseEntity<ApiResponse<AuthResponse>> loginInvitado() {
        log.info("POST /api/auth/guest");
        return ResponseEntity.ok(ApiResponse.ok(authService.loginInvitado()));
    }

    @GetMapping("/me")
    @Operation(summary = "Devuelve los datos del token enviado (sirve para validar la sesión)")
    public ResponseEntity<ApiResponse<SesionResponse>> sesionActual(
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario) {

        return ResponseEntity.ok(ApiResponse.ok(SesionResponse.builder()
                .sub(usuario.getSub())
                .name(usuario.getNombre())
                .role(usuario.getRol())
                .build()));
    }
}
