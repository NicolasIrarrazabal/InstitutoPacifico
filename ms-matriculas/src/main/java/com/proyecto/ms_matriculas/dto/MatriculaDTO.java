package com.proyecto.ms_matriculas.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaDTO {

    @NotNull
    private Long estudianteId; // Ahora solo tenemos el ID del estudiante

    @NotNull
    private Long seccionId; // Ahora solo tenemos el ID de la sección

    @NotNull
    private LocalDate fechaMatricula;

    @NotNull
    private String estado;
}