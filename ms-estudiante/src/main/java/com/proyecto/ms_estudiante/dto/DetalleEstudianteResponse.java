package com.proyecto.ms_estudiante.dto;

import com.proyecto.ms_estudiante.client.MatriculaClientService.MatriculaResponse;
import com.proyecto.ms_estudiante.client.NotaClientService.NotaResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record DetalleEstudianteResponse(
        UUID id,
        String nombre,
        String rut,
        String email,
        String telefono,
        String direccion,
        String estado,
        BigDecimal promedioPonderado,
        BigDecimal promedioSimple,
        int totalNotas,
        boolean aprobado,
        List<NotaResponse> notas,
        int totalMatriculas,
        List<MatriculaResponse> matriculas
) {}
