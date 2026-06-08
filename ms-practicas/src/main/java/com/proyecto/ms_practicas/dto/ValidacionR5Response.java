package com.proyecto.ms_practicas.dto;

public record ValidacionR5Response(
        boolean creditosAprobados,
        boolean arancelAlDia,
        boolean empresaConConvenio,
        boolean puedeInscribir,
        String mensaje
) {}
