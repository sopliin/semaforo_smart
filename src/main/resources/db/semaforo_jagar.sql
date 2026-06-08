-- =========================================================
-- Semaforo Smart (JAGAR) - Esquema de base de datos
-- (v1.0 - alcance MVP y demo de concurso, 07/06/2026)
--
-- Modelo conceptual (secciones 3 y 9 del documento):
--   interseccion 1--N nodo (tipo: ojo | cerebro)
--   nodo (ojo)   1--N deteccion
--   interseccion 1--N evento_alerta  (tecnica | vial | infraestructura)
--
-- Nombre de BD alineado con application.properties (spring.datasource.url)
-- =========================================================

CREATE DATABASE IF NOT EXISTS semaforo_smart;
USE semaforo_smart;

-- =========================================================
-- TABLA: usuarios
-- Operadores/administradores con acceso al panel de gestion
-- =========================================================
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol ENUM('admin', 'operador') NOT NULL DEFAULT 'operador',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    ultimo_acceso DATETIME NULL,
    creado_el TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================================================
-- TABLA: intersecciones
-- Cruce registrado en la plataforma (secciones 5.2 y 9.1)
-- =========================================================
CREATE TABLE intersecciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    calle_principal VARCHAR(120) NOT NULL,
    calle_secundaria VARCHAR(120) NOT NULL,
    zona_distrito VARCHAR(120),
    estado ENUM('operativo', 'alerta') NOT NULL DEFAULT 'operativo',
    ultima_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    creado_el TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_el TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =========================================================
-- TABLA: nodos
-- Nodo ojo (sensado/camara) o nodo cerebro (integracion) de una interseccion
-- (secciones 5.4 y 9.2). Los campos exclusivos de un tipo de nodo quedan
-- NULL en el otro (p. ej. camara_estado/fps/latencia_ms solo aplican a "ojo").
-- =========================================================
CREATE TABLE nodos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_interseccion INT NOT NULL,
    tipo ENUM('ojo', 'cerebro') NOT NULL,
    nombre VARCHAR(120) NOT NULL,
    ip VARCHAR(45),
    estado_conexion ENUM('online', 'offline', 'degradado') NOT NULL DEFAULT 'offline',
    temperatura_c DECIMAL(5,2),
    cpu_pct DECIMAL(5,2),
    ram_pct DECIMAL(5,2),
    almacenamiento_libre_mb INT,
    camara_estado ENUM('active', 'no_signal') NULL,
    fps DECIMAL(5,2) NULL,
    latencia_ms INT NULL,
    version_software VARCHAR(40),
    reinicios_recientes INT NOT NULL DEFAULT 0,
    ultimo_heartbeat DATETIME,
    creado_el TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_interseccion) REFERENCES intersecciones(id) ON DELETE CASCADE
);

-- =========================================================
-- TABLA: detecciones
-- Registro de objetos detectados por un nodo ojo (secciones 6.1 y 9.3).
-- No se calcula velocidad vehicular en esta version (seccion 6.2: "No velocidad").
-- =========================================================
CREATE TABLE detecciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_nodo INT NOT NULL,
    id_interseccion INT NOT NULL,
    ts_deteccion DATETIME DEFAULT CURRENT_TIMESTAMP,
    clase ENUM('vehiculo', 'peaton') NOT NULL,
    conteo_intervalo INT DEFAULT 1,
    FOREIGN KEY (id_nodo) REFERENCES nodos(id) ON DELETE CASCADE,
    FOREIGN KEY (id_interseccion) REFERENCES intersecciones(id) ON DELETE CASCADE
);

-- =========================================================
-- TABLA: eventos_alertas
-- Eventos derivados y alertas tecnicas / viales / de infraestructura semaforica
-- (secciones 5.6, 7 y 9.4). Flujo de estados: pendiente -> en_revision ->
-- notificada / resuelta / descartada.
-- =========================================================
CREATE TABLE eventos_alertas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo ENUM('tecnica', 'vial', 'infraestructura') NOT NULL,
    subtipo VARCHAR(60) NOT NULL,
    severidad ENUM('baja', 'media', 'alta', 'critica') NOT NULL DEFAULT 'media',
    estado ENUM('pendiente', 'en_revision', 'notificada', 'resuelta', 'descartada') NOT NULL DEFAULT 'pendiente',
    id_interseccion INT NOT NULL,
    id_nodo INT NULL,
    valor_metrica VARCHAR(60),
    ts_inicio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ts_fin DATETIME NULL,
    observacion_operador TEXT,
    creado_el TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_interseccion) REFERENCES intersecciones(id) ON DELETE CASCADE,
    FOREIGN KEY (id_nodo) REFERENCES nodos(id) ON DELETE SET NULL
);

-- =========================================================
-- INDICES PARA OPTIMIZACION
-- =========================================================
CREATE INDEX idx_nodos_interseccion       ON nodos(id_interseccion);
CREATE INDEX idx_nodos_estado_conexion    ON nodos(estado_conexion);
CREATE INDEX idx_detecciones_ts           ON detecciones(ts_deteccion);
CREATE INDEX idx_detecciones_nodo         ON detecciones(id_nodo);
CREATE INDEX idx_detecciones_interseccion ON detecciones(id_interseccion);
CREATE INDEX idx_alertas_estado           ON eventos_alertas(estado);
CREATE INDEX idx_alertas_interseccion     ON eventos_alertas(id_interseccion);

-- =========================================================
-- DATOS DE EJEMPLO
-- =========================================================

-- Usuario administrador (contrasena en texto plano: jagaritos123)
INSERT INTO usuarios (nombre, correo, password_hash, rol)
VALUES ('Administrador JAGAR', 'admin@jagar.org',
        '$2a$10$2wJ3zFgBFYZJi.P6jQJZAekO7TiqrSPwCWeSv380IrBZPBM97PvJW', 'admin');

-- Intersecciones registradas (ids 1, 2, 3)
INSERT INTO intersecciones (nombre, calle_principal, calle_secundaria, zona_distrito, estado) VALUES
('Av. Universitaria x Av. Morales Duarez', 'Av. Universitaria', 'Av. Morales Duarez', 'Lima - San Miguel', 'operativo'),
('Av. Argentina x Av. Colonial',           'Av. Argentina',     'Av. Colonial',       'Lima - Cercado',    'alerta'),
('Av. Brasil x Jr. Cusco',                 'Av. Brasil',        'Jr. Cusco',          'Lima - Brena',      'alerta');

-- Nodos cerebro: uno por interseccion (ids 1, 2, 3)
INSERT INTO nodos (id_interseccion, tipo, nombre, ip, estado_conexion, temperatura_c, cpu_pct, ram_pct,
                   almacenamiento_libre_mb, version_software, reinicios_recientes, ultimo_heartbeat) VALUES
(1, 'cerebro', 'Cerebro Universitaria', '192.168.1.10', 'online',    52.10, 38.00, 55.00, 18000, 'v0.3.1', 0, '2026-06-07 10:05:00'),
(2, 'cerebro', 'Cerebro Argentina',     '192.168.1.30', 'degradado', 61.40, 47.00, 70.00, 9000,  'v0.3.1', 1, '2026-06-07 09:58:00'),
(3, 'cerebro', 'Cerebro Brasil',        '192.168.1.50', 'online',    55.80, 41.00, 60.00, 15000, 'v0.3.0', 0, '2026-06-07 10:04:00');

-- Nodos ojo: tres por interseccion (ids 4-12)
INSERT INTO nodos (id_interseccion, tipo, nombre, ip, estado_conexion, temperatura_c, cpu_pct, ram_pct,
                   almacenamiento_libre_mb, camara_estado, fps, latencia_ms, version_software, reinicios_recientes, ultimo_heartbeat) VALUES
(1, 'ojo', 'Ojo Norte',    '192.168.1.11', 'online',    58.40, 42.00, 63.00, 12000, 'active',    12.50, 8,    'v0.3.1', 0, '2026-06-07 10:05:00'),
(1, 'ojo', 'Ojo Sur',      '192.168.1.12', 'online',    56.20, 39.00, 60.00, 12500, 'active',    13.10, 9,    'v0.3.1', 0, '2026-06-07 10:05:00'),
(1, 'ojo', 'Ojo Este',     '192.168.1.13', 'offline',   NULL,  NULL,  NULL,  NULL,  'no_signal', NULL,  NULL, 'v0.3.1', 2, '2026-06-07 09:58:00'),
(2, 'ojo', 'Ojo Oeste',    '192.168.1.31', 'online',    60.10, 50.00, 66.00, 10000, 'active',    11.80, 10,   'v0.3.0', 0, '2026-06-07 10:03:00'),
(2, 'ojo', 'Ojo Central',  '192.168.1.32', 'degradado', 74.60, 68.00, 80.00, 4000,  'active',    6.20,  35,   'v0.3.0', 1, '2026-06-07 10:01:00'),
(2, 'ojo', 'Ojo Acceso',   '192.168.1.33', 'online',    59.30, 44.00, 58.00, 11000, 'active',    12.90, 9,    'v0.3.0', 0, '2026-06-07 10:04:00'),
(3, 'ojo', 'Ojo Norte 2',  '192.168.1.51', 'online',    57.70, 40.00, 57.00, 13000, 'active',    12.20, 7,    'v0.3.1', 0, '2026-06-07 10:05:00'),
(3, 'ojo', 'Ojo Sur 2',    '192.168.1.52', 'online',    58.90, 43.00, 61.00, 12200, 'active',    12.60, 8,    'v0.3.1', 0, '2026-06-07 10:04:00'),
(3, 'ojo', 'Ojo Peatonal', '192.168.1.53', 'online',    56.50, 38.00, 55.00, 14000, 'active',    13.40, 6,    'v0.3.1', 0, '2026-06-07 10:05:00');

-- Detecciones recientes capturadas por nodos ojo (clase: vehiculo | peaton)
INSERT INTO detecciones (id_nodo, id_interseccion, ts_deteccion, clase, conteo_intervalo) VALUES
(4,  1, '2026-06-07 10:00:00', 'vehiculo', 5),
(4,  1, '2026-06-07 10:00:00', 'peaton',   2),
(5,  1, '2026-06-07 10:01:00', 'vehiculo', 4),
(7,  2, '2026-06-07 10:02:00', 'vehiculo', 7),
(9,  2, '2026-06-07 10:03:00', 'peaton',   3),
(10, 3, '2026-06-07 10:04:00', 'vehiculo', 6),
(12, 3, '2026-06-07 10:05:00', 'peaton',   4);

-- Eventos derivados / alertas (tecnicas, viales e infraestructura)
INSERT INTO eventos_alertas (tipo, subtipo, severidad, estado, id_interseccion, id_nodo, valor_metrica, ts_inicio, observacion_operador) VALUES
('vial',    'congestion_prolongada', 'alta',  'pendiente',   3, NULL, 'ocupacion=78%',           '2026-06-07 10:04:00', NULL),
('tecnica', 'nodo_ojo_desconectado', 'media', 'pendiente',   1, 6,    'estado_conexion=offline', '2026-06-07 09:58:00', NULL),
('vial',    'cruce_bloqueado',       'alta',  'en_revision', 2, 8,    'permanencia=18s',         '2026-06-07 09:40:00', 'Operador validando con camara de Ojo Central');

-- =========================================================
-- CONSULTAS UTILES PARA EL DASHBOARD
-- =========================================================

-- Resumen de red para las tarjetas de la pantalla de Inicio (seccion 5.1)
SELECT
    (SELECT COUNT(*) FROM intersecciones)                                              AS total_intersecciones,
    (SELECT COUNT(*) FROM nodos WHERE tipo = 'ojo')                                    AS total_nodos_ojo,
    (SELECT COUNT(*) FROM nodos WHERE tipo = 'ojo' AND estado_conexion = 'online')     AS nodos_ojo_activos,
    (SELECT COUNT(*) FROM nodos WHERE tipo = 'cerebro')                                AS total_nodos_cerebro,
    (SELECT COUNT(*) FROM nodos WHERE tipo = 'cerebro' AND estado_conexion = 'online') AS nodos_cerebro_activos,
    (SELECT COUNT(*) FROM eventos_alertas WHERE estado = 'pendiente')                  AS alertas_pendientes;

-- Tabla resumen de intersecciones: nombre, zona, ojos, cerebro, estado (seccion 5.2)
SELECT
    i.id,
    i.nombre,
    i.zona_distrito,
    SUM(n.tipo = 'ojo')     AS nodos_ojo,
    SUM(n.tipo = 'cerebro') AS nodos_cerebro,
    i.estado
FROM intersecciones i
LEFT JOIN nodos n ON n.id_interseccion = i.id
GROUP BY i.id, i.nombre, i.zona_distrito, i.estado;

-- Diagnostico de nodos: conexion, temperatura, IP y ultimo heartbeat (seccion 5.4)
SELECT
    n.id,
    n.nombre,
    n.tipo,
    i.nombre AS interseccion,
    n.ip,
    n.estado_conexion,
    n.temperatura_c,
    n.ultimo_heartbeat
FROM nodos n
JOIN intersecciones i ON i.id = n.id_interseccion
ORDER BY n.estado_conexion DESC, n.ultimo_heartbeat DESC;

-- Conteo de detecciones por clase para el panel de metricas directas (seccion 6.1)
SELECT
    clase,
    SUM(conteo_intervalo) AS total
FROM detecciones
GROUP BY clase;

-- Historico: ultimas detecciones por interseccion / nodo / fecha (seccion 5.5)
SELECT
    d.ts_deteccion,
    i.nombre AS interseccion,
    n.nombre AS nodo,
    d.clase,
    d.conteo_intervalo
FROM detecciones d
JOIN nodos n          ON n.id = d.id_nodo
JOIN intersecciones i ON i.id = d.id_interseccion
ORDER BY d.ts_deteccion DESC
LIMIT 20;

-- Alertas pendientes / en revision para el panel de Alertas (secciones 5.6 y 7)
SELECT
    a.id,
    a.tipo,
    a.subtipo,
    a.severidad,
    a.estado,
    i.nombre AS interseccion,
    n.nombre AS nodo,
    a.ts_inicio
FROM eventos_alertas a
JOIN intersecciones i ON i.id = a.id_interseccion
LEFT JOIN nodos n     ON n.id = a.id_nodo
WHERE a.estado IN ('pendiente', 'en_revision')
ORDER BY a.ts_inicio DESC;
