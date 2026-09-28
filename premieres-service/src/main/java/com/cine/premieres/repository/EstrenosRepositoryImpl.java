package com.cine.premieres.repository;


import com.cine.premieres.entity.Estrenos;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class EstrenosRepositoryImpl implements EstrenosRepository {

    private static final String SQL_LISTAR_ACTIVOS = "SELECT * FROM premieres.fn_listar_estrenos()";

    private static final RowMapper<Estrenos> ESTRENO_ROW_MAPPER = (rs, numFila) -> Estrenos.builder()
            .id(rs.getObject("id", UUID.class))
            .titulo(rs.getString("titulo"))
            .descripcion(rs.getString("descripcion"))
            .urlImagen(rs.getString("url_imagen"))
            .fechaEstreno(rs.getObject("fecha_estreno", LocalDate.class))
            .build();

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Estrenos> listarActivos() {
        log.debug("Ejecutando {}", SQL_LISTAR_ACTIVOS);
        return jdbcTemplate.query(SQL_LISTAR_ACTIVOS, ESTRENO_ROW_MAPPER);
    }
}