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

/**
 * Servicio que gestiona los prerrequisitos entre asignaturas (regla R1).
 * Permite listar los prerrequisitos de una asignatura y asignar nuevos.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PrerequisitoService {

    private final PrerequisitoRepository prerequisitoRepository;

    /**
     * Obtiene todos los prerrequisitos de una asignatura (regla R1).
     *
     * @param asignaturaId identificador de la asignatura principal
     * @return lista de prerrequisitos de la asignatura
     */
    public List<Prerequisito> listarPorAsignatura(UUID asignaturaId) {
        log.info("Listando prerrequisitos de la asignatura ID: {}", asignaturaId);
        return prerequisitoRepository.findByAsignaturaPrincipalId(asignaturaId);
    }

    /**
     * Asigna un prerrequisito a una asignatura principal.
     *
     * @param prerequisito relación de prerrequisito a crear
     * @return la relación de prerrequisito creada
     */
    public Prerequisito asignarPrerequisito(Prerequisito prerequisito) {
        log.info("Asignando prerrequisito: {} requiere {}",
                prerequisito.getAsignaturaPrincipal().getNombre(),
                prerequisito.getAsignaturaRequisito().getNombre());
        return prerequisitoRepository.save(prerequisito);
    }
}
