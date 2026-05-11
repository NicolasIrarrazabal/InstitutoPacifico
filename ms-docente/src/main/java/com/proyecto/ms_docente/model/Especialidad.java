package com.proyecto.ms_docente.model;

<<<<<<< HEAD
public class Especialidad {
}
=======
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "especialidades")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Especialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "El nombre de la especialidad es obligatorio")
    private String nombre;

    @NotBlank(message = "La descripcion de la especialidad es obligatorio")
    private String descripcion;
}
>>>>>>> fa0c9f7d3e1e5d3ade7459411904e5793176644e
