package com.proyecto.ms_practicas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record FinalizarPracticaDTO(

        @NotNull(message = "La fecha de fin es obligatoria")
        LocalDate fechaFin,

        // COMPLETADA o REPROBADA
        @NotBlank(message = "El estado final es obligatorio")
        String estado,

        String observaciones
) {}
