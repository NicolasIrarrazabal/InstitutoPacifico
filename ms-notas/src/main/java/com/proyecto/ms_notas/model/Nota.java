package com.proyecto.ms_notas.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "notas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "estudiante_id", nullable = false)
    private UUID estudianteId;

    @Column(name = "seccion_id", nullable = false)
    private UUID seccionId;

    @Column(name = "nota", nullable = false, precision = 3, scale = 1)
    private BigDecimal nota;

    @Column(name = "tipo", nullable = false, length = 30)
    private String tipo;

    @Column(name = "ponderacion", nullable = false, precision = 4, scale = 2)
    private BigDecimal ponderacion;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Transient
    public boolean isAprobado() {
        return this.nota != null && this.nota.compareTo(new BigDecimal("4.0")) >= 0;
    }
}
