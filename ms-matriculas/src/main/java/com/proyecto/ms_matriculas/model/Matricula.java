package com.proyecto.ms_matriculas.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "matriculas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "estudiante_id", nullable = false)
    private Long estudianteId; // Solo almacenamos el ID del estudiante

    @Column(name = "seccion_id", nullable = false)
    private Long seccionId; // Solo almacenamos el ID de la sección

    @Column(name = "fecha_matricula", nullable = false)
    private LocalDate fechaMatricula;

    @Column(name = "estado", nullable = false)
    private String estado;
}