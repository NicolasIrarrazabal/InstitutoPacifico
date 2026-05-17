package com.proyecto.ms_notas.client;

import java.time.LocalDate;
import java.util.UUID;

// Este record representa la respuesta que devuelve ms-matriculas
// cuando le pedimos los datos de una matrícula.
// Los campos deben coincidir exactamente con lo que devuelve ms-matriculas.
public record MatriculaResponse(
        UUID id,
        UUID estudianteId,
        UUID seccionId,
        LocalDate fechaMatricula,
        String estado  // "ACTIVA" o "INACTIVA"
) {}
