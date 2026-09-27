package com.cine.complete.controller;

import com.cine.complete.client.payu.PayuResponse;
import com.cine.complete.dto.ApiResponse;
import com.cine.complete.dto.DatosDispositivo;
import com.cine.complete.dto.PaymentRequest;
import com.cine.complete.dto.PaymentResponse;
import com.cine.complete.security.UsuarioAutenticado;
import com.cine.complete.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Cobro con PayU (sandbox)")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Cobra el carrito con PayU. El total lo calcula el backend.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "PayU respondió. code=0 si APPROVED, code=1 si fue rechazado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Sin token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "502", description = "PayU no respondió")
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> pagar(
            @Valid @RequestBody PaymentRequest request,
            @Parameter(hidden = true) @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario,
            HttpServletRequest httpRequest) {

        log.info("POST /api/payments - usuario: {} - {}", usuario.getSub(), request);
        PaymentResponse pago = paymentService.procesarPago(request, datosDispositivo(httpRequest, usuario), authorization);

        boolean aprobado = PayuResponse.STATE_APPROVED.equals(pago.getState());
        return ResponseEntity.ok(new ApiResponse<>(aprobado ? "0" : "1", pago.getMessage(), pago));
    }

    /** PayU exige ip, userAgent, cookie y deviceSessionId (antifraude). */
    private DatosDispositivo datosDispositivo(HttpServletRequest request, UsuarioAutenticado usuario) {
        String forwardedFor = request.getHeader("X-Forwarded-For");   // IP real si vino por el gateway
        String ip = forwardedFor != null ? forwardedFor.split(",")[0].trim() : request.getRemoteAddr();
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        String sesion = DigestUtils.md5DigestAsHex(usuario.getSub().getBytes(StandardCharsets.UTF_8));

        return DatosDispositivo.builder()
                .ipAddress(ip)
                .userAgent(userAgent != null ? userAgent : "desconocido")
                .cookie(sesion)
                .deviceSessionId(sesion)
                .build();
    }
}