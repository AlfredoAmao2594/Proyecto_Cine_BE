# Base de datos (PostgreSQL)

Scripts que crean la base `cine_db` desde cero. Se ejecutan **en orden**:

| Archivo | Qué hace |
|---|---|
| `01_esquemas.sql` | Crea los esquemas `premieres`, `candystore`, `complete` y `auth` |
| `02_tablas.sql` | Crea las tablas (ids UUID) |
| `03_procedimientos.sql` | Funciones y procedimientos almacenados que usan los servicios |
| `04_datos.sql` | Datos iniciales: estrenos (`a1000000-...`) y productos (`b2000000-...`) |
| `05_usuario_app.sh` | Crea el usuario `cine_app` con permisos mínimos (lo usa Docker) |

## Local (psql)

```bash
psql -U postgres -c "CREATE DATABASE cine_db"
psql -U postgres -d cine_db -f 01_esquemas.sql -f 02_tablas.sql -f 03_procedimientos.sql -f 04_datos.sql
```

## Docker

`docker-compose.yml` monta esta carpeta en `/docker-entrypoint-initdb.d`: PostgreSQL ejecuta
los scripts automáticamente **solo la primera vez** (cuando el volumen de datos está vacío).
Para volver a crear la base: `docker compose down -v && docker compose up -d`.

## Imágenes

La columna `url_imagen` guarda la ruta de la imagen, por ejemplo `/img/estrenos/moana-2.jpg`.
Los archivos van en el frontend: `public/img/estrenos/` y `public/img/dulceria/`.
