package com.proyecto.ms_docente.model;

<<<<<<< HEAD
public class Contrato {
}
=======
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "contratos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "El tipo de contrato es obligatorio")
    private String tipoContrato;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    @NotNull(message = "El sueldo base no puede ser nulo")
    @Min(value = 0, message = "El sueldo no puede ser negativo")
    private BigDecimal sueldoBase;

    @OneToOne
    @JoinColumn(name = "docente_id", unique = true)
    @NotNull(message = "Un contrato debe estar asociado a un docente")
    private Docente docente;
}
>>>>>>> fa0c9f7d3e1e5d3ade7459411904e5793176644e
