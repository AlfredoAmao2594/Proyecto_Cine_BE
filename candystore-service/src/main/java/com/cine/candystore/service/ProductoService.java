package com.cine.candystore.service;

import com.cine.candystore.dto.PrecioProductoResponse;
import com.cine.candystore.dto.ProductoResponse;

import java.util.List;
import java.util.UUID;

public interface ProductoService {

    List<ProductoResponse> listarProductos();

    List<PrecioProductoResponse> obtenerPrecios(List<UUID> ids);
}