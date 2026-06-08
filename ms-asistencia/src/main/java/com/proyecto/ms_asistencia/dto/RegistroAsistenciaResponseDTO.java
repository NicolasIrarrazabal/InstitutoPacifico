package com.proyecto.ms_asistencia.dto;

import com.proyecto.ms_asistencia.model.Asistencia;

public record RegistroAsistenciaResponseDTO(

        Asistencia asistencia,

        ResumenAsistenciaDTO resumenR2

) {}
