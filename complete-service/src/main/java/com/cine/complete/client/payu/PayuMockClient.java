package com.cine.complete.client.payu;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@ConditionalOnProperty(name = "payu.mock", havingValue = "true")
public class PayuMockClient implements PayuClient {

    @Override
    public PayuResponse enviarPago(PayuRequest request) {
        boolean aprobado = "APPROVED".equals(request.getTransaction().getCreditCard().getName());
        log.warn("PayU en modo MOCK: pago {}", aprobado ? "APROBADO" : "RECHAZADO");

        return PayuResponse.builder()
                .code(PayuResponse.CODE_SUCCESS)
                .transactionResponse(PayuResponse.TransactionResponse.builder()
                        .orderId(ThreadLocalRandom.current().nextLong(1_000_000_000L, 9_999_999_999L))
                        .transactionId(UUID.randomUUID().toString())
                        .state(aprobado ? "APPROVED" : "DECLINED")
                        .responseCode(aprobado ? "APPROVED" : "ANTIFRAUD_REJECTED")
                        .responseMessage(aprobado ? "Aprobado (simulado)" : "Rechazado (simulado)")
                        .operationDate(System.currentTimeMillis())
                        .build())
                .build();
    }
}