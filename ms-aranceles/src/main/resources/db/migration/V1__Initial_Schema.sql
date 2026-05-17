CREATE TABLE aranceles (
    id          UUID PRIMARY KEY,
    estudiante_id UUID NOT NULL,
    concepto    VARCHAR(100) NOT NULL,
    monto       NUMERIC(10,2) NOT NULL,
    fecha_emision   DATE NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    fecha_pago  DATE,
    estado      VARCHAR(20) NOT NULL
);
