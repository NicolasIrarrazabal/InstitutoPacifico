package com.proyecto.ms_asistencia.dto;

import com.proyecto.ms_asistencia.model.TipoAsistencia;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

// DTO de entrada: lo que el cliente manda en el cuerpo del POST/PUT
// Las anotaciones de validación se activan automáticamente con @Valid en el controller
public record AsistenciaDTO(

        @NotNull(message = "El ID del estudiante es obligatorio")
        UUID estudianteId,

        @NotNull(message = "El ID de la sección es obligatorio")
        UUID seccionId,

        // @PastOrPresent: la fecha debe ser hoy o en el pasado
        // (no puedes registrar asistencia de clases que aún no ocurren)
        @NotNull(message = "La fecha es obligatoria")
        @PastOrPresent(message = "La fecha no puede ser futura")
        LocalDate fecha,

        // @NotNull en enum: el tipo debe ser PRESENTE, AUSENTE o JUSTIFICADO
        @NotNull(message = "El tipo de asistencia es obligatorio (PRESENTE, AUSENTE o JUSTIFICADO)")
        TipoAsistencia tipo,

        // @Size: la observación es opcional, pero si viene, no puede superar 255 caracteres
        @Size(max = 255, message = "La observación no puede superar los 255 caracteres")
        String observacion  // Este campo SÍ puede ser null (no tiene @NotNull)
) {}
