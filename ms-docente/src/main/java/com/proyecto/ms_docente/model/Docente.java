package com.proyecto.ms_docente.model;

<<<<<<< HEAD
public class Docente {
}
=======
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "docentes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Docente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    @Column(unique = true)
    private String email;

    @NotNull(message = "La especialidad no puede ser nula")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "especialidad_id")
    private Especialidad especialidad;
}
>>>>>>> fa0c9f7d3e1e5d3ade7459411904e5793176644e
