package com.cine.candystore.repository.impl;

import com.cine.candystore.entity.Producto;
import com.cine.candystore.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProductoRepositoryImpl implements ProductoRepository {

    private static final String SQL_LISTAR_ACTIVOS = "SELECT * FROM candystore.fn_listar_productos()";
    private static final String SQL_BUSCAR_POR_IDS = "SELECT * FROM candystore.fn_obtener_productos_por_ids(?::uuid[])";

    private static final RowMapper<Producto> PRODUCTO_ROW_MAPPER = (rs, numFila) -> Producto.builder()
            .id(rs.getObject("id", UUID.class))
            .nombre(rs.getString("nombre"))
            .descripcion(rs.getString("descripcion"))
            .precio(rs.getBigDecimal("precio"))
            .urlImagen(rs.getString("url_imagen"))
            .categoria(rs.getString("categoria"))
            .build();

    private static final RowMapper<Producto> PRECIO_ROW_MAPPER = (rs, numFila) -> Producto.builder()
            .id(rs.getObject("id", UUID.class))
            .nombre(rs.getString("nombre"))
            .precio(rs.getBigDecimal("precio"))
            .build();

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Producto> listarActivos() {
        log.debug("Ejecutando {}", SQL_LISTAR_ACTIVOS);
        return jdbcTemplate.query(SQL_LISTAR_ACTIVOS, PRODUCTO_ROW_MAPPER);
    }

    @Override
    public List<Producto> buscarPorIds(List<UUID> ids) {
        log.debug("Ejecutando {} con {} ids", SQL_BUSCAR_POR_IDS, ids.size());
        // Un arreglo de PostgreSQL se envía con createArrayOf("uuid", ...)
        return jdbcTemplate.query(SQL_BUSCAR_POR_IDS,
                ps -> ps.setArray(1, ps.getConnection().createArrayOf("uuid", ids.toArray())),
                PRECIO_ROW_MAPPER);
    }
}