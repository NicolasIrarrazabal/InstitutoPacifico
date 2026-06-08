package com.proyecto.ms_asignaturas.service;

import com.proyecto.ms_asignaturas.model.Prerequisito;
import com.proyecto.ms_asignaturas.repository.PrerequisitoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Tag(name = "Prerequisito Service", description = "Lógica de negocio para prerrequisitos de asignaturas (R1)")
@Service
@Slf4j
@RequiredArgsConstructor
public class PrerequisitoService {

    private final PrerequisitoRepository prerequisitoRepository;

    @Operation(summary = "Listar prerrequisitos por asignatura", description = "Retorna los prerrequisitos de una asignatura (R1)")
    public List<Prerequisito> listarPorAsignatura(UUID asignaturaId) {
        log.info("Listando prerrequisitos de la asignatura ID: {}", asignaturaId);
        return prerequisitoRepository.findByAsignaturaPrincipalId(asignaturaId);
    }

    @Operation(summary = "Asignar prerrequisito", description = "Asigna un prerrequisito a una asignatura principal")
    public Prerequisito asignarPrerequisito(Prerequisito prerequisito) {
        log.info("Asignando prerrequisito: {} requiere {}",
                prerequisito.getAsignaturaPrincipal().getNombre(),
                prerequisito.getAsignaturaRequisito().getNombre());
        return prerequisitoRepository.save(prerequisito);
    }
}
