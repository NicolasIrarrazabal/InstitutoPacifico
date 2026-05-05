package com.proyecto.ms_matriculas.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "matriculas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "estudiante_id", nullable = false)
    private UUID estudianteId;

    @Column(name = "seccion_id", nullable = false)
    private UUID seccionId;

    @Column(name = "fecha_matricula", nullable = false)
    private LocalDate fechaMatricula;

    @Column(name = "estado", nullable = false)
    private String estado;
}