package com.proyecto.ms_asistencia.dto;

import com.proyecto.ms_asistencia.model.Asistencia;

// Este DTO se devuelve al hacer POST de una asistencia.
// Incluye el registro recién guardado Y el resumen de R2 calculado al instante.
// Así el docente sabe inmediatamente si el estudiante superó el 25%.
public record RegistroAsistenciaResponseDTO(

        // El registro de asistencia que se acaba de guardar
        Asistencia asistencia,

        // El resumen actualizado de R2 después de guardar este registro
        // Si reprobadoPorAsistencia = true → ¡alerta!
        ResumenAsistenciaDTO resumenR2

) {}
