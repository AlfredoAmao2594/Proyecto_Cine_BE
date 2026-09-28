CREATE OR REPLACE FUNCTION premieres.fn_listar_estrenos()
RETURNS TABLE (
    id             UUID,
    titulo         VARCHAR,
    descripcion    VARCHAR,
    url_imagen     VARCHAR,
    fecha_estreno  DATE
)
LANGUAGE sql
STABLE
AS $$
    SELECT e.id, e.titulo, e.descripcion, e.url_imagen, e.fecha_estreno
      FROM premieres.estreno e
     WHERE e.activo
     ORDER BY e.fecha_estreno DESC NULLS LAST, e.titulo;
$$;


CREATE OR REPLACE FUNCTION candystore.fn_listar_productos()
RETURNS TABLE (
    id           UUID,
    nombre       VARCHAR,
    descripcion  VARCHAR,
    precio       NUMERIC,
    url_imagen   VARCHAR,
    categoria    VARCHAR
)
LANGUAGE sql
STABLE
AS $$
    SELECT p.id, p.nombre, p.descripcion, p.precio, p.url_imagen, p.categoria
      FROM candystore.producto p
     WHERE p.activo
     ORDER BY CASE p.categoria WHEN 'COMBO' THEN 1 WHEN 'SNACK' THEN 2 ELSE 3 END,
              p.nombre;
$$;

CREATE OR REPLACE FUNCTION candystore.fn_obtener_productos_por_ids(p_ids UUID[])
RETURNS TABLE (
    id      UUID,
    nombre  VARCHAR,
    precio  NUMERIC
)
LANGUAGE sql
STABLE
AS $$
    SELECT p.id, p.nombre, p.precio
      FROM candystore.producto p
     WHERE p.activo
       AND p.id = ANY (p_ids);
$$;

CREATE OR REPLACE PROCEDURE complete.sp_registrar_compra(
    IN  p_correo            VARCHAR,
    IN  p_nombre_completo   VARCHAR,
    IN  p_tipo_documento    VARCHAR,
    IN  p_numero_documento  VARCHAR,
    IN  p_id_transaccion    VARCHAR,
    IN  p_id_orden_payu     BIGINT,
    IN  p_fecha_operacion   TIMESTAMP,
    IN  p_monto_total       NUMERIC,
    OUT p_id_compra         UUID,
    OUT p_codigo            VARCHAR,
    OUT p_mensaje           VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
    IF COALESCE(TRIM(p_correo), '') = ''
       OR COALESCE(TRIM(p_nombre_completo), '') = ''
       OR COALESCE(TRIM(p_numero_documento), '') = ''
       OR COALESCE(TRIM(p_id_transaccion), '') = ''
       OR p_fecha_operacion IS NULL THEN
        p_codigo  := '2';
        p_mensaje := 'Datos incompletos para registrar la compra';
        RETURN;
    END IF;

    SELECT c.id INTO p_id_compra
      FROM complete.compra c
     WHERE c.id_transaccion = p_id_transaccion;

    IF FOUND THEN
        p_codigo  := '1';
        p_mensaje := 'La transacción ya fue registrada';
        RETURN;
    END IF;

    INSERT INTO complete.compra (
        correo, nombre_completo, tipo_documento, numero_documento,
        id_transaccion, id_orden_payu, fecha_operacion, monto_total, estado
    ) VALUES (
        LOWER(TRIM(p_correo)), TRIM(p_nombre_completo),
        UPPER(COALESCE(NULLIF(TRIM(p_tipo_documento), ''), 'DNI')),
        TRIM(p_numero_documento), p_id_transaccion, p_id_orden_payu,
        p_fecha_operacion, p_monto_total, 'COMPLETADA'
    )
    RETURNING id INTO p_id_compra;

    p_codigo  := '0';
    p_mensaje := 'Compra registrada';

EXCEPTION
    WHEN unique_violation THEN
        SELECT c.id INTO p_id_compra
          FROM complete.compra c
         WHERE c.id_transaccion = p_id_transaccion;
        p_codigo  := '1';
        p_mensaje := 'La transacción ya fue registrada';
    WHEN check_violation THEN
        p_id_compra := NULL;
        p_codigo    := '2';
        p_mensaje   := 'Datos inválidos: ' || SQLERRM;
END;
$$;

CREATE OR REPLACE PROCEDURE complete.sp_registrar_compra_detalle(
    IN p_id_compra        UUID,
    IN p_id_producto      UUID,
    IN p_nombre_producto  VARCHAR,
    IN p_cantidad         INTEGER,
    IN p_precio_unitario  NUMERIC
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO complete.compra_detalle (
        id_compra, id_producto, nombre_producto, cantidad, precio_unitario
    ) VALUES (
        p_id_compra, p_id_producto, p_nombre_producto, p_cantidad, p_precio_unitario
    );
END;
$$;

CREATE OR REPLACE PROCEDURE complete.sp_registrar_log_pago(
    IN p_codigo_referencia  VARCHAR,
    IN p_estado             VARCHAR,
    IN p_codigo_respuesta   VARCHAR,
    IN p_id_transaccion     VARCHAR,
    IN p_monto              NUMERIC
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO complete.log_pago (
        codigo_referencia, estado, codigo_respuesta, id_transaccion, monto
    ) VALUES (
        p_codigo_referencia, UPPER(p_estado), p_codigo_respuesta, p_id_transaccion, p_monto
    );
END;
$$;

CREATE OR REPLACE FUNCTION complete.fn_buscar_pago(p_id_transaccion VARCHAR)
RETURNS TABLE (
    codigo_referencia  VARCHAR,
    estado             VARCHAR,
    codigo_respuesta   VARCHAR,
    id_transaccion     VARCHAR,
    monto              NUMERIC,
    fecha_creacion     TIMESTAMP
)
LANGUAGE sql
STABLE
AS $$
    SELECT l.codigo_referencia, l.estado, l.codigo_respuesta, l.id_transaccion, l.monto, l.fecha_creacion
      FROM complete.log_pago l
     WHERE l.id_transaccion = p_id_transaccion
     ORDER BY l.fecha_creacion DESC
     LIMIT 1;
$$;

CREATE OR REPLACE PROCEDURE auth.sp_registrar_usuario(
    IN  p_correo      VARCHAR,
    IN  p_nombre      VARCHAR,
    OUT p_id_usuario  UUID
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO auth.usuario (correo, nombre)
    VALUES (LOWER(TRIM(p_correo)), TRIM(p_nombre))
    ON CONFLICT (correo) DO UPDATE
        SET nombre        = EXCLUDED.nombre,
            ultimo_acceso = CURRENT_TIMESTAMP
    RETURNING id INTO p_id_usuario;
END;
$$;
