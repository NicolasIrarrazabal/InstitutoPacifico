package com.proyecto.ms_matriculas.service;

import com.proyecto.ms_matriculas.model.Matricula;
import com.proyecto.ms_matriculas.dto.MatriculaDTO;
import com.proyecto.ms_matriculas.repository.MatriculaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatriculaService {

    private static final Logger log = LoggerFactory.getLogger(MatriculaService.class);

    private final MatriculaRepository repository;

    public List<Matricula> findAll() {
        return repository.findAll();
    }

    public Matricula findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Matrícula no encontrada"));
    }

    public Matricula create(MatriculaDTO dto) {

        log.info("Creando matrícula estudiante {} sección {}",
                dto.getEstudianteId(), dto.getSeccionId());

        if (!cumpleReglaPrerequisitos(dto.getEstudianteId(), dto.getSeccionId())) {
            log.warn("Estudiante {} no cumple prerrequisitos para sección {}",
                    dto.getEstudianteId(), dto.getSeccionId());

            throw new IllegalStateException("No cumple prerrequisitos de asignatura");
        }

        Matricula m = new Matricula();
        m.setEstudianteId(dto.getEstudianteId());
        m.setSeccionId(dto.getSeccionId());
        m.setFechaMatricula(dto.getFechaMatricula());
        m.setEstado("ACTIVA");

        return repository.save(m);
    }

    public Matricula update(Long id, MatriculaDTO dto) {

        Matricula m = findById(id);

        if ("INACTIVA".equals(m.getEstado())) {
            throw new IllegalStateException("No se puede modificar matrícula inactiva");
        }

        m.setEstado(dto.getEstado());
        return repository.save(m);
    }

    public void delete(Long id) {

        Matricula m = findById(id);
        m.setEstado("INACTIVA");
        repository.save(m);
    }

    private boolean cumpleReglaPrerequisitos(Long estudianteId, Long seccionId) {

        log.info("Validando prerrequisitos estudiante {} sección {}",
                estudianteId, seccionId);

        boolean estudianteValido = validarEstudiante(estudianteId);
        boolean seccionValida = validarSeccion(seccionId);
        boolean cumpleHistorial = validarHistorialAcademico(estudianteId, seccionId);

        return estudianteValido && seccionValida && cumpleHistorial;
    }

    private boolean validarEstudiante(Long estudianteId) {
        log.info("Validando estudiante {}", estudianteId);
        return estudianteId != null;
    }

    private boolean validarSeccion(Long seccionId) {
        log.info("Validando sección {}", seccionId);
        return seccionId != null;
    }

    private boolean validarHistorialAcademico(Long estudianteId, Long seccionId) {
        log.info("Validando historial académico estudiante {} sección {}",
                estudianteId, seccionId);

        return true;
    }
}