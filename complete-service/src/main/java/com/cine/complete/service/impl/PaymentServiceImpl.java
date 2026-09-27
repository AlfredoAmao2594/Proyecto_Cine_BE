package com.cine.complete.service.impl;

import com.cine.complete.client.payu.PayuClient;
import com.cine.complete.client.payu.PayuRequest;
import com.cine.complete.client.payu.PayuResponse;
import com.cine.complete.config.PayuProperties;
import com.cine.complete.dto.DatosDispositivo;
import com.cine.complete.dto.PaymentRequest;
import com.cine.complete.dto.PaymentResponse;
import com.cine.complete.entity.LogPago;
import com.cine.complete.exception.BusinessException;
import com.cine.complete.repository.LogPagoRepository;
import com.cine.complete.service.CalculadoraPedido;
import com.cine.complete.service.PaymentService;
import com.cine.complete.service.PedidoCalculado;
import com.cine.complete.util.CardUtils;
import com.cine.complete.util.DocumentoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final ZoneId ZONA_LIMA = ZoneId.of("America/Lima");
    private static final Set<String> ESTADOS_VALIDOS = Set.of("APPROVED", "DECLINED", "PENDING", "ERROR");
    private static final String ESTADO_ERROR = "ERROR";

    /** El formulario del reto no pide teléfono ni dirección, pero PayU los exige. */
    private static final String TELEFONO_POR_DEFECTO = "999999999";
    private static final PayuRequest.Direccion DIRECCION_POR_DEFECTO = PayuRequest.Direccion.builder()
            .street1("Av. Principal 123").city("Lima").state("Lima")
            .country("PE").postalCode("15001").phone(TELEFONO_POR_DEFECTO)
            .build();

    private final PayuClient payuClient;
    private final PayuProperties payuProperties;
    private final CalculadoraPedido calculadoraPedido;
    private final LogPagoRepository logPagoRepository;

    @Override
    public PaymentResponse procesarPago(PaymentRequest request, DatosDispositivo dispositivo, String authorization) {
        validarDatos(request);

        // 1. El total SIEMPRE se calcula con los precios de candystore
        PedidoCalculado pedido = calculadoraPedido.calcular(request.getItems(), authorization);
        BigDecimal total = pedido.getTotal();

        // 2. Referencia única de nuestra orden
        String referencia = "CINE-" + UUID.randomUUID();
        log.info("Procesando pago {} | tarjeta {} | total S/ {}",
                referencia, CardUtils.enmascarar(request.getCardNumber()), total);

        // 3. Armar y enviar la petición a PayU
        PayuRequest payuRequest = construirPeticion(request, dispositivo, total, referencia);
        PayuResponse payuResponse;
        try {
            payuResponse = payuClient.enviarPago(payuRequest);
        } catch (BusinessException e) {
            registrarLog(referencia, ESTADO_ERROR, "SIN_RESPUESTA", null, total);
            throw e;
        }

        // 4. Interpretar la respuesta y dejar auditoría (aprobada o no)
        PaymentResponse respuesta = interpretar(payuResponse, referencia, total);
        String codigoRespuesta = payuResponse != null && payuResponse.getTransactionResponse() != null
                ? payuResponse.getTransactionResponse().getResponseCode() : ESTADO_ERROR;
        registrarLog(referencia, respuesta.getState(), codigoRespuesta, respuesta.getTransactionId(), total);

        if (PayuResponse.STATE_APPROVED.equals(respuesta.getState())) {
            log.info("Pago {} APROBADO, transacción {}", referencia, respuesta.getTransactionId());
        } else {
            log.warn("Pago {} NO aprobado: {} - {}", referencia, respuesta.getState(), respuesta.getMessage());
        }
        return respuesta;
    }

    /** Reglas que @Valid no puede expresar con anotaciones simples. */
    private void validarDatos(PaymentRequest request) {
        if (!CardUtils.esLuhnValido(request.getCardNumber())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "El número de tarjeta no es válido");
        }
        if (CardUtils.estaVencida(request.getExpirationDate(), YearMonth.now(ZONA_LIMA))) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "La tarjeta está vencida");
        }
        if (!DocumentoUtils.esValido(request.getDocumentType(), request.getDocumentNumber())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "El número de documento no es válido para " + request.getDocumentType());
        }
    }

    /** MD5 de "apiKey~merchantId~referenceCode~valor~moneda", como indica PayU. */
    String calcularFirma(String referencia, BigDecimal total) {
        String texto = String.join("~",
                payuProperties.getApiKey(),
                payuProperties.getMerchantId(),
                referencia,
                total.toPlainString(),
                payuProperties.getCurrency());
        return DigestUtils.md5DigestAsHex(texto.getBytes(StandardCharsets.UTF_8));
    }

    private PayuRequest construirPeticion(PaymentRequest request, DatosDispositivo dispositivo,
                                          BigDecimal total, String referencia) {
        PayuRequest.Persona comprador = PayuRequest.Persona.builder()
                .fullName(request.getFullName())
                .emailAddress(request.getEmail())
                .contactPhone(TELEFONO_POR_DEFECTO)
                .dniNumber(request.getDocumentNumber())
                .shippingAddress(DIRECCION_POR_DEFECTO)
                .build();

        PayuRequest.Persona pagador = PayuRequest.Persona.builder()
                .fullName(request.getFullName())
                .emailAddress(request.getEmail())
                .contactPhone(TELEFONO_POR_DEFECTO)
                .dniNumber(request.getDocumentNumber())
                .billingAddress(DIRECCION_POR_DEFECTO)
                .build();

        PayuRequest.Order orden = PayuRequest.Order.builder()
                .accountId(payuProperties.getAccountId())
                .referenceCode(referencia)
                .description("Compra de dulcería - Cine")
                .language("es")
                .signature(calcularFirma(referencia, total))
                .additionalValues(Map.of("TX_VALUE", new PayuRequest.Valor(total, payuProperties.getCurrency())))
                .buyer(comprador)
                .build();

        PayuRequest.Transaction transaccion = PayuRequest.Transaction.builder()
                .order(orden)
                .payer(pagador)
                .creditCard(PayuRequest.CreditCard.builder()
                        .number(request.getCardNumber())
                        .securityCode(request.getCvv())
                        .expirationDate(request.getExpirationDate())
                        .name(request.getCardHolderName())
                        .build())
                .extraParameters(Map.of("INSTALLMENTS_NUMBER", 1))
                .type("AUTHORIZATION_AND_CAPTURE")
                .paymentMethod(CardUtils.franquicia(request.getCardNumber()))
                .paymentCountry(payuProperties.getCountry())
                .deviceSessionId(dispositivo.getDeviceSessionId())
                .ipAddress(dispositivo.getIpAddress())
                .cookie(dispositivo.getCookie())
                .userAgent(dispositivo.getUserAgent())
                .build();

        return PayuRequest.builder()
                .language("es")
                .command("SUBMIT_TRANSACTION")
                .merchant(new PayuRequest.Merchant(payuProperties.getApiKey(), payuProperties.getApiLogin()))
                .transaction(transaccion)
                .test(payuProperties.isTest())
                .build();
    }

    private PaymentResponse interpretar(PayuResponse payuResponse, String referencia, BigDecimal total) {
        // code != SUCCESS: PayU ni siquiera procesó la transacción (firma mala, datos faltantes...)
        if (payuResponse == null || !PayuResponse.CODE_SUCCESS.equals(payuResponse.getCode())
                || payuResponse.getTransactionResponse() == null) {
            String error = payuResponse != null && payuResponse.getError() != null
                    ? payuResponse.getError() : "La pasarela no procesó la transacción";
            return PaymentResponse.builder()
                    .state(ESTADO_ERROR).message(error).amount(total).referenceCode(referencia)
                    .build();
        }

        PayuResponse.TransactionResponse tr = payuResponse.getTransactionResponse();
        String mensaje = tr.getResponseMessage() != null ? tr.getResponseMessage()
                : tr.getPaymentNetworkResponseErrorMessage() != null ? tr.getPaymentNetworkResponseErrorMessage()
                : tr.getResponseCode();

        return PaymentResponse.builder()
                .state(tr.getState())
                .transactionId(tr.getTransactionId())
                .orderId(tr.getOrderId())
                .operationDate(tr.getOperationDate())
                .message(mensaje)
                .amount(total)
                .referenceCode(referencia)
                .build();
    }

    private void registrarLog(String referencia, String estado, String codigoRespuesta,
                              String idTransaccion, BigDecimal monto) {
        try {
            logPagoRepository.registrar(LogPago.builder()
                    .codigoReferencia(referencia)
                    .estado(ESTADOS_VALIDOS.contains(estado) ? estado : ESTADO_ERROR)
                    .codigoRespuesta(codigoRespuesta)
                    .idTransaccion(idTransaccion)
                    .monto(monto)
                    .build());
        } catch (DataAccessException e) {
            // El cobro ya ocurrió: no se debe responder error al usuario por un fallo de auditoría
            log.error("No se pudo guardar log_pago de {}: {}", referencia, e.getMessage());
        }
    }
}
