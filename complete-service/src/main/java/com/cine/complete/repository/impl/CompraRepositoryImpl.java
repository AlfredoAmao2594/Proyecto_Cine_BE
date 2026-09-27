package com.cine.complete.repository.impl;


import com.cine.complete.entity.Compra;
import com.cine.complete.entity.CompraDetalle;
import com.cine.complete.repository.CompraRepository;
import com.cine.complete.repository.ResultadoRegistroCompra;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CompraRepositoryImpl implements CompraRepository {

    private static final String SQL_REGISTRAR_COMPRA =
            "CALL compras.sp_registrar_compra(?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL, NULL)";

    private static final String SQL_REGISTRAR_DETALLE =
            "CALL compras.sp_registrar_compra_detalle(?, ?, ?, ?, ?)";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public ResultadoRegistroCompra registrarCompra(Compra compra) {
        log.debug("Ejecutando {}", SQL_REGISTRAR_COMPRA);
        // En PostgreSQL, CALL devuelve los parámetros OUT como UNA fila: se lee con queryForMap
        Map<String, Object> out = jdbcTemplate.queryForMap(SQL_REGISTRAR_COMPRA,
                compra.getCorreo(),
                compra.getNombreCompleto(),
                compra.getTipoDocumento(),
                compra.getNumeroDocumento(),
                compra.getIdTransaccion(),
                new SqlParameterValue(Types.BIGINT, compra.getIdOrdenPayu()),   // puede ser null
                Timestamp.valueOf(compra.getFechaOperacion()),
                compra.getMontoTotal());

        return new ResultadoRegistroCompra(
                (UUID) out.get("p_id_compra"),
                (String) out.get("p_codigo"),
                (String) out.get("p_mensaje"));
    }

    @Override
    public void registrarDetalle(CompraDetalle detalle) {
        jdbcTemplate.update(SQL_REGISTRAR_DETALLE,
                detalle.getIdCompra(),
                detalle.getIdProducto(),
                detalle.getNombreProducto(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario());
    }
}