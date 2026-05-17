CREATE TABLE practicas (
    id              UUID PRIMARY KEY,
    estudiante_id   UUID NOT NULL,
    empresa_id      UUID NOT NULL,
    supervisor_nombre VARCHAR(150) NOT NULL,
    fecha_inicio    DATE NOT NULL,
    fecha_fin       DATE,
    estado          VARCHAR(20) NOT NULL,
    observaciones   TEXT
);
