package com.proyecto.ms_practicas.dto;

// Este DTO se usa para mostrar el resultado detallado de las 3 validaciones de la R5.
// Así el frontend o Postman puede ver exactamente qué requisito está bloqueando al estudiante.
public record ValidacionR5Response(
        boolean creditosAprobados,       // ¿El estudiante tiene ≥ 80% de créditos aprobados?
        boolean arancelAlDia,            // ¿El estudiante no tiene deuda de arancel?
        boolean empresaConConvenio,      // ¿La empresa tiene un convenio vigente con el instituto?
        boolean puedeInscribir,          // true solo si los tres anteriores son true
        String mensaje                   // Mensaje explicativo (éxito o detalle del bloqueo)
) {}
