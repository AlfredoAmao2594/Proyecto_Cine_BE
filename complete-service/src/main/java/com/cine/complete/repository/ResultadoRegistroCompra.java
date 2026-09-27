package com.cine.complete.repository;

import lombok.Value;

import java.util.UUID;

@Value
public class ResultadoRegistroCompra {

    public static final String REGISTRADA = "0";
    public static final String DUPLICADA = "1";
    public static final String DATOS_INVALIDOS = "2";

    UUID idCompra;
    String codigo;
    String mensaje;
}