package com.proyecto.ms_asignaturas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AsignaturaDTO(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotNull(message = "Los créditos son obligatorios")
        @Min(value = 1, message = "Los créditos deben ser al menos 1")
        Integer creditos,

        @NotNull(message = "El ID de la carrera es obligatorio")
        Long carreraId,

        @NotNull(message = "El semestre es obligatorio")
        @Min(value = 1, message = "El semestre debe ser al menos 1")
        Integer semestre
) {}