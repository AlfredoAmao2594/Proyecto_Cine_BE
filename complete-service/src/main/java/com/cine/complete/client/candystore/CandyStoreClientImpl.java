package com.cine.complete.client.candystore;

import com.cine.complete.dto.ApiResponse;
import com.cine.complete.exception.BusinessException;
import com.cine.complete.filter.TraceIdFilter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CandyStoreClientImpl implements CandyStoreClient {

    private static final ParameterizedTypeReference<ApiResponse<List<PrecioProducto>>> TIPO_RESPUESTA =
            new ParameterizedTypeReference<>() { };

    private final RestTemplate restTemplate;
    private final String candystoreUrl;

    public CandyStoreClientImpl(RestTemplate restTemplate, @Value("${candystore.url}") String candystoreUrl) {
        this.restTemplate = restTemplate;
        this.candystoreUrl = candystoreUrl;
    }

    @Override
    public List<PrecioProducto> obtenerPrecios(List<UUID> ids, String authorization) {
        URI uri = UriComponentsBuilder.fromHttpUrl(candystoreUrl)
                .path("/api/candystore/precios")
                .queryParam("ids", ids.stream().map(UUID::toString).collect(Collectors.joining(",")))
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorization);
        String traceId = MDC.get("traceId");
        if (traceId != null) {
            headers.set(TraceIdFilter.HEADER, traceId);   // mismo traceId en los logs de ambos servicios
        }

        try {
            log.info("Consultando precios en candystore: {} productos", ids.size());
            ApiResponse<List<PrecioProducto>> respuesta = restTemplate
                    .exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), TIPO_RESPUESTA)
                    .getBody();
            return respuesta.getData();
        } catch (HttpClientErrorException.NotFound e) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Uno o más productos ya no están disponibles");
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Token inválido para consultar la dulcería");
        } catch (RestClientException e) {
            log.error("candystore-service no respondió: {}", e.getMessage());
            throw new BusinessException(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de dulcería no está disponible");
        }
    }
}