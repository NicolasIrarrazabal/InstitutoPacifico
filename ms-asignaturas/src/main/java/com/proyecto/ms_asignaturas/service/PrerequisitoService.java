package com.proyecto.ms_asignaturas.service;

import com.proyecto.ms_asignaturas.model.Prerequisito;
import com.proyecto.ms_asignaturas.repository.PrerequisitoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrerequisitoService {

    private final PrerequisitoRepository prerequisitoRepository;

    public List<Prerequisito> listarPorAsignatura(UUID asignaturaId) {
        log.info("Listando prerrequisitos de la asignatura ID: {}", asignaturaId);
        return prerequisitoRepository.findByAsignaturaPrincipalId(asignaturaId);
    }

    public Prerequisito asignarPrerequisito(Prerequisito prerequisito) {
        log.info("Asignando prerrequisito: {} requiere {}",
                prerequisito.getAsignaturaPrincipal().getNombre(),
                prerequisito.getAsignaturaRequisito().getNombre());
        return prerequisitoRepository.save(prerequisito);
    }
}
