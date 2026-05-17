package com.proyecto.ms_notas.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

// record: es una forma moderna de Java para crear clases de datos simples
// Spring valida automáticamente estos campos cuando llega un JSON con @Valid en el controller
public record NotaDTO(

        // @NotNull: el campo no puede venir nulo en el JSON
        @NotNull(message = "El ID del estudiante es obligatorio")
        UUID estudianteId,

        @NotNull(message = "El ID de la sección es obligatorio")
        UUID seccionId,

        // @DecimalMin y @DecimalMax: valida el rango de la nota (escala chilena 1.0 - 7.0)
        @NotNull(message = "La nota es obligatoria")
        @DecimalMin(value = "1.0", message = "La nota mínima es 1.0")
        @DecimalMax(value = "7.0", message = "La nota máxima es 7.0")
        BigDecimal nota,

        // @NotBlank: el texto no puede ser vacío ni solo espacios
        @NotBlank(message = "El tipo de evaluación es obligatorio")
        String tipo,

        // La ponderación debe estar entre 0.01 (1%) y 1.00 (100%)
        @NotNull(message = "La ponderación es obligatoria")
        @DecimalMin(value = "0.01", message = "La ponderación mínima es 0.01 (1%)")
        @DecimalMax(value = "1.00", message = "La ponderación máxima es 1.00 (100%)")
        BigDecimal ponderacion,

        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha
) {}
