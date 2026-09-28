package com.cine.complete.repository.impl;


import com.cine.complete.entity.LogPago;
import com.cine.complete.repository.LogPagoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LogPagoRepositoryImpl implements LogPagoRepository {

    private static final String SQL_REGISTRAR = "CALL complete.sp_registrar_log_pago(?, ?, ?, ?, ?)";
    private static final String SQL_BUSCAR = "SELECT * FROM complete.fn_buscar_pago(?)";

    private static final RowMapper<LogPago> LOG_PAGO_ROW_MAPPER = (rs, numFila) -> LogPago.builder()
            .codigoReferencia(rs.getString("codigo_referencia"))
            .estado(rs.getString("estado"))
            .codigoRespuesta(rs.getString("codigo_respuesta"))
            .idTransaccion(rs.getString("id_transaccion"))
            .monto(rs.getBigDecimal("monto"))
            .fechaCreacion(rs.getObject("fecha_creacion", LocalDateTime.class))
            .build();

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void registrar(LogPago logPago) {
        jdbcTemplate.update(SQL_REGISTRAR,
                logPago.getCodigoReferencia(),
                logPago.getEstado(),
                logPago.getCodigoRespuesta(),
                logPago.getIdTransaccion(),
                logPago.getMonto());
    }

    @Override
    public Optional<LogPago> buscarPorTransaccion(String idTransaccion) {
        return jdbcTemplate.query(SQL_BUSCAR, LOG_PAGO_ROW_MAPPER, idTransaccion)
                .stream()
                .findFirst();
    }
}