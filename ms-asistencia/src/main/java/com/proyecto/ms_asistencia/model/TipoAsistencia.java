package com.proyecto.ms_asistencia.model;

// Enum: define los valores FIJOS permitidos para el tipo de asistencia
// Usar un enum evita errores de tipeo (ej: "PRESNTE" en vez de "PRESENTE")
public enum TipoAsistencia {

    // El estudiante asistió a clases
    PRESENTE,

    // El estudiante no asistió → CUENTA para el cálculo del 25% (R2)
    AUSENTE,

    // El estudiante no asistió pero presentó justificación válida
    // → NO CUENTA para el cálculo del 25% (R2)
    JUSTIFICADO
}
