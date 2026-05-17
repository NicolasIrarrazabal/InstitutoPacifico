package com.proyecto.ms_docente.service;

import com.proyecto.ms_docente.model.Especialidad;
import com.proyecto.ms_docente.repository.EspecialidadRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class EspecialidadService {

    private EspecialidadRepository repository;

    public List<Especialidad> listarTodas() {
        return repository.findAll();
    }

    public Especialidad guardar(Especialidad especialidad) {
        log.info("Guardando especialidad: {}", especialidad.getNombre());
        Especialidad guardada = repository.save(especialidad);
        log.info("Especialidad guardada con ID {}", guardada.getId());
        return guardada;
    }
}