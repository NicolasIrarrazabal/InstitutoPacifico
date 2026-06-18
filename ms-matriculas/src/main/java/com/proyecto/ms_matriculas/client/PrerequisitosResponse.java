package com.proyecto.ms_matriculas.client;

import java.util.UUID;

public record PrerequisitosResponse(
        UUID id,
        AsignaturaRef asignaturaRequisito
) {
    public record AsignaturaRef(UUID id, String nombre) {}
}
