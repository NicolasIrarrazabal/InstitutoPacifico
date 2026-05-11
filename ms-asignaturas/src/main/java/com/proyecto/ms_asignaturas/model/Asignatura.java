package com.proyecto.ms_asignaturas.model;

import jakarta.persistence.*;
<<<<<<< HEAD
import jakarta.validation.constraints.Min;
=======
>>>>>>> fa0c9f7d3e1e5d3ade7459411904e5793176644e
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "asignaturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asignatura {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "El nombre de la asignatura no puede estar vacío")
    private String nombre;

<<<<<<< HEAD
    @Min(value = 1, message = "Debe tener al menos 1 crédito")
    @ManyToOne(fetch = FetchType.EAGER)
=======
    @ManyToOne(fetch = FetchType.LAZY)
>>>>>>> fa0c9f7d3e1e5d3ade7459411904e5793176644e
    @JoinColumn(name = "credito_id")
    private Credito credito;

    @OneToMany(mappedBy = "asignaturaPrincipal", cascade = CascadeType.ALL)
    private List<Prerequisito> listaPrerequisitos;
}
