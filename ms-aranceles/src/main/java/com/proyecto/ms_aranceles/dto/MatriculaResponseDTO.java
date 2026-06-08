package com.proyecto.ms_aranceles.dto;

import java.time.LocalDate;
import java.util.UUID;

public record MatriculaResponseDTO(
        UUID id,
        UUID estudianteId,
        UUID seccionId,
        LocalDate fechaMatricula,
        String estado
) {}
