-- =============================================================================
-- 04_datos.sql
-- Datos semilla con UUID fijos para poder probar en Postman y DBeaver
-- siempre con los mismos ids. Estrenos: a1...  Productos: b2...
-- Las imágenes se sirven desde frontend/public/img.
-- =============================================================================

INSERT INTO premieres.estreno (id, titulo, descripcion, url_imagen, fecha_estreno) VALUES
('a1000000-0000-4000-8000-000000000001', 'Dune: Parte Dos',
 'Paul Atreides se une a Chani y a los Fremen en una guerra de venganza contra quienes destruyeron a su familia, mientras intenta evitar un futuro terrible que solo él puede prever.',
 '/img/estrenos/dune-parte-dos.jpg', '2026-09-03'),
('a1000000-0000-4000-8000-000000000002', 'Intensamente 2',
 'Riley entra en la adolescencia y el cuartel general de su mente se remodela para dar paso a nuevas emociones, entre ellas Ansiedad, que no piensa quedarse al margen.',
 '/img/estrenos/intensamente-2.jpg', '2026-09-10'),
('a1000000-0000-4000-8000-000000000003', 'Gladiador II',
 'Años después de la muerte de Máximo, Lucio se ve obligado a entrar al Coliseo cuando su hogar es conquistado por los tiránicos emperadores que gobiernan Roma.',
 '/img/estrenos/gladiador-2.jpg', '2026-09-17'),
('a1000000-0000-4000-8000-000000000004', 'Moana 2',
 'Moana recibe una llamada inesperada de sus antepasados y emprende un viaje hacia los lejanos mares de Oceanía junto a una tripulación poco común.',
 '/img/estrenos/moana-2.jpg', '2026-09-24'),
('a1000000-0000-4000-8000-000000000005', 'Deadpool y Wolverine',
 'Deadpool es reclutado por la Autoridad de Variación Temporal y debe convencer a un Wolverine muy poco dispuesto para salvar su universo.',
 '/img/estrenos/deadpool-wolverine.jpg', '2026-10-01'),
('a1000000-0000-4000-8000-000000000006', 'Robot Salvaje',
 'La robot ROZZUM 7134 naufraga en una isla deshabitada y debe aprender a adaptarse al entorno, creando lazos con los animales y adoptando a un gansito huérfano.',
 '/img/estrenos/robot-salvaje.jpg', '2026-10-08');

INSERT INTO candystore.producto (id, nombre, descripcion, precio, url_imagen, categoria) VALUES
('b2000000-0000-4000-8000-000000000001', 'Combo Pareja',     '2 gaseosas medianas + 1 canchita grande salada',        32.50, '/img/dulceria/combo-pareja.png',     'COMBO'),
('b2000000-0000-4000-8000-000000000002', 'Combo Familiar',   '4 gaseosas medianas + 2 canchitas grandes + 1 nachos',  58.90, '/img/dulceria/combo-familiar.png',   'COMBO'),
('b2000000-0000-4000-8000-000000000003', 'Combo Individual', '1 gaseosa mediana + 1 canchita mediana',                19.90, '/img/dulceria/combo-individual.png', 'COMBO'),
('b2000000-0000-4000-8000-000000000004', 'Canchita Grande',  'Canchita salada tamaño grande',                         18.50, '/img/dulceria/canchita.png',         'SNACK'),
('b2000000-0000-4000-8000-000000000005', 'Canchita Dulce',   'Canchita acaramelada tamaño mediano',                   16.00, '/img/dulceria/canchita-dulce.png',   'SNACK'),
('b2000000-0000-4000-8000-000000000006', 'Nachos con Queso', 'Nachos crujientes con salsa de queso cheddar',          15.90, '/img/dulceria/nachos.png',           'SNACK'),
('b2000000-0000-4000-8000-000000000007', 'Hot Dog',          'Pan con salchicha de Huacho, papas al hilo y cremas',   12.50, '/img/dulceria/hot-dog.png',          'SNACK'),
('b2000000-0000-4000-8000-000000000008', 'Gaseosa Mediana',  'Gaseosa de 22 oz a elección',                           10.90, '/img/dulceria/gaseosa.png',          'BEBIDA'),
('b2000000-0000-4000-8000-000000000009', 'Agua Mineral',     'Agua sin gas de 625 ml',                                 6.50, '/img/dulceria/agua.png',             'BEBIDA');
