package com.proyecto.ms_asistencia.dto;

import com.proyecto.ms_asistencia.model.TipoAsistencia;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record AsistenciaDTO(

        @NotNull(message = "El ID del estudiante es obligatorio")
        UUID estudianteId,

        @NotNull(message = "El ID de la sección es obligatorio")
        UUID seccionId,

        @NotNull(message = "La fecha es obligatoria")
        @PastOrPresent(message = "La fecha no puede ser futura")
        LocalDate fecha,

        @NotNull(message = "El tipo de asistencia es obligatorio (PRESENTE, AUSENTE o JUSTIFICADO)")
        TipoAsistencia tipo,

        @Size(max = 255, message = "La observación no puede superar los 255 caracteres")
        String observacion
) {}
