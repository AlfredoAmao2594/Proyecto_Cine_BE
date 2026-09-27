package com.cine.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class TraceIdGlobalFilter implements GlobalFilter, Ordered {

    public static final String HEADER = "X-Trace-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String recibido = exchange.getRequest().getHeaders().getFirst(HEADER);
        String traceId = (recibido == null || recibido.isBlank())
                ? UUID.randomUUID().toString().substring(0, 8)
                : recibido;

        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(HEADER, traceId)
                .build();

        exchange.getResponse().beforeCommit(() -> {
            exchange.getResponse().getHeaders().set(HEADER, traceId);
            return Mono.empty();
        });

        long inicio = System.currentTimeMillis();
        return chain.filter(exchange.mutate().request(request).build())
                .doFinally(senal -> log.info("[{}] {} {} -> {} ({} ms)",
                        traceId,
                        request.getMethod(),
                        request.getURI().getPath(),
                        exchange.getResponse().getStatusCode(),
                        System.currentTimeMillis() - inicio));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}