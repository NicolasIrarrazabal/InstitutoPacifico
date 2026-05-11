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

    // mapea entity a dto
    private EstudianteDTO toDTO(Estudiante est) {
        return new EstudianteDTO(
                est.getNombre(),
                est.getRut(),
                est.getEmail(),
                est.getTelefono(),
                est.getDireccion()
        );
    }

    // =========================================================
    // LISTAR TODOS
    // =========================================================
    public List<EstudianteDTO> findAll() {
        log.info("Listando estudiantes");

        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // =========================================================
    // BUSCAR POR ID
    // =========================================================
    public EstudianteDTO findById(Long id) {
        log.info("Buscando estudiante con id {}", id);

        Estudiante est = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("No existe estudiante con id {}", id);
                    return new EntityNotFoundException("Estudiante no encontrado: " + id);
                });

        return toDTO(est);
    }

    // =========================================================
    // BUSCAR POR RUT
    // =========================================================
    public EstudianteDTO findByRut(String rut) {
        log.info("Buscando estudiante con rut {}", rut);

        Estudiante est = repository.findByRut(rut)
                .orElseThrow(() -> {
                    log.error("No existe estudiante con rut {}", rut);
                    return new EntityNotFoundException("Estudiante no encontrado: " + rut);
                });

        return toDTO(est);
    }

    // =========================================================
    // CREAR ESTUDIANTE
    // =========================================================
    public EstudianteDTO save(EstudianteDTO dto) {
        log.info("Creando estudiante con rut {}", dto.getRut());

        if (repository.existsByRut(dto.getRut())) {
            throw new IllegalArgumentException("Ya existe un estudiante con ese rut");
        }

        if (repository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Ya existe un estudiante con ese email");
        }

        Estudiante est = new Estudiante();
        est.setNombre(dto.getNombre());
        est.setRut(dto.getRut());
        est.setEmail(dto.getEmail());
        est.setTelefono(dto.getTelefono());
        est.setDireccion(dto.getDireccion());
        est.setEstado("ACTIVO");

        Estudiante saved = repository.save(est);

        log.info("Estudiante creado con id {}", saved.getId());

        return toDTO(saved);
    }

    // =========================================================
    // ACTUALIZAR
    // =========================================================
    public EstudianteDTO update(Long id, EstudianteDTO dto) {
        log.info("Actualizando estudiante {}", id);

        Estudiante est = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe estudiante"));

        if ("INACTIVO".equals(est.getEstado())) {
            throw new IllegalStateException("No se puede modificar un estudiante inactivo");
        }

        if (!est.getEmail().equals(dto.getEmail())
                && repository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email ya en uso");
        }

        est.setNombre(dto.getNombre());
        est.setEmail(dto.getEmail());
        est.setTelefono(dto.getTelefono());
        est.setDireccion(dto.getDireccion());

        return toDTO(repository.save(est));
    }

    // =========================================================
    // ELIMINACION LOGICA
    // =========================================================
    public void delete(Long id) {
        log.info("Desactivando estudiante {}", id);

        Estudiante est = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe estudiante"));

        if ("INACTIVO".equals(est.getEstado())) {
            throw new IllegalStateException("Ya está inactivo");
        }

        est.setEstado("INACTIVO");
        repository.save(est);
    }

    // =========================================================/
    // VALIDACION MATRICULA
    // =========================================================
    public boolean puedeMatricular(Long id) {
        Estudiante est = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe estudiante"));

        return !"INACTIVO".equals(est.getEstado());
    }
}