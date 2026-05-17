CREATE TABLE matriculas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    estudiante_id UUID NOT NULL,
    seccion_id UUID NOT NULL,
    fecha_matricula DATE NOT NULL,
    estado VARCHAR(20) NOT NULL
);
