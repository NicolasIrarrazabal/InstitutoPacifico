package com.proyecto.ms_estudiante.service;

import com.proyecto.ms_estudiante.dto.EstudianteDTO;
import com.proyecto.ms_estudiante.model.Estudiante;
import com.proyecto.ms_estudiante.repository.EstudianteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstudianteService {

    private static final Logger log = LoggerFactory.getLogger(EstudianteService.class);

    private final EstudianteRepository repository;

    // =========================================================
    // LISTAR TODOS
    // =========================================================
    public List<Estudiante> findAll() {
        log.info("Listando todos los estudiantes");
        return repository.findAll();
    }

    // =========================================================
    // BUSCAR POR ID
    // =========================================================
    public Estudiante findById(Long id) {
        log.info("Buscando estudiante con ID {}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Estudiante no encontrado con ID {}", id);
                    return new EntityNotFoundException("Estudiante no encontrado con ID: " + id);
                });
    }

    // =========================================================
    // BUSCAR POR RUT
    // =========================================================
    public Estudiante findByRut(String rut) {
        log.info("Buscando estudiante con RUT {}", rut);
        return repository.findByRut(rut)
                .orElseThrow(() -> {
                    log.error("Estudiante no encontrado con RUT {}", rut);
                    return new EntityNotFoundException("Estudiante no encontrado con RUT: " + rut);
                });
    }

    // =========================================================
    // CREAR ESTUDIANTE
    // =========================================================
    public Estudiante save(EstudianteDTO dto) {
        log.info("Intentando crear estudiante con RUT {}", dto.getRut());

        // Regla de negocio: RUT unico
        if (repository.existsByRut(dto.getRut())) {
            log.error("Ya existe un estudiante con RUT {}", dto.getRut());
            throw new IllegalArgumentException("Ya existe un estudiante registrado con el RUT: " + dto.getRut());
        }

        // Regla de negocio: Email unico
        if (repository.existsByEmail(dto.getEmail())) {
            log.error("Ya existe un estudiante con email {}", dto.getEmail());
            throw new IllegalArgumentException("Ya existe un estudiante registrado con el email: " + dto.getEmail());
        }

        Estudiante est = new Estudiante();
        est.setNombre(dto.getNombre());
        est.setRut(dto.getRut());
        est.setEmail(dto.getEmail());
        est.setTelefono(dto.getTelefono());
        est.setDireccion(dto.getDireccion());
        est.setEstado("ACTIVO"); // estado inicial siempre ACTIVO

        Estudiante guardado = repository.save(est);
        log.info("Estudiante creado correctamente con ID {} y RUT {}", guardado.getId(), guardado.getRut());
        return guardado;
    }

    // =========================================================
    // ACTUALIZAR ESTUDIANTE
    // =========================================================
    public Estudiante update(Long id, EstudianteDTO dto) {
        Estudiante est = findById(id);
        log.info("Actualizando estudiante con ID {}", id);

        // Regla de negocio: no se puede modificar un estudiante inactivo
        if ("INACTIVO".equals(est.getEstado())) {
            log.warn("Intento de actualizacion de estudiante inactivo, ID {}", id);
            throw new IllegalStateException("No se puede modificar un estudiante en estado INACTIVO");
        }

        // Si cambia el email, verificar que no exista en otro registro
        if (!est.getEmail().equals(dto.getEmail()) && repository.existsByEmail(dto.getEmail())) {
            log.error("Email {} ya esta en uso por otro estudiante", dto.getEmail());
            throw new IllegalArgumentException("El email ya esta en uso por otro estudiante");
        }

        est.setNombre(dto.getNombre());
        est.setEmail(dto.getEmail());
        est.setTelefono(dto.getTelefono());
        est.setDireccion(dto.getDireccion());

        Estudiante actualizado = repository.save(est);
        log.info("Estudiante actualizado correctamente, ID {}", id);
        return actualizado;
    }

    // =========================================================
    // ELIMINACION
    // =========================================================
    public void delete(Long id) {
        Estudiante est = findById(id);
        log.info("Desactivando (baja logica) estudiante con ID {}", id);

        if ("INACTIVO".equals(est.getEstado())) {
            log.warn("Estudiante ya estaba en estado INACTIVO, ID {}", id);
            throw new IllegalStateException("El estudiante ya se encuentra en estado INACTIVO");
        }

        est.setEstado("INACTIVO");
        repository.save(est);
        log.info("Estudiante desactivado correctamente, ID {}", id);
    }

    // =========================================================
    // REGLA R4 — VERIFICACION PARA MATRICULA
    // Expuesto via endpoint /puede-matricular para ms-matriculas
    // =========================================================
    public boolean puedeMatricular(Long estudianteId) {
        Estudiante est = findById(estudianteId);

        if ("INACTIVO".equals(est.getEstado())) {
            log.warn("Estudiante INACTIVO no puede matricularse, ID {}", estudianteId);
            return false;
        }

        // para verificar deudas > 45 dias (Regla R4)
        log.info("Estudiante habilitado para matricula, ID {}", estudianteId);
        return true;
    }
}