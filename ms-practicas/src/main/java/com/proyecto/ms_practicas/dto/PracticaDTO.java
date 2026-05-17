package com.proyecto.ms_practicas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record PracticaDTO(

        @NotNull(message = "El ID del estudiante es obligatorio")
        UUID estudianteId,

        @NotNull(message = "El ID de la empresa es obligatorio")
        UUID empresaId,

        @NotBlank(message = "El nombre del supervisor es obligatorio")
        String supervisorNombre,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate fechaInicio
) {}
