CREATE EXTENSION IF NOT EXISTS pgcrypto;

BEGIN;

-- =========================================================
-- BORRAR TABLAS Y DATOS ANTERIORES
-- =========================================================
DROP TABLE IF EXISTS "reserva" CASCADE;
DROP TABLE IF EXISTS "acceso" CASCADE;
DROP TABLE IF EXISTS "cuota" CASCADE;
DROP TABLE IF EXISTS "usuario" CASCADE;
DROP TABLE IF EXISTS "socio" CASCADE;
DROP TABLE IF EXISTS "actividad" CASCADE;
DROP TABLE IF EXISTS "monitor" CASCADE;
DROP TABLE IF EXISTS "plan" CASCADE;

-- =========================================================
-- TABLAS
-- =========================================================
CREATE TABLE "plan" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "nombre" VARCHAR(50) NOT NULL UNIQUE,
    "precio_mensual" DECIMAL(8, 2) NOT NULL,
    "incluye_clases" BOOLEAN NOT NULL,
    "tipo_plan" VARCHAR(20) NOT NULL DEFAULT 'BASICO',
    CONSTRAINT "plan_tipo_plan_check" CHECK ("tipo_plan" IN ('BASICO', 'PREMIUM'))
);

CREATE TABLE "monitor" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "nombre" VARCHAR(255) NOT NULL,
    "apellido" VARCHAR(255) NOT NULL
);

CREATE TABLE "socio" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "dni" VARCHAR(9) NOT NULL UNIQUE,
    "nombre" VARCHAR(100) NOT NULL,
    "apellidos" VARCHAR(100) NOT NULL,
    "email" VARCHAR(150) NOT NULL UNIQUE,
    "telefono" VARCHAR(15) NOT NULL UNIQUE,
    "fecha_alta" DATE NOT NULL DEFAULT CURRENT_DATE,
    "matricula" VARCHAR(10) UNIQUE,
    "estado" VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    "plan_id" UUID NOT NULL,
    CONSTRAINT "socio_estado_check" CHECK ("estado" IN ('ACTIVO', 'INACTIVO')),
    CONSTRAINT "socio_plan_fk" FOREIGN KEY ("plan_id") REFERENCES "plan" ("id")
);

CREATE TABLE "usuario" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "nombre" VARCHAR(255) NOT NULL,
    "apellidos" VARCHAR(255) NOT NULL,
    "username" VARCHAR(50) NOT NULL UNIQUE,
    "password" VARCHAR(255) NOT NULL,
    "rol" VARCHAR(20) NOT NULL DEFAULT 'SOCIO',
    "socio_id" UUID UNIQUE,
    CONSTRAINT "usuario_rol_check" CHECK ("rol" IN ('ADMIN', 'RECEPCION', 'SOCIO')),
    CONSTRAINT "usuario_socio_fk" FOREIGN KEY ("socio_id") REFERENCES "socio" ("id")
);

CREATE TABLE "cuota" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "mes" INTEGER NOT NULL,
    "anio" INTEGER NOT NULL,
    "importe" DECIMAL(8, 2) NOT NULL,
    "estado" VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    "fecha_pago" TIMESTAMP WITHOUT TIME ZONE,
    "socio_id" UUID NOT NULL,
    "plan_id" UUID NOT NULL,
    CONSTRAINT "cuota_estado_check" CHECK ("estado" IN ('PENDIENTE', 'PAGADA')),
    CONSTRAINT "cuota_socio_fk" FOREIGN KEY ("socio_id") REFERENCES "socio" ("id"),
    CONSTRAINT "cuota_plan_fk" FOREIGN KEY ("plan_id") REFERENCES "plan" ("id"),
    CONSTRAINT "cuota_socio_mes_anio_unique" UNIQUE ("socio_id", "mes", "anio")
);

CREATE TABLE "acceso" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "fecha_hora_entrada" TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "fecha_hora_salida" TIMESTAMP WITHOUT TIME ZONE,
    "resultado" BOOLEAN NOT NULL,
    "motivo_rechazo" VARCHAR(50),
    "bonificacion_parking_aplicada" BOOLEAN NOT NULL,
    "socio_id" UUID NOT NULL,
    CONSTRAINT "acceso_socio_fk" FOREIGN KEY ("socio_id") REFERENCES "socio" ("id")
);

CREATE TABLE "actividad" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "nombre" VARCHAR(100) NOT NULL UNIQUE,
    "fecha_hora" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "duracion_minutos" INTEGER NOT NULL,
    "sala" VARCHAR(50) NOT NULL,
    "aforo_maximo" INTEGER NOT NULL,
    "monitor_id" UUID NOT NULL,
    CONSTRAINT "actividad_monitor_fk" FOREIGN KEY ("monitor_id") REFERENCES "monitor" ("id")
);

CREATE TABLE "reserva" (
    "id" UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "fecha_reserva" TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "socio_id" UUID NOT NULL,
    "actividad_id" UUID NOT NULL,
    CONSTRAINT "reserva_socio_fk" FOREIGN KEY ("socio_id") REFERENCES "socio" ("id"),
    CONSTRAINT "reserva_actividad_fk" FOREIGN KEY ("actividad_id") REFERENCES "actividad" ("id"),
    CONSTRAINT "reserva_socio_actividad_unique" UNIQUE ("socio_id", "actividad_id")
);

-- =========================================================
-- PLANES
-- =========================================================
INSERT INTO "plan" ("nombre", "precio_mensual", "incluye_clases", "tipo_plan")
VALUES
    ('Plan Básico Mensual', 29.99, FALSE, 'BASICO'),
    ('Plan Premium Mensual', 49.99, TRUE, 'PREMIUM'),
    ('Plan Básico Joven', 24.99, FALSE, 'BASICO'),
    ('Plan Premium Joven', 39.99, TRUE, 'PREMIUM'),
    ('Plan Básico Senior', 19.99, FALSE, 'BASICO'),
    ('Plan Premium Senior', 34.99, TRUE, 'PREMIUM'),
    ('Plan Básico Trimestral', 79.99, FALSE, 'BASICO'),
    ('Plan Premium Trimestral', 129.99, TRUE, 'PREMIUM'),
    ('Plan Básico Anual', 280.00, FALSE, 'BASICO'),
    ('Plan Premium Anual', 450.00, TRUE, 'PREMIUM');

-- =========================================================
-- MONITORES
-- =========================================================
INSERT INTO "monitor" ("nombre", "apellido")
VALUES
    ('Carlos', 'González'),
    ('Laura', 'Martínez'),
    ('David', 'Fernández'),
    ('Sofía', 'López'),
    ('Alejandro', 'Ruiz'),
    ('Elena', 'Navarro'),
    ('Pablo', 'Sánchez'),
    ('Lucía', 'Gómez'),
    ('Javier', 'Torres'),
    ('Marta', 'Díaz');

-- =========================================================
-- SOCIOS (DNIs con letra válida)
-- =========================================================
INSERT INTO "socio"
    ("dni", "nombre", "apellidos", "email", "telefono", "fecha_alta", "matricula", "estado", "plan_id")
SELECT
    d.dni, d.nombre, d.apellidos, d.email, d.telefono,
    d.fecha_alta::date, d.matricula, d.estado, p."id"
FROM (
    VALUES
        ('12345678Z', 'Juan',    'Pérez García',      'juan.perez.garcia@gmail.com',     '612345678', '2025-09-01', '4821KJH', 'ACTIVO',   'Plan Premium Mensual'),
        ('23456789D', 'María',   'López Rodríguez',   'maria.lopez.rod@hotmail.com',     '623456789', '2025-09-15', NULL,      'ACTIVO',   'Plan Básico Mensual'),
        ('34567890V', 'Pedro',   'Gómez Sánchez',     'pedro.gomez.s@gmail.com',         '634567890', '2025-10-01', '7364LMN', 'ACTIVO',   'Plan Premium Mensual'),
        ('45678901G', 'Ana',     'Martín Alonso',     'ana.martin.alonso@outlook.es',    '645678901', '2025-10-10', NULL,      'INACTIVO', 'Plan Básico Joven'),
        ('56789012B', 'Luis',    'Hernández Gil',     'luis.hernandez.gil@gmail.com',    '656789012', '2025-11-03', '1957PQR', 'ACTIVO',   'Plan Premium Joven'),
        ('67890123B', 'Carmen',  'Navarro Cano',      'carmen.navarro.c@gmail.com',      '667890123', '2025-12-01', NULL,      'ACTIVO',   'Plan Premium Mensual'),
        ('78901234X', 'Sergio',  'Romero Torres',     'sergio.romero.t@yahoo.es',        '678901234', '2026-01-12', '3048STV', 'ACTIVO',   'Plan Premium Trimestral'),
        ('89012345E', 'Paula',   'Serrano Marín',     'paula.serrano.m@gmail.com',       '689012345', '2026-02-02', NULL,      'INACTIVO', 'Plan Básico Mensual'),
        ('90123456A', 'Diego',   'Blanco Morales',    'diego.blanco.m@gmail.com',        '690123456', '2026-03-01', '6612WXY', 'ACTIVO',   'Plan Premium Mensual'),
        ('10234567G', 'Isabel',  'Suárez Ortega',     'isabel.suarez.o@hotmail.com',     '601234567', '2026-04-06', NULL,      'ACTIVO',   'Plan Premium Senior'),
        ('11223344B', 'Alberto', 'Vega Molina',       'alberto.vega.m@gmail.com',        '611223344', '2026-05-04', '2275BCD', 'ACTIVO',   'Plan Básico Trimestral'),
        ('22334455Y', 'Natalia', 'Castro Iglesias',   'natalia.castro.i@gmail.com',      '622334455', '2026-06-01', '9031FGH', 'ACTIVO',   'Plan Premium Anual')
) AS d(dni, nombre, apellidos, email, telefono, fecha_alta, matricula, estado, nombre_plan)
JOIN "plan" p ON p."nombre" = d.nombre_plan;

-- =========================================================
-- USUARIOS  (contraseña de todos: password, cifrada con bcrypt vía pgcrypto)
-- =========================================================
INSERT INTO "usuario" ("nombre", "apellidos", "username", "password", "rol", "socio_id")
SELECT
    d.nombre, d.apellidos, d.username,
    '{bcrypt}' || crypt('password', gen_salt('bf', 10)),
    d.rol, s."id"
FROM (
    VALUES
        ('Administrador', 'General',          'admin',       'ADMIN',     NULL),
        ('Laura',         'Recepción',        'recepcion',   'RECEPCION', NULL),
        ('Miguel',        'Recepción',        'recepcion2',  'RECEPCION', NULL),
        ('Juan',          'Pérez García',     'jperez',      'SOCIO', '12345678Z'),
        ('María',         'López Rodríguez',  'mlopez',      'SOCIO', '23456789D'),
        ('Pedro',         'Gómez Sánchez',    'pgomez',      'SOCIO', '34567890V'),
        ('Ana',           'Martín Alonso',    'amartin',     'SOCIO', '45678901G'),
        ('Luis',          'Hernández Gil',    'lhernandez',  'SOCIO', '56789012B'),
        ('Carmen',        'Navarro Cano',     'cnavarro',    'SOCIO', '67890123B'),
        ('Sergio',        'Romero Torres',    'sromero',     'SOCIO', '78901234X'),
        ('Paula',         'Serrano Marín',    'pserrano',    'SOCIO', '89012345E'),
        ('Diego',         'Blanco Morales',   'dblanco',     'SOCIO', '90123456A'),
        ('Isabel',        'Suárez Ortega',    'isuarez',     'SOCIO', '10234567G'),
        ('Alberto',       'Vega Molina',      'avega',       'SOCIO', '11223344B'),
        ('Natalia',       'Castro Iglesias',  'ncastro',     'SOCIO', '22334455Y')
) AS d(nombre, apellidos, username, rol, dni)
LEFT JOIN "socio" s ON s."dni" = d.dni;

-- =========================================================
-- CUOTAS
-- Octubre y noviembre de 2026, todas PAGADAS
-- salvo las listadas en "pendientes" (casos de prueba).
-- =========================================================
INSERT INTO "cuota" ("mes", "anio", "importe", "estado", "fecha_pago", "socio_id", "plan_id")
SELECT
    EXTRACT(MONTH FROM m)::int,
    EXTRACT(YEAR FROM m)::int,
    p."precio_mensual",
    CASE WHEN pe.dni IS NULL THEN 'PAGADA' ELSE 'PENDIENTE' END,
    CASE WHEN pe.dni IS NULL
         THEN m + ((ascii(right(s."dni", 1)) % 5) * INTERVAL '1 day') + INTERVAL '9 hours 20 minutes'
         ELSE NULL END,
    s."id",
    p."id"
FROM "socio" s
JOIN "plan" p ON p."id" = s."plan_id"
CROSS JOIN LATERAL generate_series(
    GREATEST(date_trunc('month', s."fecha_alta"::timestamp), TIMESTAMP '2026-10-01'),
    TIMESTAMP '2026-11-01',
    INTERVAL '1 month'
) AS m
LEFT JOIN (
    VALUES
        ('23456789D', 11, 2026),  -- María: solo noviembre pendiente
        ('45678901G', 10, 2026),  -- Ana (INACTIVA): 2 meses sin pagar
        ('45678901G', 11, 2026),
        ('67890123B', 11, 2026),  -- Carmen: noviembre pendiente (Premium)
        ('89012345E', 10, 2026),  -- Paula (INACTIVA): 2 meses sin pagar
        ('89012345E', 11, 2026),
        ('10234567G', 10, 2026),  -- Isabel: octubre y noviembre pendientes
        ('10234567G', 11, 2026)
) AS pe(dni, mes, anio)
    ON pe.dni = s."dni"
   AND pe.mes = EXTRACT(MONTH FROM m)::int
   AND pe.anio = EXTRACT(YEAR FROM m)::int;

-- =========================================================
-- ACTIVIDADES  (pasadas y próximas)
-- =========================================================
INSERT INTO "actividad"
    ("nombre", "fecha_hora", "duracion_minutos", "sala", "aforo_maximo", "monitor_id")
SELECT d.nombre, d.fecha_hora::timestamp, d.duracion, d.sala, d.aforo, mo."id"
FROM (
    VALUES
        ('Spinning Mañana 24/11',      '2026-11-24 09:00:00', 45, 'Sala Ciclismo',  20, 'Carlos',    'González'),
        ('Yoga Vital 25/11',           '2026-11-25 10:00:00', 60, 'Sala Mente',     15, 'Laura',     'Martínez'),
        ('Body Pump Intenso',          '2026-11-27 11:00:00', 50, 'Sala Principal', 25, 'David',     'Fernández'),
        ('Pilates Core',               '2026-11-27 17:00:00', 55, 'Sala Mente',     15, 'Sofía',     'López'),
        ('Zumba Fitness',              '2026-11-27 18:00:00', 50, 'Sala Principal', 30, 'Alejandro', 'Ruiz'),
        ('CrossTraining Express',      '2026-11-28 19:00:00', 45, 'Zona Funcional', 12, 'Elena',     'Navarro'),
        ('Spinning Tarde',             '2026-11-28 19:30:00', 45, 'Sala Ciclismo',  20, 'Pablo',     'Sánchez'),
        ('GAP Fuerza',                 '2026-11-30 10:00:00', 45, 'Sala Principal', 20, 'Lucía',     'Gómez'),
        ('Boxeo Fit',                  '2026-11-30 18:00:00', 60, 'Zona Funcional', 15, 'Javier',    'Torres'),
        ('Yoga Atardecer',             '2026-12-01 20:00:00', 60, 'Sala Mente',     15, 'Marta',     'Díaz'),
        ('Entrenamiento Personal Duo', '2026-12-02 12:00:00', 60, 'Zona Funcional',  2, 'Elena',     'Navarro')  -- aforo 2: probar "aforo completo"
) AS d(nombre, fecha_hora, duracion, sala, aforo, nombre_monitor, apellido_monitor)
JOIN "monitor" mo
  ON mo."nombre" = d.nombre_monitor AND mo."apellido" = d.apellido_monitor;

-- =========================================================
-- RESERVAS (solo socios ACTIVOS con plan Premium)
-- =========================================================
INSERT INTO "reserva" ("fecha_reserva", "socio_id", "actividad_id")
SELECT d.fecha_reserva::timestamp, s."id", a."id"
FROM (
    VALUES
        ('2026-11-20 10:00:00', '12345678Z', 'Spinning Mañana 24/11'),
        ('2026-11-20 10:05:00', '34567890V', 'Spinning Mañana 24/11'),
        ('2026-11-21 09:30:00', '56789012B', 'Yoga Vital 25/11'),
        ('2026-11-23 18:10:00', '67890123B', 'Body Pump Intenso'),
        ('2026-11-23 18:15:00', '78901234X', 'Body Pump Intenso'),
        ('2026-11-24 12:00:00', '90123456A', 'Pilates Core'),
        ('2026-11-24 12:10:00', '10234567G', 'Zumba Fitness'),
        ('2026-11-25 09:00:00', '12345678Z', 'CrossTraining Express'),
        ('2026-11-25 09:05:00', '34567890V', 'Spinning Tarde'),
        ('2026-11-25 09:10:00', '56789012B', 'GAP Fuerza'),
        ('2026-11-25 20:00:00', '22334455Y', 'Yoga Atardecer'),
        ('2026-11-26 08:00:00', '90123456A', 'Entrenamiento Personal Duo'),
        ('2026-11-26 08:05:00', '22334455Y', 'Entrenamiento Personal Duo')  -- aforo completo
) AS d(fecha_reserva, dni, nombre_actividad)
JOIN "socio" s ON s."dni" = d.dni
JOIN "actividad" a ON a."nombre" = d.nombre_actividad;

-- =========================================================
-- ACCESOS (últimos días, incluye rechazos para probar)
-- =========================================================
INSERT INTO "acceso"
    ("fecha_hora_entrada", "fecha_hora_salida", "resultado", "motivo_rechazo",
     "bonificacion_parking_aplicada", "socio_id")
SELECT d.entrada::timestamp, d.salida::timestamp, d.resultado, d.motivo, d.parking, s."id"
FROM (
    VALUES
        ('2026-11-25 07:45:00', '2026-11-25 09:10:00', TRUE,  NULL,              TRUE,  '12345678Z'),
        ('2026-11-25 08:00:00', '2026-11-25 09:00:00', TRUE,  NULL,              FALSE, '23456789D'),
        ('2026-11-25 08:20:00', '2026-11-25 09:45:00', TRUE,  NULL,              TRUE,  '34567890V'),
        ('2026-11-25 09:00:00', '2026-11-25 10:20:00', TRUE,  NULL,              TRUE,  '56789012B'),
        ('2026-11-25 10:15:00', '2026-11-25 11:30:00', TRUE,  NULL,              FALSE, '67890123B'),
        ('2026-11-25 18:30:00', '2026-11-25 20:00:00', TRUE,  NULL,              TRUE,  '78901234X'),
        ('2026-11-26 07:30:00', NULL,                  TRUE,  NULL,              TRUE,  '90123456A'),
        ('2026-11-26 08:10:00', NULL,                  TRUE,  NULL,              FALSE, '10234567G'),
        ('2026-11-26 08:35:00', NULL,                  FALSE, 'CUOTA_PENDIENTE', FALSE, '45678901G'),
        ('2026-11-26 09:00:00', NULL,                  FALSE, 'SOCIO_INACTIVO',  FALSE, '89012345E'),
        ('2026-11-26 09:20:00', NULL,                  TRUE,  NULL,              TRUE,  '11223344B'),
        ('2026-11-26 09:40:00', NULL,                  TRUE,  NULL,              TRUE,  '22334455Y')
) AS d(entrada, salida, resultado, motivo, parking, dni)
JOIN "socio" s ON s."dni" = d.dni;

COMMIT;

-- =========================================================
-- COMPROBACIONES
-- =========================================================
SELECT "rol", "username" FROM "usuario" ORDER BY "rol", "username";

SELECT s."dni", s."nombre", s."estado",
       COUNT(*) FILTER (WHERE c."estado" = 'PAGADA')    AS pagadas,
       COUNT(*) FILTER (WHERE c."estado" = 'PENDIENTE') AS pendientes
FROM "socio" s
JOIN "cuota" c ON c."socio_id" = s."id"
GROUP BY s."dni", s."nombre", s."estado"
ORDER BY s."nombre";
