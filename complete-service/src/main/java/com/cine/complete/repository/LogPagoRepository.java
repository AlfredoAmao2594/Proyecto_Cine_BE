package com.cine.complete.repository;

import com.cine.complete.entity.LogPago;

import java.util.Optional;

public interface LogPagoRepository {

    void registrar(LogPago logPago);
    Optional<LogPago> buscarPorTransaccion(String idTransaccion);
}