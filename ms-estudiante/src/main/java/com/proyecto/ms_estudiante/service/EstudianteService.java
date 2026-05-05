package com.proyecto.ms_estudiante.service;

import com.proyecto.ms_estudiante.dto.EstudianteDTO;
import com.proyecto.ms_estudiante.model.Estudiante;
import com.proyecto.ms_estudiante.model.enums.EstadoEstudiante;
import com.proyecto.ms_estudiante.repository.EstudianteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EstudianteService {

    private static final Logger log = LoggerFactory.getLogger(EstudianteService.class);

    private final EstudianteRepository repository;

    public List<Estudiante> findAll() {
        return repository.findAll();
    }

    public Estudiante findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado con ID: " + id));
    }

    public Estudiante findByRut(String rut) {
        return repository.findByRut(rut)
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado con RUT: " + rut));
    }

    public Estudiante save(EstudianteDTO dto) {
        if (repository.existsByRut(dto.rut())) {
            throw new IllegalArgumentException("Ya existe un estudiante registrado con el RUT: " + dto.rut());
        }

        if (repository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Ya existe un estudiante registrado con el email: " + dto.email());
        }

        Estudiante est = new Estudiante();
        est.setNombre(dto.nombre());
        est.setRut(dto.rut());
        est.setEmail(dto.email());
        est.setTelefono(dto.telefono());
        est.setDireccion(dto.direccion());

        est.setEstado(EstadoEstudiante.ACTIVO);

        Estudiante guardado = repository.save(est);
        log.info("Estudiante creado correctamente con ID {} y RUT {}", guardado.getId(), guardado.getRut());
        return guardado;
    }

    public Estudiante update(UUID id, EstudianteDTO dto) {
        Estudiante est = findById(id);

        if (EstadoEstudiante.INACTIVO.equals(est.getEstado())) {
            throw new IllegalStateException("No se puede modificar un estudiante en estado INACTIVO");
        }

        if (!est.getEmail().equals(dto.email()) && repository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("El email ya esta en uso por otro estudiante");
        }

        est.setNombre(dto.nombre());
        est.setEmail(dto.email());
        est.setTelefono(dto.telefono());
        est.setDireccion(dto.direccion());

        Estudiante actualizado = repository.save(est);
        log.info("Estudiante actualizado correctamente, ID {}", id);
        return actualizado;
    }

    public void delete(UUID id) {
        Estudiante est = findById(id);

        if (EstadoEstudiante.INACTIVO.equals(est.getEstado())) {
            throw new IllegalStateException("El estudiante ya se encuentra en estado INACTIVO");
        }

        est.setEstado(EstadoEstudiante.INACTIVO);
        repository.save(est);
        log.info("Estudiante marcado como INACTIVO, ID {}", id);
    }

    public boolean puedeMatricular(UUID estudianteId) {
        Estudiante est = findById(estudianteId);

        return EstadoEstudiante.ACTIVO.equals(est.getEstado());
    }
}