CREATE DATABASE IF NOT EXISTS sistema_reservas;
USE sistema_reservas;

-- --------------------------------------------------------
-- TABLAS
-- --------------------------------------------------------

CREATE TABLE IF NOT EXISTS funcionarios (
    id          varchar(10)  not null,
    clave       varchar(20)  not null,
    rol         varchar(20)  not null,
    nombre      varchar(100) not null,
    telefono    varchar(20),
    puesto      varchar(100),
    Primary Key (id)
);

CREATE TABLE IF NOT EXISTS categorias (
    id          varchar(10)  not null,
    nombre      varchar(100) not null,
    descripcion varchar(255),
    Primary Key (id)
);

CREATE TABLE IF NOT EXISTS recursos (
    id           varchar(10)  not null,
    descripcion  varchar(255) not null,
    fk_categoria varchar(10)  not null,
    Primary Key (id)
);

CREATE TABLE IF NOT EXISTS reservas (
    id             varchar(10)  not null,
    titulo         varchar(150) not null,
    fecha          date         not null,
    hora_inicio    time         not null,
    hora_fin       time         not null,
    fk_funcionario varchar(10)  not null,
    estado         varchar(20)  not null,
    Primary Key (id)
);

CREATE TABLE IF NOT EXISTS reserva_categorias (
    fk_reserva   varchar(10) not null,
    fk_categoria varchar(10) not null,
    Primary Key (fk_reserva, fk_categoria)
);

CREATE TABLE IF NOT EXISTS reserva_recursos (
    fk_reserva varchar(10) not null,
    fk_recurso varchar(10) not null,
    Primary Key (fk_reserva, fk_recurso)
);

CREATE TABLE IF NOT EXISTS auditoria (
    id         int          not null AUTO_INCREMENT,
    fecha_hora datetime     not null DEFAULT CURRENT_TIMESTAMP,
    usuario    varchar(10),
    accion     varchar(50)  not null,
    detalle    varchar(500),
    Primary Key (id)
);

-- --------------------------------------------------------
-- LLAVES FORANEAS
-- --------------------------------------------------------

SET @ddl = IF((SELECT COUNT(*) FROM information_schema.KEY_COLUMN_USAGE
               WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'recursos'
                 AND COLUMN_NAME = 'fk_categoria' AND REFERENCED_TABLE_NAME = 'categorias') = 0,
              'ALTER TABLE recursos ADD CONSTRAINT fk_recursos_categoria FOREIGN KEY (fk_categoria) REFERENCES categorias (id)',
              'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF((SELECT COUNT(*) FROM information_schema.KEY_COLUMN_USAGE
               WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'reservas'
                 AND COLUMN_NAME = 'fk_funcionario' AND REFERENCED_TABLE_NAME = 'funcionarios') = 0,
              'ALTER TABLE reservas ADD CONSTRAINT fk_reservas_funcionario FOREIGN KEY (fk_funcionario) REFERENCES funcionarios (id)',
              'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF((SELECT COUNT(*) FROM information_schema.KEY_COLUMN_USAGE
               WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'reserva_categorias'
                 AND COLUMN_NAME = 'fk_reserva' AND REFERENCED_TABLE_NAME = 'reservas') = 0,
              'ALTER TABLE reserva_categorias ADD CONSTRAINT fk_reserva_categorias_reserva FOREIGN KEY (fk_reserva) REFERENCES reservas (id)',
              'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF((SELECT COUNT(*) FROM information_schema.KEY_COLUMN_USAGE
               WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'reserva_categorias'
                 AND COLUMN_NAME = 'fk_categoria' AND REFERENCED_TABLE_NAME = 'categorias') = 0,
              'ALTER TABLE reserva_categorias ADD CONSTRAINT fk_reserva_categorias_categoria FOREIGN KEY (fk_categoria) REFERENCES categorias (id)',
              'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF((SELECT COUNT(*) FROM information_schema.KEY_COLUMN_USAGE
               WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'reserva_recursos'
                 AND COLUMN_NAME = 'fk_reserva' AND REFERENCED_TABLE_NAME = 'reservas') = 0,
              'ALTER TABLE reserva_recursos ADD CONSTRAINT fk_reserva_recursos_reserva FOREIGN KEY (fk_reserva) REFERENCES reservas (id)',
              'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF((SELECT COUNT(*) FROM information_schema.KEY_COLUMN_USAGE
               WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'reserva_recursos'
                 AND COLUMN_NAME = 'fk_recurso' AND REFERENCED_TABLE_NAME = 'recursos') = 0,
              'ALTER TABLE reserva_recursos ADD CONSTRAINT fk_reserva_recursos_recurso FOREIGN KEY (fk_recurso) REFERENCES recursos (id)',
              'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF((SELECT COUNT(*) FROM information_schema.KEY_COLUMN_USAGE
               WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'auditoria'
                 AND COLUMN_NAME = 'usuario' AND REFERENCED_TABLE_NAME = 'funcionarios') = 0,
              'ALTER TABLE auditoria ADD CONSTRAINT fk_auditoria_funcionario FOREIGN KEY (usuario) REFERENCES funcionarios (id)',
              'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- --------------------------------------------------------
-- DATOS INICIALES
-- --------------------------------------------------------

INSERT IGNORE INTO funcionarios (id, clave, rol, nombre, telefono, puesto) VALUES
    ('admin', '222', 'ADMIN',       'Administrador',   '2222-0000', ''),
    ('111', '111', 'FUNCIONARIO', 'Funcionario Test', '8888-1234', '');

INSERT IGNORE INTO categorias (id, nombre, descripcion) VALUES
    ('CAT-001', 'Sala para 10 personas', 'Sala para 10 personas'),
    ('CAT-002', 'Laptop windows 11',     'Laptop windows 11');

INSERT IGNORE INTO recursos (id, descripcion, fk_categoria) VALUES
    ('SALA-1',  'Sala 1 primer piso',  'CAT-001'),
    ('SALA-2',  'Sala 2 segundo piso', 'CAT-001'),
    ('LAP-001', 'Laptop #238715',      'CAT-002');

INSERT IGNORE INTO reservas (id, titulo, fecha, hora_inicio, hora_fin, fk_funcionario, estado) VALUES
    ('RES-DEMO', 'Reunion de coordinacion', '2026-11-10', '10:00:00', '12:00:00', '111', 'ACTIVA'),
    ('RES-002',  'Capacitacion tecnica',    '2026-11-15', '08:00:00', '10:00:00', '111', 'ACTIVA');

INSERT IGNORE INTO reserva_categorias (fk_reserva, fk_categoria) VALUES
    ('RES-DEMO', 'CAT-001'),
    ('RES-002',  'CAT-002');

INSERT IGNORE INTO reserva_recursos (fk_reserva, fk_recurso) VALUES
    ('RES-DEMO', 'SALA-1'),
    ('RES-002',  'LAP-001');

-- --------------------------------------------------------
-- VISTAS
-- --------------------------------------------------------

CREATE OR REPLACE VIEW v_reservas_detalle AS
SELECT
    r.id,
    r.titulo,
    r.fecha,
    r.hora_inicio,
    r.hora_fin,
    r.estado,
    f.nombre        AS solicitante,
    rec.id          AS recurso_id,
    rec.descripcion AS recurso_descripcion,
    cat.nombre      AS categoria_nombre
FROM reservas r
    JOIN funcionarios f      ON r.fk_funcionario = f.id
    JOIN reserva_recursos rr ON r.id = rr.fk_reserva
    JOIN recursos rec        ON rr.fk_recurso = rec.id
    JOIN categorias cat      ON rec.fk_categoria = cat.id;

CREATE OR REPLACE VIEW v_estadistica_recursos AS
SELECT
    cat.id          AS categoria_id,
    cat.nombre      AS categoria_nombre,
    COUNT(r.id)     AS total_reservas
FROM categorias cat
    LEFT JOIN reserva_categorias rc ON cat.id = rc.fk_categoria
    LEFT JOIN reservas r            ON rc.fk_reserva = r.id
GROUP BY cat.id, cat.nombre;

CREATE OR REPLACE VIEW v_estadistica_semanas AS
SELECT
    YEAR(fecha)    AS anio,
    WEEK(fecha, 3) AS semana_iso,
    COUNT(id)      AS total_reservas
FROM reservas
GROUP BY YEAR(fecha), WEEK(fecha, 3)
ORDER BY anio, semana_iso;
