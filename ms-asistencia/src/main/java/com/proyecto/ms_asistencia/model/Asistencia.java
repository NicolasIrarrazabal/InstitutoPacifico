package com.proyecto.ms_asistencia.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "asistencias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "estudiante_id", nullable = false)
    private UUID estudianteId;

    @Column(name = "seccion_id", nullable = false)
    private UUID seccionId;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoAsistencia tipo;

    @Column(name = "observacion", length = 255)
    private String observacion;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;
}
