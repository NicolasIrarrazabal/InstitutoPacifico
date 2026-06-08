package com.proyecto.ms_notas.dto;

import java.util.UUID;

public record AvanceResponseDTO(

        UUID estudianteId,

        int totalSecciones,

        int seccionesAprobadas,

        double porcentajeAvance,

        boolean cumpleAvance80
) {}
