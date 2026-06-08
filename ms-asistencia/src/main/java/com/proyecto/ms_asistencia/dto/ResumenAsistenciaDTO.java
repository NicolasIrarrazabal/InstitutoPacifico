package com.proyecto.ms_asistencia.dto;

import java.util.UUID;

public record ResumenAsistenciaDTO(

        UUID estudianteId,
        UUID seccionId,

        int totalClases,

        int clasesPresente,

        int clasesAusente,

        int clasesJustificado,

        double porcentajeInasistencia,

        boolean reprobadoPorAsistencia,

        String mensaje
) {}
