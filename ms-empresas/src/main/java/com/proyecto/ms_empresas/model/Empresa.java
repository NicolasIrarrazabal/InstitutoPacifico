package com.proyecto.ms_empresas.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "empresas")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String rut;

    @Column(nullable = false)
    private String rubro;

    @Column
    private String direccion;

    @Column
    private String telefono;

    @Column
    private String emailContacto;

    @Column
    private String nombreContacto;

    @Column
    private LocalDate fechaInicioConvenio;

    @Column
    private LocalDate fechaFinConvenio;

    @Column(nullable = false)
    private String estado;
}
