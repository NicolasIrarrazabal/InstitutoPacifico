package com.proyecto.ms_asistencia.dto;

import java.util.UUID;

// DTO de respuesta para el endpoint de resumen de asistencia
// Contiene todos los datos del cálculo de R2 para que el docente
// (o cualquier otro microservicio) pueda ver el estado del estudiante
public record ResumenAsistenciaDTO(

        UUID estudianteId,
        UUID seccionId,

        // Total de clases registradas para esta sección
        // (= todos los registros ACTIVOS de cualquier tipo para esa sección)
        int totalClases,

        // Clases en que el estudiante estuvo PRESENTE
        int clasesPresente,

        // Clases AUSENTE sin justificación (estas cuentan para el 25%)
        int clasesAusente,

        // Clases con justificación (NO cuentan para el 25%)
        int clasesJustificado,

        // Porcentaje de inasistencias INJUSTIFICADAS:
        // (clasesAusente / totalClases) * 100
        double porcentajeInasistencia,

        // ══════════════════════════════════════════════
        // si supera el 25% queda reprobado
        // Si es true → el estudiante está REPROBADO por asistencia
        // ══════════════════════════════════════════════
        boolean reprobadoPorAsistencia,

        // Mensaje descriptivo del estado (útil para mostrar al usuario)
        // Ej: "REPROBADO POR ASISTENCIA: superó el 25% de inasistencias (28.5%)"
        //  o: "Asistencia dentro del límite (15.0% de inasistencia)"
        String mensaje
) {}
