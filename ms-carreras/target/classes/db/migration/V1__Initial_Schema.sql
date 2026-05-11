-- Flyway migration: crear tabla carreras
CREATE TABLE carreras (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(1000),
    duracion_semestres INTEGER NOT NULL,
    sede VARCHAR(255) NOT NULL
);