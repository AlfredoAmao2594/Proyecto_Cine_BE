CREATE TABLE IF NOT EXISTS premieres.estreno (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    titulo          VARCHAR(150)  NOT NULL,
    descripcion     VARCHAR(1000),
    url_imagen      VARCHAR(500)  NOT NULL,
    fecha_estreno   DATE,
    activo          BOOLEAN       NOT NULL DEFAULT TRUE,
    fecha_creacion  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS ix_estreno_activo_fecha
    ON premieres.estreno (activo, fecha_estreno DESC);

CREATE TABLE IF NOT EXISTS candystore.producto (
    id           UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre       VARCHAR(100)   NOT NULL,
    descripcion  VARCHAR(300),
    precio       NUMERIC(10,2)  NOT NULL,
    url_imagen   VARCHAR(500),
    categoria    VARCHAR(30)    NOT NULL,
    activo       BOOLEAN        NOT NULL DEFAULT TRUE,
    CONSTRAINT ck_producto_precio    CHECK (precio > 0),
    CONSTRAINT ck_producto_categoria CHECK (categoria IN ('COMBO', 'BEBIDA', 'SNACK'))
);

CREATE TABLE IF NOT EXISTS complete.compra (
    id                UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    correo            VARCHAR(150)   NOT NULL,
    nombre_completo   VARCHAR(150)   NOT NULL,
    tipo_documento    VARCHAR(10)    NOT NULL DEFAULT 'DNI',
    numero_documento  VARCHAR(20)    NOT NULL,
    id_transaccion    VARCHAR(36)    NOT NULL,
    id_orden_payu     BIGINT,
    fecha_operacion   TIMESTAMP      NOT NULL,
    monto_total       NUMERIC(10,2),
    estado            VARCHAR(20)    NOT NULL DEFAULT 'COMPLETADA',
    fecha_creacion    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_compra_id_transaccion UNIQUE (id_transaccion),
    CONSTRAINT ck_compra_tipo_documento CHECK (tipo_documento IN ('DNI', 'CE', 'PASAPORTE')),
    CONSTRAINT ck_compra_monto_total    CHECK (monto_total IS NULL OR monto_total >= 0)
);

CREATE INDEX IF NOT EXISTS ix_compra_correo ON complete.compra (correo);

CREATE TABLE IF NOT EXISTS complete.compra_detalle (
    id               UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    id_compra        UUID           NOT NULL,
    id_producto      UUID           NOT NULL,
    nombre_producto  VARCHAR(100)   NOT NULL,
    cantidad         INTEGER        NOT NULL,
    precio_unitario  NUMERIC(10,2)  NOT NULL,
    subtotal         NUMERIC(10,2)  GENERATED ALWAYS AS (cantidad * precio_unitario) STORED,
    CONSTRAINT fk_detalle_compra FOREIGN KEY (id_compra)
        REFERENCES complete.compra (id) ON DELETE CASCADE,
    CONSTRAINT ck_detalle_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_detalle_precio   CHECK (precio_unitario > 0)
);

CREATE INDEX IF NOT EXISTS ix_detalle_id_compra ON complete.compra_detalle (id_compra);

CREATE TABLE IF NOT EXISTS complete.log_pago (
    id                 UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo_referencia  VARCHAR(100)  NOT NULL,
    estado             VARCHAR(20)   NOT NULL,
    codigo_respuesta   VARCHAR(64),
    id_transaccion     VARCHAR(36),
    monto              NUMERIC(10,2),
    fecha_creacion     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_log_pago_estado CHECK (estado IN ('APPROVED', 'DECLINED', 'PENDING', 'ERROR'))
);

CREATE INDEX IF NOT EXISTS ix_log_pago_referencia ON complete.log_pago (codigo_referencia);

CREATE TABLE IF NOT EXISTS auth.usuario (
    id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    correo          VARCHAR(150)  NOT NULL,
    nombre          VARCHAR(150)  NOT NULL,
    rol             VARCHAR(10)   NOT NULL DEFAULT 'USER',
    ultimo_acceso   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_creacion  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_usuario_correo UNIQUE (correo),
    CONSTRAINT ck_usuario_rol    CHECK (rol IN ('USER', 'GUEST'))
);
