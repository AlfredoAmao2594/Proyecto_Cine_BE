-- =============================================================================
-- 01_esquemas.sql
-- Un esquema por microservicio dentro de la base cine_db.
-- Ningún servicio lee el esquema de otro.
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS premieres;       -- premieres-service
CREATE SCHEMA IF NOT EXISTS candystore;       -- candystore-service
CREATE SCHEMA IF NOT EXISTS complete;        -- complete-service
CREATE SCHEMA IF NOT EXISTS auth;  -- auth-service (opcional)

COMMENT ON SCHEMA premieres      IS 'Estrenos que muestra la pantalla Home (premieres-service)';
COMMENT ON SCHEMA candystore      IS 'Productos de dulcería (candystore-service)';
COMMENT ON SCHEMA complete       IS 'Compras registradas y auditoría de PayU (complete-service)';
COMMENT ON SCHEMA auth IS 'Usuarios que iniciaron sesión con Google (auth-service, opcional)';
