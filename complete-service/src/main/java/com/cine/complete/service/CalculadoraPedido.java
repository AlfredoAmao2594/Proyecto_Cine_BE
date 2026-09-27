package com.cine.complete.service;


import com.cine.complete.client.candystore.CandyStoreClient;
import com.cine.complete.client.candystore.PrecioProducto;
import com.cine.complete.dto.ItemRequest;
import com.cine.complete.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CalculadoraPedido {

    private final CandyStoreClient candyStoreClient;

    public PedidoCalculado calcular(List<ItemRequest> items, String authorization) {
        Map<UUID, Integer> cantidades = items.stream().collect(Collectors.toMap(
                ItemRequest::getProductId, ItemRequest::getQuantity, Integer::sum, LinkedHashMap::new));

        Map<UUID, PrecioProducto> precios = candyStoreClient
                .obtenerPrecios(List.copyOf(cantidades.keySet()), authorization)
                .stream()
                .collect(Collectors.toMap(PrecioProducto::getId, Function.identity()));

        List<PedidoCalculado.Linea> lineas = cantidades.entrySet().stream().map(e -> {
            PrecioProducto producto = precios.get(e.getKey());
            if (producto == null) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Producto no disponible: " + e.getKey());
            }
            BigDecimal subtotal = producto.getPrice().multiply(BigDecimal.valueOf(e.getValue()));
            return new PedidoCalculado.Linea(e.getKey(), producto.getName(), e.getValue(), producto.getPrice(), subtotal);
        }).collect(Collectors.toList());

        BigDecimal total = lineas.stream()
                .map(PedidoCalculado.Linea::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        return new PedidoCalculado(lineas, total);
    }
}