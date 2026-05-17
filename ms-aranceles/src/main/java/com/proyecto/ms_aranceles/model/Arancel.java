package com.proyecto.ms_aranceles.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "aranceles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Arancel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ID del estudiante que debe este arancel (viene de ms-estudiantes)
    @Column(name = "estudiante_id", nullable = false)
    private UUID estudianteId;

    // Ej: "ARANCEL_MENSUAL", "MATRICULA_ANUAL", "DERECHO_EXAMEN"
    @Column(name = "concepto", nullable = false)
    private String concepto;

    @Column(name = "monto", nullable = false)
    private BigDecimal monto;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    // Solo se llena cuando el estudiante paga
    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    // PENDIENTE → PAGADO o VENCIDO
    @Column(name = "estado", nullable = false)
    private String estado;
}
