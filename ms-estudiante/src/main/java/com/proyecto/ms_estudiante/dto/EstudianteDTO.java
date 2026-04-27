package com.proyecto.ms_estudiante.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EstudianteDTO {

    @NotBlank
    private String nombre;

    @NotBlank
    private String rut;

    @Email
    private String email;
}