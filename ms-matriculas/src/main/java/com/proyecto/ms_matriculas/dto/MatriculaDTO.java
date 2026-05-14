package com.proyecto.ms_matriculas.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record MatriculaDTO(
        @NotNull(message = "El ID del estudiante es obligatorio")
        UUID estudianteId,

        @NotNull(message = "El ID de la sección es obligatorio")
        UUID seccionId,

        @NotNull(message = "La fecha de matrícula es obligatoria")
        LocalDate fechaMatricula,

        @NotNull(message = "El estado es obligatorio")
        String estado
) {}