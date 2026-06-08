package com.proyecto.ms_notas.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PromedioResponseDTO(

        UUID estudianteId,

        UUID seccionId,

        BigDecimal promedioPonderado,

        BigDecimal promedioSimple,

        int totalNotas,

        boolean aprobado,

        String estadoAcademico,

        String mensajeR3
) {}

