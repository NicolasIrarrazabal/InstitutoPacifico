package com.proyecto.ms_notas.client;

import java.time.LocalDate;
import java.util.UUID;

public record MatriculaResponse(
        UUID id,
        UUID estudianteId,
        UUID seccionId,
        LocalDate fechaMatricula,
        String estado
) {}
