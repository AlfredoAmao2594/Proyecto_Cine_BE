package com.cine.candystore.repository;

import com.cine.candystore.entity.Producto;

import java.util.List;
import java.util.UUID;

public interface ProductoRepository {

    List<Producto> listarActivos();
    List<Producto> buscarPorIds(List<UUID> ids);
}