package com.proyecto.ms_practicas.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "practicas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Practica {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "estudiante_id", nullable = false)
    private UUID estudianteId;

    @Column(name = "empresa_id", nullable = false)
    private UUID empresaId;

    @Column(name = "supervisor_nombre", nullable = false)
    private String supervisorNombre;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
}
