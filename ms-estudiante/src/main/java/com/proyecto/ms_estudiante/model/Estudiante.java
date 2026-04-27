package com.proyecto.ms_estudiante.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estudiantes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(nullable = false)
    private String estado; // ACTIVO, INACTIVO
}