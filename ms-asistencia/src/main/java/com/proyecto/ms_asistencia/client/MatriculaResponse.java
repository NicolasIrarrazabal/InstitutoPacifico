package com.proyecto.ms_asistencia.client;

import java.time.LocalDate;
import java.util.UUID;

// Representa la respuesta que devuelve ms-matriculas
// Los campos deben coincidir con lo que ese microservicio devuelve en su JSON
public record MatriculaResponse(
        UUID id,
        UUID estudianteId,
        UUID seccionId,
        LocalDate fechaMatricula,
        String estado   // "ACTIVA" o "INACTIVA"
) {}
