-- Flyway migration: agregar columna disponible (R1 - disponibilidad de carrera)
ALTER TABLE carreras ADD COLUMN disponible BOOLEAN NOT NULL DEFAULT TRUE;
