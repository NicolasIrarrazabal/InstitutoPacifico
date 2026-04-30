package com.proyecto.ms_estudiante.model;

import com.proyecto.ms_estudiante.model.enums.EstadoEstudiante;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "estudiantes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String rut;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String telefono;

    @Column
    private String direccion;

    @Enumerated(EnumType.STRING)
    private EstadoEstudiante estado;
}