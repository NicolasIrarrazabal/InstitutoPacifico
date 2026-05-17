-- ═══════════════════════════════════════════════════════════
-- V1__Initial_Schema.sql — ms-asistencia
-- Flyway ejecuta este archivo UNA SOLA VEZ al arrancar.
-- NUNCA lo modifiques después de ejecutado.
-- Para cambios futuros: crea V2__..., V3__..., etc.
-- ═══════════════════════════════════════════════════════════

-- Tabla principal de registros de asistencia
-- Cada fila = un estudiante en una clase específica en una fecha
CREATE TABLE asistencias (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- ID del estudiante (de ms-estudiantes)
    estudiante_id   UUID NOT NULL,

    -- ID de la sección/asignatura (de ms-secciones o ms-matrículas)
    seccion_id      UUID NOT NULL,

    -- Fecha de la clase
    fecha           DATE NOT NULL,

    -- Estado de asistencia:
    --   PRESENTE    → asistió a clases
    --   AUSENTE     → no asistió (cuenta para el 25%)
    --   JUSTIFICADO → no asistió pero con justificación (NO cuenta para el 25%)
    tipo            VARCHAR(20) NOT NULL,

    -- Observación opcional del docente (ej: "llegó tarde", "certificado médico")
    observacion     VARCHAR(255),

    -- Estado del registro: ACTIVO o ANULADO (eliminación lógica)
    estado          VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
);

-- Evita registrar dos veces al mismo estudiante en la misma sección y fecha
-- Esto garantiza que no haya duplicados por error del docente
CREATE UNIQUE INDEX idx_asistencia_unica
    ON asistencias(estudiante_id, seccion_id, fecha)
    WHERE estado = 'ACTIVO';

-- Índices para búsquedas rápidas
CREATE INDEX idx_asistencia_estudiante ON asistencias(estudiante_id);
CREATE INDEX idx_asistencia_seccion    ON asistencias(seccion_id);
CREATE INDEX idx_asistencia_fecha      ON asistencias(fecha);
