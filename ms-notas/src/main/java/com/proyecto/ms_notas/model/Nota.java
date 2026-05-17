package com.proyecto.ms_notas.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

// @Entity: le dice a JPA que esta clase representa una tabla en la base de datos
// @Table: especifica el nombre exacto de la tabla
// @Data: Lombok genera automáticamente getters, setters, equals y toString
// @NoArgsConstructor: Lombok genera el constructor vacío (requerido por JPA)
// @AllArgsConstructor: Lombok genera el constructor con todos los campos
@Entity
@Table(name = "notas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Nota {

    // @Id: este campo es la clave primaria
    // @GeneratedValue(UUID): la base de datos genera automáticamente el ID
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // @Column: mapea el campo Java al nombre de columna en la tabla SQL
    @Column(name = "estudiante_id", nullable = false)
    private UUID estudianteId;

    @Column(name = "seccion_id", nullable = false)
    private UUID seccionId;

    // BigDecimal es el tipo correcto para notas con decimales (evita errores de redondeo)
    @Column(name = "nota", nullable = false, precision = 3, scale = 1)
    private BigDecimal nota;

    // Tipo de evaluación: PARCIAL_1, PARCIAL_2, EXAMEN, TRABAJO, etc.
    @Column(name = "tipo", nullable = false, length = 30)
    private String tipo;

    // Ponderación: cuánto pesa esta nota (ej: 0.30 = 30%)
    @Column(name = "ponderacion", nullable = false, precision = 4, scale = 2)
    private BigDecimal ponderacion;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    // ACTIVA = válida, ANULADA = eliminada lógicamente (no se borra físicamente)
    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    // Método de utilidad: retorna true si la nota es >= 4.0 (aprobado en Chile)
    // @Transient: este campo NO se guarda en la base de datos, se calcula en memoria
    @Transient
    public boolean isAprobado() {
        return this.nota != null && this.nota.compareTo(new BigDecimal("4.0")) >= 0;
    }
}
