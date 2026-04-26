package com.proyecto.ms_asignaturas.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "prerequisitos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prerequisito {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "asignatura_id")
    private Asignatura asignaturaPrincipal;

    @ManyToOne
    @JoinColumn(name = "prerequisito_id")
    private Asignatura asignaturaRequisito;
}
