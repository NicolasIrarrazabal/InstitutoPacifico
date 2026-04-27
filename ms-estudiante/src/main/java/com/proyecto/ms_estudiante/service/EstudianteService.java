package com.proyecto.ms_estudiante.service;

import com.proyecto.ms_estudiante.model.Estudiante;
import com.proyecto.ms_estudiante.dto.EstudianteDTO;
import com.proyecto.ms_estudiante.repository.EstudianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstudianteService {

    private static final Logger log = LoggerFactory.getLogger(EstudianteService.class);
    private final EstudianteRepository repository;

    // =========================
    // CONSULTAR TODOS
    // =========================
    public List<Estudiante> findAll() {
        log.info("Listando todos los estudiantes");
        return repository.findAll();
    }

    // =========================
    // BUSCAR POR ID
    // =========================
    public Estudiante findById(Long id) {
        log.info("Buscando estudiante ID {}", id);

        return repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Estudiante no encontrado ID {}", id);
                    return new EntityNotFoundException("Estudiante no encontrado");
                });
    }

    // =========================
    // CREAR ESTUDIANTE
    // =========================
    public Estudiante save(EstudianteDTO dto) {

        log.info("Intentando crear estudiante con RUT {}", dto.getRut());

        // REGRA DE NEGOCIO (simulada base dominio)
        if (dto.getRut() == null || dto.getRut().isBlank()) {
            log.error("RUT inválido");
            throw new IllegalArgumentException("RUT es obligatorio");
        }

        Estudiante est = new Estudiante();
        est.setNombre(dto.getNombre());
        est.setRut(dto.getRut());
        est.setEmail(dto.getEmail());

        // Estado inicial obligatorio del sistema
        est.setEstado("ACTIVO");

        log.info("Estudiante creado correctamente: {}", dto.getRut());
        return repository.save(est);
    }

    // =========================
    // ACTUALIZAR ESTUDIANTE
    // =========================
    public Estudiante update(Long id, EstudianteDTO dto) {

        Estudiante est = findById(id);

        log.info("Actualizando estudiante ID {}", id);

        // REGRA: no se puede modificar estudiante inactivo
        if ("INACTIVO".equals(est.getEstado())) {
            log.warn("Intento de actualización de estudiante inactivo ID {}", id);
            throw new IllegalStateException("No se puede modificar un estudiante inactivo");
        }

        est.setNombre(dto.getNombre());
        est.setEmail(dto.getEmail());

        return repository.save(est);
    }

    // =========================
    // ELIMINACIÓN LÓGICA
    // =========================
    public void delete(Long id) {

        Estudiante est = findById(id);

        log.info("Desactivando estudiante ID {}", id);

        // REGRA: ya está inactivo
        if ("INACTIVO".equals(est.getEstado())) {
            log.warn("Estudiante ya estaba inactivo ID {}", id);
            throw new IllegalStateException("El estudiante ya está inactivo");
        }

        est.setEstado("INACTIVO");
        repository.save(est);

        log.info("Estudiante desactivado correctamente ID {}", id);
    }

    // =========================
    // REGLA DE NEGOCIO EXTENDIBLE (PREPARACIÓN MS)
    // =========================
    public boolean puedeMatricular(Long estudianteId) {

        Estudiante est = findById(estudianteId);

        // REGRA R4 (SIMULADA en este MS):
        // estudiante bloqueado por estado financiero o inactivo
        if ("INACTIVO".equals(est.getEstado())) {
            log.warn("Estudiante inactivo no puede matricular ID {}", estudianteId);
            return false;
        }

        // Aquí luego se conectará con ms-aranceles (Feign/WebClient)
        log.info("Estudiante habilitado para matrícula ID {}", estudianteId);
        return true;
    }
}