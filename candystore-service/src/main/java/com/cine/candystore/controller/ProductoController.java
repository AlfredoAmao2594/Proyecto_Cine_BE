package com.cine.candystore.controller;

import com.cine.candystore.dto.ApiResponse;
import com.cine.candystore.dto.PrecioProductoResponse;
import com.cine.candystore.dto.ProductoResponse;
import com.cine.candystore.security.UsuarioAutenticado;
import com.cine.candystore.service.ProductoService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/candystore")
@RequiredArgsConstructor
@Tag(name = "Dulcería", description = "Productos de dulcería (requiere token)")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    @Operation(summary = "Lista los productos de dulcería")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado de productos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Sin token o token inválido")
    })
    public ResponseEntity<ApiResponse<List<ProductoResponse>>> listarProductos(
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario) {

        log.info("GET /api/candystore - usuario: {} ({})", usuario.getNombre(), usuario.getRol());
        return ResponseEntity.ok(ApiResponse.ok(productoService.listarProductos()));
    }

    @GetMapping("/precios")
    @Operation(summary = "Precios vigentes de los productos indicados (uso interno de complete-service)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Precios encontrados"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "ids vacío o con formato inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Algún producto no existe o está inactivo")
    })
    public ResponseEntity<ApiResponse<List<PrecioProductoResponse>>> obtenerPrecios(
            @Parameter(description = "UUIDs separados por coma") @RequestParam List<UUID> ids) {

        log.info("GET /api/candystore/precios - {} ids", ids.size());
        return ResponseEntity.ok(ApiResponse.ok(productoService.obtenerPrecios(ids)));
    }
}