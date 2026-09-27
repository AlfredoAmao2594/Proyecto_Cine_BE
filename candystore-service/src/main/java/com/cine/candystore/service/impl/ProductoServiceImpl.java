package com.cine.candystore.service.impl;

import com.cine.candystore.dto.PrecioProductoResponse;
import com.cine.candystore.dto.ProductoResponse;
import com.cine.candystore.entity.Producto;
import com.cine.candystore.exception.BusinessException;
import com.cine.candystore.mapper.ProductoMapper;
import com.cine.candystore.repository.ProductoRepository;
import com.cine.candystore.service.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private static final int MAX_IDS = 50;

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarProductos() {
        log.info("Consultando productos de dulcería");
        List<Producto> productos = productoRepository.listarActivos();
        log.info("Se encontraron {} productos", productos.size());
        return productoMapper.toResponseList(productos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrecioProductoResponse> obtenerPrecios(List<UUID> ids) {
        List<UUID> idsUnicos = ids.stream().distinct().collect(Collectors.toList());
        if (idsUnicos.isEmpty() || idsUnicos.size() > MAX_IDS) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Debe enviar entre 1 y " + MAX_IDS + " productos");
        }

        log.info("Consultando precios de {} productos", idsUnicos.size());
        List<Producto> productos = productoRepository.buscarPorIds(idsUnicos);

        Set<UUID> encontrados = productos.stream().map(Producto::getId).collect(Collectors.toSet());
        List<UUID> faltantes = idsUnicos.stream().filter(id -> !encontrados.contains(id)).collect(Collectors.toList());
        if (!faltantes.isEmpty()) {
            log.warn("Productos no disponibles: {}", faltantes);
            throw new BusinessException(HttpStatus.NOT_FOUND, "Productos no disponibles: " + faltantes);
        }

        return productoMapper.toPrecioResponseList(productos);
    }
}