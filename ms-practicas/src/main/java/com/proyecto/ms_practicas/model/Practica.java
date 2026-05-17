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

    // ID del estudiante que realiza la práctica (viene de ms-estudiantes)
    @Column(name = "estudiante_id", nullable = false)
    private UUID estudianteId;

    // ID de la empresa donde se realiza la práctica (viene de ms-empresas)
    @Column(name = "empresa_id", nullable = false)
    private UUID empresaId;

    // Nombre del supervisor en la empresa
    @Column(name = "supervisor_nombre", nullable = false)
    private String supervisorNombre;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    // Se llena cuando el estudiante termina la práctica
    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    // PENDIENTE → EN_CURSO → COMPLETADA o REPROBADA
    @Column(name = "estado", nullable = false)
    private String estado;

    // Comentarios del supervisor o coordinador de práctica
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
}
