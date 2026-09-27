package com.cine.premieres.controller;

import com.cine.premieres.dto.ApiResponse;
import com.cine.premieres.dto.EstrenoResponse;
import com.cine.premieres.security.UsuarioAutenticado;
import com.cine.premieres.service.EstrenosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/premieres")
@RequiredArgsConstructor
@Tag(name = "Estrenos", description = "Cartelera que se muestra en la pantalla Home")
public class EstrenosController {

    private final EstrenosService estrenosService;

    @GetMapping
    @Operation(summary = "Lista los estrenos activos (público, no requiere token)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado de estrenos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
    })
    public ResponseEntity<ApiResponse<List<EstrenoResponse>>> listarEstrenos(
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario) {

        log.info("GET /api/premieres - usuario: {}", usuario != null ? usuario.getNombre() : "anónimo");
        List<EstrenoResponse> estrenos = estrenosService.listarEstrenos();
        return ResponseEntity.ok(ApiResponse.ok(estrenos));
    }
}