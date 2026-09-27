package com.cine.complete.repository;

import com.cine.complete.entity.Compra;
import com.cine.complete.entity.CompraDetalle;

public interface CompraRepository {

    ResultadoRegistroCompra registrarCompra(Compra compra);

    void registrarDetalle(CompraDetalle detalle);
}