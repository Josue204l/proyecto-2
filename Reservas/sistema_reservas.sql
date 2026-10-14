CREATE DATABASE sistema_reservas;
    use sistema_reservas;

    -- --------------------------------------------------------
    -- TABLAS
    -- --------------------------------------------------------

CREATE TABLE funcionarios (
                              id          varchar(10)  not null,
                              clave       varchar(20)  not null,
                              rol         varchar(20)  not null,
                              nombre      varchar(100) not null,
                              telefono    varchar(20),
                              puesto      varchar(100),
                              Primary Key (id)
);

CREATE TABLE categorias (
                            id          varchar(10)  not null,
                            nombre      varchar(100) not null,
                            descripcion varchar(255),
                            Primary Key (id)
);

CREATE TABLE recursos (
                          id          varchar(10)  not null,
                          descripcion varchar(255) not null,
                          fk_categoria varchar(10) not null,
                          Primary Key (id)
);

CREATE TABLE reservas (
                          id              varchar(10)  not null,
                          titulo          varchar(150) not null,
                          fecha           date         not null,
                          hora_inicio     time         not null,
                          hora_fin        time         not null,
                          fk_funcionario  varchar(10)  not null,
                          estado          varchar(20)  not null,
                          Primary Key (id)
);

CREATE TABLE reserva_categorias (
                                    fk_reserva   varchar(10) not null,
                                    fk_categoria varchar(10) not null,
                                    Primary Key (fk_reserva, fk_categoria)
);

CREATE TABLE reserva_recursos (
                                  fk_reserva  varchar(10) not null,
                                  fk_recurso  varchar(10) not null,
                                  Primary Key (fk_reserva, fk_recurso)
);

CREATE TABLE auditoria (
                           id          int          not null AUTO_INCREMENT,
                           fecha_hora  datetime     not null DEFAULT CURRENT_TIMESTAMP,
                           usuario     varchar(10),
                           accion      varchar(50)  not null,
                           detalle     varchar(500),
                           Primary Key (id)
);

-- --------------------------------------------------------
-- LLAVES FORÁNEAS
-- --------------------------------------------------------

ALTER TABLE recursos ADD Foreign Key (fk_categoria) REFERENCES categorias (id);

ALTER TABLE reservas ADD Foreign Key (fk_funcionario) REFERENCES funcionarios (id);

ALTER TABLE reserva_categorias ADD Foreign Key (fk_reserva) REFERENCES reservas (id);
ALTER TABLE reserva_categorias ADD Foreign Key (fk_categoria) REFERENCES categorias (id);

ALTER TABLE reserva_recursos ADD Foreign Key (fk_reserva) REFERENCES reservas (id);
ALTER TABLE reserva_recursos ADD Foreign Key (fk_recurso) REFERENCES recursos (id);

ALTER TABLE auditoria ADD Foreign Key (usuario) REFERENCES funcionarios (id);

-- --------------------------------------------------------
-- DATOS INICIALES
-- --------------------------------------------------------

insert into funcionarios (id, clave, rol, nombre, telefono, puesto) values
                                                                        ('admin', '222',  'ADMIN',       'Administrador',   '2222-0000', ''),
                                                                        ('_111',  '111',  'FUNCIONARIO', 'Funcionario Test', '8888-1234', '');

insert into categorias (id, nombre, descripcion) values
                                                     ('CAT-001', 'Sala para 10 personas', 'Sala para 10 personas'),
                                                     ('CAT-002', 'Laptop windows 11',     'Laptop windows 11');

insert into recursos (id, descripcion, fk_categoria) values
                                                         ('SALA-1',   'Sala 1 primer piso',  'CAT-001'),
                                                         ('SALA-2',   'Sala 2 segundo piso', 'CAT-001'),
                                                         ('_238715',  'Laptop #238715',      'CAT-002');

insert into reservas (id, titulo, fecha, hora_inicio, hora_fin, fk_funcionario, estado) values
                                                                                            ('RES-DEMO', 'Reunión de coordinación', '2026-09-10', '10:00:00', '12:00:00', '_111', 'ACTIVA'),
                                                                                            ('RES-002',  'Capacitación técnica',    '2026-09-15', '08:00:00', '10:00:00', '_111', 'ACTIVA');

insert into reserva_categorias (fk_reserva, fk_categoria) values
                                                              ('RES-DEMO', 'CAT-001'),
                                                              ('RES-002',  'CAT-002');

insert into reserva_recursos (fk_reserva, fk_recurso) values
                                                          ('RES-DEMO', 'SALA-1'),
                                                          ('RES-002',  '_238715');

-- --------------------------------------------------------
-- VISTAS
-- --------------------------------------------------------

CREATE VIEW v_reservas_detalle AS
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
         JOIN funcionarios f   ON r.fk_funcionario = f.id
         JOIN reserva_recursos rr ON r.id = rr.fk_reserva
         JOIN recursos rec     ON rr.fk_recurso = rec.id
         JOIN categorias cat   ON rec.fk_categoria = cat.id;

CREATE VIEW v_estadistica_recursos AS
SELECT
    cat.id              AS categoria_id,
    cat.nombre          AS categoria_nombre,
    COUNT(r.id)         AS total_reservas
FROM categorias cat
         LEFT JOIN reserva_categorias rc ON cat.id = rc.fk_categoria
         LEFT JOIN reservas r            ON rc.fk_reserva = r.id
GROUP BY cat.id, cat.nombre;

CREATE VIEW v_estadistica_semanas AS
SELECT
        YEAR(fecha)                     AS anio,
        WEEK(fecha, 3)                  AS semana_iso,
        COUNT(id)                       AS total_reservas
        FROM reservas
        GROUP BY YEAR(fecha), WEEK(fecha, 3)
        ORDER BY anio, semana_iso;
