package com.proyecto.ms_docente.service;

import com.proyecto.ms_docente.model.Especialidad;
import com.proyecto.ms_docente.repository.EspecialidadRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Tag(name = "Especialidad Service", description = "Lógica de negocio para especialidades de docentes")
@Service
@Slf4j
@AllArgsConstructor
public class EspecialidadService {

    private EspecialidadRepository repository;

    @Operation(summary = "Listar todas las especialidades", description = "Retorna todas las especialidades registradas")
    public List<Especialidad> listarTodas() {
        return repository.findAll();
    }

    @Operation(summary = "Guardar especialidad", description = "Registra una nueva especialidad")
    public Especialidad guardar(Especialidad especialidad) {
        log.info("Guardando especialidad: {}", especialidad.getNombre());
        Especialidad guardada = repository.save(especialidad);
        log.info("Especialidad guardada con ID {}", guardada.getId());
        return guardada;
    }
}