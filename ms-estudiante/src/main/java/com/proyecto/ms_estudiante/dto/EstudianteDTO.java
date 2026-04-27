package com.proyecto.ms_estudiante.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor // Lombok genera el constructor
@Getter // Lombok genera los getters automáticamente
public class EstudianteDTO {

    @NotBlank
    private String nombre;

    @NotBlank
    private String rut;

    @Email
    private String email;
}