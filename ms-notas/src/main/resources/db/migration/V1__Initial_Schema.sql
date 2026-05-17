-- Flyway ejecuta este archivo una sola vez para crear la tabla en la base de datos.
-- El nombre V1__ significa "versión 1", si luego necesitas cambiar la tabla,
-- creas un archivo V2__..., V3__... etc. NUNCA modifiques este archivo una vez ejecutado.

CREATE TABLE notas (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- ID del estudiante (viene del ms-estudiante o del contexto del sistema)
    estudiante_id UUID NOT NULL,

    -- ID de la sección/asignatura en la que está inscrito
    seccion_id  UUID NOT NULL,

    -- La nota en escala chilena: 1.0 a 7.0
    -- NUMERIC(3,1) guarda hasta 3 dígitos con 1 decimal, ej: 6.5
    nota        NUMERIC(3,1) NOT NULL,

    -- Tipo de evaluación: PARCIAL_1, PARCIAL_2, EXAMEN, TRABAJO, etc.
    tipo        VARCHAR(30) NOT NULL,

    -- Ponderación de esta nota sobre el total (ej: 0.30 = 30%)
    ponderacion NUMERIC(4,2) NOT NULL,

    -- Fecha en que se registró la evaluación
    fecha       DATE NOT NULL,

    -- Estado: ACTIVA o ANULADA (eliminación lógica, igual que en ms-matriculas)
    estado      VARCHAR(20) NOT NULL DEFAULT 'ACTIVA'
);

-- Índice para buscar rápido todas las notas de un estudiante
CREATE INDEX idx_notas_estudiante ON notas(estudiante_id);

-- Índice para buscar notas por sección
CREATE INDEX idx_notas_seccion ON notas(seccion_id);
