package com.cine.complete.client.payu;


import com.cine.complete.config.PayuProperties;
import com.cine.complete.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "payu.mock", havingValue = "false", matchIfMissing = true)
public class PayuHttpClient implements PayuClient {

    private final RestTemplate restTemplate;
    private final PayuProperties payuProperties;

    @Override
    public PayuResponse enviarPago(PayuRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Sin Accept: application/json, PayU responde en XML
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        try {
            log.info("Enviando pago a PayU: {}", request.getTransaction().getOrder().getReferenceCode());
            return restTemplate.postForObject(payuProperties.getUrl(), new HttpEntity<>(request, headers),
                    PayuResponse.class);
        } catch (RestClientException e) {
            log.error("Error al comunicarse con PayU: {}", e.getMessage());
            throw new BusinessException(HttpStatus.BAD_GATEWAY, "No se pudo conectar con la pasarela de pagos");
        }
    }
}