package com.proyecto.ms_asistencia.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

// @Entity: esta clase es una tabla en la base de datos
// @Table: nombre exacto de la tabla en SQL
// Lombok: @Data genera getters/setters, @NoArgsConstructor y @AllArgsConstructor generan constructores
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

    // @Enumerated(STRING): guarda el texto "PRESENTE"/"AUSENTE"/"JUSTIFICADO" en la BD
    // (en vez de guardar 0, 1, 2 que es confuso)
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoAsistencia tipo;

    // Observación opcional (puede ser null)
    @Column(name = "observacion", length = 255)
    private String observacion;

    // "ACTIVO" o "ANULADO" — nunca borramos registros físicamente
    @Column(name = "estado", nullable = false, length = 20)
    private String estado;
}
