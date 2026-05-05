package com.proyecto.ms_estudiante.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EstudianteDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String nombre,

        @NotBlank(message = "El RUT es obligatorio")
        @Pattern(
                regexp = "^\\d{7,8}-[\\dkK]$",
                message = "El RUT debe tener el formato 12345678-9 o 12345678-K"
        )
        String rut,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato valido")
        String email,

        @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "El telefono no es valido")
        String telefono,

        @Size(max = 200, message = "La direccion no puede superar 200 caracteres")
        String direccion
) {}