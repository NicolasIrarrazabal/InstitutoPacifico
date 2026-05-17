package com.proyecto.ms_aranceles.dto;

import java.time.LocalDate;
import java.util.UUID;

// Lo que ms-matriculas nos devuelve al consultar una matrícula
public record MatriculaResponseDTO(
        UUID id,
        UUID estudianteId,
        UUID seccionId,
        LocalDate fechaMatricula,
        String estado
) {}
