package com.cine.complete.service;

import lombok.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Value
public class PedidoCalculado {

    List<Linea> lineas;
    BigDecimal total;

    @Value
    public static class Linea {
        UUID productId;
        String nombre;
        int cantidad;
        BigDecimal precioUnitario;
        BigDecimal subtotal;
    }
}