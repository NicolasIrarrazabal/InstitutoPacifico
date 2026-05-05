package com.proyecto.ms_matriculas.dto;

import java.util.UUID;

public record EstudianteResponseDTO(
        UUID id,
        String nombre,
        String rut,
        String email,
        String estado
) {}