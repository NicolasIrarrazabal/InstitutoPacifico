package com.proyecto.ms_notas.dto;

import java.math.BigDecimal;
import java.util.UUID;

// DTO de respuesta para el endpoint de promedio (Regla R3)
// Se devuelve al cliente con todos los datos del promedio calculado
public record PromedioResponseDTO(

        UUID estudianteId,

        // Si se calculó por sección, se incluye su ID; null si es global
        UUID seccionId,

        // Promedio ponderado de todas las notas activas del estudiante
        BigDecimal promedioPonderado,

        // Promedio simple (suma de notas / cantidad)
        BigDecimal promedioSimple,

        // Total de notas activas consideradas
        int totalNotas,

        // ¿El promedio supera el mínimo de aprobación (4.0)?
        boolean aprobado,

        // Estado académico según el promedio ponderado:
        //   APROBADO                    → promedio >= 4.0
        //   PENDIENTE_EXAMEN_RECUPERACION → promedio entre 3.5 y 3.9 (inclusive)
        //   REPROBADO                   → promedio < 3.5
        String estadoAcademico,

        // Mensaje descriptivo que explica el estado y qué debe hacer el estudiante
        String mensajeR3
) {}

