#!/bin/bash
# =============================================================================
# 05_usuario_app.sh
# Crea el usuario de aplicación (los microservicios NO usan el superusuario).
# Es .sh y no .sql porque lee la clave desde variables de entorno:
#   APP_DB_USER      (por defecto: cine_app)
#   APP_DB_PASSWORD  (obligatoria)
# =============================================================================
set -e

APP_DB_USER="${APP_DB_USER:-cine_app}"

if [ -z "${APP_DB_PASSWORD}" ]; then
  echo "ERROR: falta la variable APP_DB_PASSWORD" >&2
  exit 1
fi

psql -v ON_ERROR_STOP=1 \
     --username "$POSTGRES_USER" \
     --dbname "$POSTGRES_DB" \
     -v app_user="$APP_DB_USER" \
     -v app_pass="$APP_DB_PASSWORD" <<-'EOSQL'
  CREATE ROLE :"app_user" WITH LOGIN PASSWORD :'app_pass';

  REVOKE ALL ON SCHEMA public FROM PUBLIC;
  GRANT CONNECT ON DATABASE :"DBNAME" TO :"app_user";

  GRANT USAGE ON SCHEMA premieres, candystore, complete, auth TO :"app_user";

  GRANT SELECT ON ALL TABLES IN SCHEMA premieres, candystore TO :"app_user";
  GRANT SELECT, INSERT ON ALL TABLES IN SCHEMA complete, auth TO :"app_user";
  GRANT UPDATE (nombre, ultimo_acceso) ON auth.usuario TO :"app_user";

  GRANT EXECUTE ON ALL FUNCTIONS  IN SCHEMA premieres, candystore, complete, auth TO :"app_user";
  GRANT EXECUTE ON ALL PROCEDURES IN SCHEMA premieres, candystore, complete, auth TO :"app_user";
EOSQL

echo "Usuario de aplicación '${APP_DB_USER}' creado con permisos SELECT, INSERT y EXECUTE."
