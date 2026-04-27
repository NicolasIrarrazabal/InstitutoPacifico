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

    private PrerequisitoRepository prerequisitoRepository;

    public List<Prerequisito> listarPorAsignatura(UUID asignaturaId) {
        return prerequisitoRepository.findAll().stream()
                .filter(p -> p.getAsignaturaPrincipal().getId().equals(asignaturaId))
                .toList();
    }

    public Prerequisito asignarPrerequisito(Prerequisito prerequisito) {
                prerequisito.getAsignaturaRequisito().getNombre();
                prerequisito.getAsignaturaPrincipal().getNombre();
        return prerequisitoRepository.save(prerequisito);
    }

}
