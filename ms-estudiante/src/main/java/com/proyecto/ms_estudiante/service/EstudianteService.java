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

    public List<Estudiante> findAll() {
        return repository.findAll();
    }

    public Estudiante findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado"));
    }

    public Estudiante save(EstudianteDTO dto) {
        Estudiante est = new Estudiante();
        est.setNombre(dto.getNombre()); // Usamos acceso directo, Lombok genera el getter
        est.setRut(dto.getRut());
        est.setEmail(dto.getEmail());
        est.setEstado("ACTIVO");

        log.info("Creando estudiante {}", dto.getRut());
        return repository.save(est);
    }

    public Estudiante update(Long id, EstudianteDTO dto) {
        Estudiante est = findById(id);
        est.setNombre(dto.getNombre()); // Acceso directo
        est.setEmail(dto.getEmail());

        log.info("Actualizando estudiante {}", id);
        return repository.save(est);
    }

    public void delete(Long id) {
        Estudiante est = findById(id);
        est.setEstado("INACTIVO");
        repository.save(est);
    }
}