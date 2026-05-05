package com.proyecto.ms_carreras.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CarreraDTO(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        String descripcion,

        @NotNull(message = "La duración en semestres es obligatoria")
        @Min(value = 1, message = "La duración mínima es 1 semestre")
        Integer duracionSemestres,

        @NotBlank(message = "La sede es obligatoria")
        String sede
) {}