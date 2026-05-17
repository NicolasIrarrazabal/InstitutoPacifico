package com.proyecto.ms_empresas.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record EmpresaDTO(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres")
        String nombre,

        @NotBlank(message = "El RUT es obligatorio")
        @Pattern(
                regexp = "^\\d{7,8}-[\\dkK]$",
                message = "El RUT debe tener el formato 12345678-9 o 12345678-K"
        )
        String rut,

        @NotBlank(message = "El rubro es obligatorio")
        String rubro,

        @Size(max = 200, message = "La direccion no puede superar 200 caracteres")
        String direccion,

        @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "El telefono no es valido")
        String telefono,

        @Email(message = "El email de contacto no tiene un formato valido")
        String emailContacto,

        @Size(max = 100, message = "El nombre del contacto no puede superar 100 caracteres")
        String nombreContacto,

        // Fechas del convenio (pueden ser nulas al crear, se asignan luego)
        LocalDate fechaInicioConvenio,

        LocalDate fechaFinConvenio
) {}
