package com.proyecto.ms_docente.service;

import com.proyecto.ms_docente.model.Especialidad;
import com.proyecto.ms_docente.repository.EspecialidadRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Servicio que gestiona las especialidades de los docentes.
 * Proporciona operaciones para listar y registrar especialidades.
 */
@Service
@Slf4j
@AllArgsConstructor
public class EspecialidadService {

    private EspecialidadRepository repository;

    /**
     * Obtiene todas las especialidades registradas.
     *
     * @return lista de especialidades, vacía si no hay registros
     */
    public List<Especialidad> listarTodas() {
        return repository.findAll();
    }

    /**
     * Registra una nueva especialidad.
     *
     * @param especialidad datos de la especialidad a registrar
     * @return la especialidad creada con su ID asignado
     */
    public Especialidad guardar(Especialidad especialidad) {
        log.info("Guardando especialidad: {}", especialidad.getNombre());
        Especialidad guardada = repository.save(especialidad);
        log.info("Especialidad guardada con ID {}", guardada.getId());
        return guardada;
    }
}