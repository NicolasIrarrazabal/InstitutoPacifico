package com.proyecto.ms_asignaturas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "creditos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Credito {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "La cantidad de créditos no puede ser nula")
    @Min(value = 1, message = "La cantidad de créditos debe ser al menos 1")
    @Max(value = 15, message = "La cantidad de créditos no puede ser mayor a 15")
    private Integer cantidad;
}
