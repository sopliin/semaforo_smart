-- =========================================================
-- Semaforo Smart (JAGAR) - Esquema de base de datos
-- v1.2 - MVP alineado con documento funcional
-- Actualizado: 08/06/2026
-- Documento asociado: Especificacion_Interfaz_SemaforoSmart_v1_2_Alineada.docx
--
-- Modelo conceptual:
--   interseccion 1--N nodo (tipo: ojo | cerebro)
--   nodo ojo 1--N deteccion
--   interseccion 1--N evento_alerta (tecnica | vial | infraestructura)
--
-- Decisiones MVP:
--   - Intersecciones solo usan estado: operativo | alerta.
--   - Detecciones NO guardan roi ni confianza en esta version.
--   - Notificaciones se manejan como estado del evento: estado = 'notificada'.
--   - No se agrega tabla de notificaciones para mantener el backend simple.
--   - No se mide velocidad vehicular.
--
-- Para una carga limpia en demo, descomenta la siguiente linea:
-- DROP DATABASE IF EXISTS semaforo_smart;
-- =========================================================

CREATE DATABASE IF NOT EXISTS semaforo_smart
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
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
-- Cruces registrados en la plataforma.
-- Estado simplificado para MVP:
--   operativo = funcionamiento normal
--   alerta    = requiere atencion por evento vial, tecnico o de infraestructura
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
-- Nodo ojo: modulo externo de sensado/camara.
-- Nodo cerebro: modulo principal de integracion/agregacion.
--
-- Campos como camara_estado, fps y latencia_ms aplican principalmente al nodo ojo.
-- Para nodo cerebro pueden quedar NULL.
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
                       actualizado_el TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       FOREIGN KEY (id_interseccion) REFERENCES intersecciones(id) ON DELETE CASCADE
);

-- =========================================================
-- TABLA: detecciones
-- Guarda detecciones agregadas por intervalo del nodo ojo.
--
-- Ejemplo: a las 10:00, Ojo Norte detecto 5 vehiculos en su intervalo.
-- No se guarda velocidad, roi ni confianza en el MVP.
-- =========================================================
CREATE TABLE detecciones (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             id_nodo INT NOT NULL,
                             id_interseccion INT NOT NULL,
                             ts_deteccion DATETIME DEFAULT CURRENT_TIMESTAMP,
                             clase ENUM('vehiculo', 'peaton') NOT NULL,
                             conteo_intervalo INT NOT NULL DEFAULT 1,
                             FOREIGN KEY (id_nodo) REFERENCES nodos(id) ON DELETE CASCADE,
                             FOREIGN KEY (id_interseccion) REFERENCES intersecciones(id) ON DELETE CASCADE
);

-- =========================================================
-- TABLA: eventos_alertas
-- Registra condiciones que requieren revision del operador.
--
-- tipo:
--   tecnica         = falla o degradacion del sistema
--   vial            = evento detectado en el cruce
--   infraestructura = posible anomalia en semaforo fisico o entorno semaforico
--
-- estado:
--   pendiente   = evento nuevo
--   en_revision = operador revisa el evento
--   notificada  = se notifico/genero reporte a operador o autoridad competente
--   resuelta    = evento atendido o normalizado
--   descartada  = falso positivo o evento no relevante
-- =========================================================
CREATE TABLE eventos_alertas (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 tipo ENUM('tecnica', 'vial', 'infraestructura') NOT NULL,
                                 subtipo VARCHAR(60) NOT NULL,
                                 severidad ENUM('baja', 'media', 'alta', 'critica') NOT NULL DEFAULT 'media',
                                 estado ENUM('pendiente', 'en_revision', 'notificada', 'resuelta', 'descartada') NOT NULL DEFAULT 'pendiente',
                                 id_interseccion INT NOT NULL,
                                 id_nodo INT NULL,
                                 descripcion VARCHAR(255),
                                 valor_metrica VARCHAR(60),
                                 ts_inicio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 ts_fin DATETIME NULL,
                                 observacion_operador TEXT,
                                 creado_el TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 actualizado_el TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 FOREIGN KEY (id_interseccion) REFERENCES intersecciones(id) ON DELETE CASCADE,
                                 FOREIGN KEY (id_nodo) REFERENCES nodos(id) ON DELETE SET NULL
);

-- =========================================================
-- INDICES PARA OPTIMIZACION
-- =========================================================
CREATE INDEX idx_nodos_interseccion       ON nodos(id_interseccion);
CREATE INDEX idx_nodos_tipo               ON nodos(tipo);
CREATE INDEX idx_nodos_estado_conexion    ON nodos(estado_conexion);
CREATE INDEX idx_detecciones_ts           ON detecciones(ts_deteccion);
CREATE INDEX idx_detecciones_nodo         ON detecciones(id_nodo);
CREATE INDEX idx_detecciones_interseccion ON detecciones(id_interseccion);
CREATE INDEX idx_alertas_estado           ON eventos_alertas(estado);
CREATE INDEX idx_alertas_tipo             ON eventos_alertas(tipo);
CREATE INDEX idx_alertas_interseccion     ON eventos_alertas(id_interseccion);
CREATE INDEX idx_alertas_ts_inicio        ON eventos_alertas(ts_inicio);

-- =========================================================
-- DATOS DE EJEMPLO
-- =========================================================

-- Usuario administrador
-- Password de referencia: jagaritos123
INSERT INTO usuarios (nombre, correo, password_hash, rol)
VALUES ('Administrador JAGAR', 'admin@jagar.org',
        '$2a$10$2wJ3zFgBFYZJi.P6jQJZAekO7TiqrSPwCWeSv380IrBZPBM97PvJW', 'admin');

-- Intersecciones registradas
INSERT INTO intersecciones (nombre, calle_principal, calle_secundaria, zona_distrito, estado) VALUES
                                                                                                  ('Av. Universitaria x Av. Morales Duarez', 'Av. Universitaria', 'Av. Morales Duarez', 'Lima - San Miguel', 'operativo'),
                                                                                                  ('Av. Argentina x Av. Colonial',           'Av. Argentina',     'Av. Colonial',       'Lima - Cercado',    'alerta'),
                                                                                                  ('Av. Brasil x Jr. Cusco',                 'Av. Brasil',        'Jr. Cusco',          'Lima - Brena',      'alerta');

-- Nodos cerebro: uno por interseccion
INSERT INTO nodos (id_interseccion, tipo, nombre, ip, estado_conexion, temperatura_c, cpu_pct, ram_pct,
                   almacenamiento_libre_mb, version_software, reinicios_recientes, ultimo_heartbeat) VALUES
                                                                                                         (1, 'cerebro', 'Cerebro Universitaria', '192.168.1.10', 'online',    52.10, 38.00, 55.00, 18000, 'v0.3.1', 0, '2026-06-07 10:05:00'),
                                                                                                         (2, 'cerebro', 'Cerebro Argentina',     '192.168.1.30', 'degradado', 61.40, 47.00, 70.00, 9000,  'v0.3.1', 1, '2026-06-07 09:58:00'),
                                                                                                         (3, 'cerebro', 'Cerebro Brasil',        '192.168.1.50', 'online',    55.80, 41.00, 60.00, 15000, 'v0.3.0', 0, '2026-06-07 10:04:00');

-- Nodos ojo: tres por interseccion
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

-- Detecciones recientes capturadas por nodos ojo.
-- Se guarda conteo por intervalo, no cada bounding box individual.
INSERT INTO detecciones (id_nodo, id_interseccion, ts_deteccion, clase, conteo_intervalo) VALUES
                                                                                              (4,  1, '2026-06-07 10:00:00', 'vehiculo', 5),
                                                                                              (4,  1, '2026-06-07 10:00:00', 'peaton',   2),
                                                                                              (5,  1, '2026-06-07 10:01:00', 'vehiculo', 4),
                                                                                              (7,  2, '2026-06-07 10:02:00', 'vehiculo', 7),
                                                                                              (9,  2, '2026-06-07 10:03:00', 'peaton',   3),
                                                                                              (10, 3, '2026-06-07 10:04:00', 'vehiculo', 6),
                                                                                              (12, 3, '2026-06-07 10:05:00', 'peaton',   4);

-- Eventos derivados / alertas.
-- Las notificaciones se gestionan cambiando estado a 'notificada'.
INSERT INTO eventos_alertas (tipo, subtipo, severidad, estado, id_interseccion, id_nodo,
                             descripcion, valor_metrica, ts_inicio, observacion_operador) VALUES
                                                                                              ('vial',    'congestion_prolongada', 'alta',  'pendiente',   3, NULL,
                                                                                               'Alta ocupacion vehicular detectada durante mas de 2 minutos.',
                                                                                               'ocupacion=78%', '2026-06-07 10:04:00', NULL),
                                                                                              ('tecnica', 'nodo_ojo_desconectado', 'media', 'pendiente',   1, 6,
                                                                                               'El nodo ojo dejo de reportar heartbeat dentro del tiempo esperado.',
                                                                                               'estado_conexion=offline', '2026-06-07 09:58:00', NULL),
                                                                                              ('vial',    'cruce_bloqueado',       'alta',  'en_revision', 2, 8,
                                                                                               'Vehiculo detectado en zona de conflicto por encima del umbral definido.',
                                                                                               'permanencia=18s', '2026-06-07 09:40:00', 'Operador validando con camara de Ojo Central'),
                                                                                              ('infraestructura', 'posible_luz_apagada', 'critica', 'pendiente', 3, NULL,
                                                                                               'Posible anomalia en la optica semaforica observada por el sistema.',
                                                                                               'estado_luminoso=no_visible', '2026-06-07 10:06:00', NULL);

-- =========================================================
-- CONSULTAS UTILES PARA EL DASHBOARD
-- =========================================================

-- Resumen de red para tarjetas de Inicio
SELECT
    (SELECT COUNT(*) FROM intersecciones)                                              AS total_intersecciones,
    (SELECT COUNT(*) FROM nodos WHERE tipo = 'ojo')                                    AS total_nodos_ojo,
    (SELECT COUNT(*) FROM nodos WHERE tipo = 'ojo' AND estado_conexion = 'online')     AS nodos_ojo_activos,
    (SELECT COUNT(*) FROM nodos WHERE tipo = 'cerebro')                                AS total_nodos_cerebro,
    (SELECT COUNT(*) FROM nodos WHERE tipo = 'cerebro' AND estado_conexion = 'online') AS nodos_cerebro_activos,
    (SELECT COUNT(*) FROM eventos_alertas WHERE estado = 'pendiente')                  AS alertas_pendientes;

-- Tabla resumen de intersecciones
SELECT
    i.id,
    i.nombre,
    i.zona_distrito,
    SUM(CASE WHEN n.tipo = 'ojo' THEN 1 ELSE 0 END)     AS nodos_ojo,
    SUM(CASE WHEN n.tipo = 'cerebro' THEN 1 ELSE 0 END) AS nodos_cerebro,
    i.estado,
    i.ultima_actualizacion
FROM intersecciones i
         LEFT JOIN nodos n ON n.id_interseccion = i.id
GROUP BY i.id, i.nombre, i.zona_distrito, i.estado, i.ultima_actualizacion;

-- Diagnostico de nodos
SELECT
    n.id,
    n.nombre,
    n.tipo,
    i.nombre AS interseccion,
    n.ip,
    n.estado_conexion,
    n.temperatura_c,
    n.cpu_pct,
    n.ram_pct,
    n.almacenamiento_libre_mb,
    n.camara_estado,
    n.fps,
    n.latencia_ms,
    n.version_software,
    n.reinicios_recientes,
    n.ultimo_heartbeat
FROM nodos n
         JOIN intersecciones i ON i.id = n.id_interseccion
ORDER BY n.estado_conexion DESC, n.ultimo_heartbeat DESC;

-- Conteo total de detecciones por clase
SELECT
    clase,
    SUM(conteo_intervalo) AS total
FROM detecciones
GROUP BY clase;

-- Historico: ultimas detecciones por interseccion / nodo / fecha
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

-- Alertas pendientes / en revision
SELECT
    a.id,
    a.tipo,
    a.subtipo,
    a.severidad,
    a.estado,
    i.nombre AS interseccion,
    n.nombre AS nodo,
    a.descripcion,
    a.valor_metrica,
    a.ts_inicio,
    a.ts_fin,
    a.observacion_operador
FROM eventos_alertas a
         JOIN intersecciones i ON i.id = a.id_interseccion
         LEFT JOIN nodos n     ON n.id = a.id_nodo
WHERE a.estado IN ('pendiente', 'en_revision')
ORDER BY a.ts_inicio DESC;


-- =========================================================
-- CONSULTAS ADICIONALES PARA DETALLE DE INTERSECCION
-- =========================================================

-- Detalle de una interseccion por id
-- Cambiar el valor de @id_interseccion segun el cruce seleccionado en la interfaz.
SET @id_interseccion := 1;

SELECT
    i.id,
    i.nombre,
    i.calle_principal,
    i.calle_secundaria,
    i.zona_distrito,
    i.estado,
    i.ultima_actualizacion
FROM intersecciones i
WHERE i.id = @id_interseccion;

-- Nodos asociados a una interseccion
SELECT
    n.id,
    n.nombre,
    n.tipo,
    n.ip,
    n.estado_conexion,
    n.temperatura_c,
    n.camara_estado,
    n.fps,
    n.latencia_ms,
    n.ultimo_heartbeat
FROM nodos n
WHERE n.id_interseccion = @id_interseccion
ORDER BY n.tipo, n.nombre;

-- Conteo historico por clase para una interseccion
SELECT
    d.clase,
    SUM(d.conteo_intervalo) AS total_detectado
FROM detecciones d
WHERE d.id_interseccion = @id_interseccion
GROUP BY d.clase;

-- Eventos activos de una interseccion
SELECT
    a.id,
    a.tipo,
    a.subtipo,
    a.severidad,
    a.estado,
    n.nombre AS nodo,
    a.descripcion,
    a.valor_metrica,
    a.ts_inicio
FROM eventos_alertas a
         LEFT JOIN nodos n ON n.id = a.id_nodo
WHERE a.id_interseccion = @id_interseccion
  AND a.estado IN ('pendiente', 'en_revision')
ORDER BY a.severidad DESC, a.ts_inicio DESC;

-- =========================================================
-- FLUJO DE EVENTOS / ALERTAS SIN TABLA DE NOTIFICACIONES
-- =========================================================

-- Marcar un evento como en revision
-- UPDATE eventos_alertas
-- SET estado = 'en_revision',
--     observacion_operador = 'Evento tomado por operador para revision.'
-- WHERE id = 1;

-- Marcar un evento como notificado
-- UPDATE eventos_alertas
-- SET estado = 'notificada',
--     observacion_operador = 'Reporte generado para operador o autoridad competente.'
-- WHERE id = 1;

-- Resolver un evento y cerrar duracion
-- UPDATE eventos_alertas
-- SET estado = 'resuelta',
--     ts_fin = CURRENT_TIMESTAMP,
--     observacion_operador = 'Evento atendido y normalizado.'
-- WHERE id = 1;

-- Descartar falso positivo
-- UPDATE eventos_alertas
-- SET estado = 'descartada',
--     ts_fin = CURRENT_TIMESTAMP,
--     observacion_operador = 'Evento descartado por falso positivo.'
-- WHERE id = 1;
