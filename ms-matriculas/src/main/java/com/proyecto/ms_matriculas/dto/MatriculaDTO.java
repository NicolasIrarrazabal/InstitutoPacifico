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
    private Long estudianteId;

    @NotNull
    private Long seccionId;

    @NotNull
    private LocalDate fechaMatricula;

    @NotNull
    private String estado;
}