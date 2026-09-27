package com.cine.complete.controller;

import com.cine.complete.dto.ApiResponse;
import com.cine.complete.dto.CompleteRequest;
import com.cine.complete.dto.CompleteResponse;
import com.cine.complete.repository.ResultadoRegistroCompra;
import com.cine.complete.service.CompleteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/complete")
@RequiredArgsConstructor
@Tag(name = "Complete", description = "Registra la compra después de que PayU la aprobó")
public class CompleteController {

    private final CompleteService completeService;

    @PostMapping
    @Operation(summary = "Registra la compra aprobada. Responde code \"0\" (o \"1\" si ya estaba registrada)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "code 0 = registrada, 1 = ya existía"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o transacción no aprobada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Sin token")
    })
    public ResponseEntity<ApiResponse<CompleteResponse>> completar(
            @Valid @RequestBody CompleteRequest request,
            @Parameter(hidden = true) @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        log.info("POST /api/complete - transacción {}", request.getTransactionId());
        ResultadoRegistroCompra resultado = completeService.registrarCompra(request, authorization);

        return ResponseEntity.ok(new ApiResponse<>(resultado.getCodigo(), resultado.getMensaje(),
                new CompleteResponse(resultado.getIdCompra())));
    }
}