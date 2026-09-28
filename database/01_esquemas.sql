CREATE SCHEMA IF NOT EXISTS premieres; 
CREATE SCHEMA IF NOT EXISTS candystore;
CREATE SCHEMA IF NOT EXISTS complete;  
CREATE SCHEMA IF NOT EXISTS auth;

COMMENT ON SCHEMA premieres      IS 'Estrenos que muestra la pantalla Home (premieres-service)';
COMMENT ON SCHEMA candystore      IS 'Productos de dulcería (candystore-service)';
COMMENT ON SCHEMA complete       IS 'Compras registradas y auditoría de PayU (complete-service)';
COMMENT ON SCHEMA auth IS 'Usuarios que iniciaron sesión con Google (auth-service, opcional)';
