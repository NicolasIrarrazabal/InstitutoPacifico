package com.proyecto.ms_notas.dto;

import java.util.UUID;

// DTO de respuesta para el endpoint de avance académico (Regla R5)
// Indica si el estudiante ha aprobado el 80% de sus secciones inscritas
public record AvanceResponseDTO(

        UUID estudianteId,

        // Total de secciones en las que el estudiante tiene notas registradas
        int totalSecciones,

        // Secciones aprobadas (promedio de notas >= 4.0 en esa sección)
        int seccionesAprobadas,

        // Porcentaje de avance: (seccionesAprobadas / totalSecciones) * 100
        double porcentajeAvance,

        // R5: ¿el avance supera el 80%?
        boolean cumpleAvance80
) {}
