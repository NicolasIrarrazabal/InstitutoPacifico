package com.proyecto.ms_matriculas.service;

import com.proyecto.ms_matriculas.client.EstudianteClientService;
import com.proyecto.ms_matriculas.client.PuedeMatricularResponse;
import com.proyecto.ms_matriculas.model.Matricula;
import com.proyecto.ms_matriculas.dto.MatriculaDTO;
import com.proyecto.ms_matriculas.repository.MatriculaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatriculaService {

    private final MatriculaRepository repository;
    private final EstudianteClientService estudianteClient;

    public List<Matricula> findAll() {
        log.info("Listando todas las matrículas");
        return repository.findAll();
    }

    public Matricula findById(UUID id) {
        log.info("Buscando matrícula por ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Matrícula no encontrada con ID: " + id));
    }

    @Transactional
    public Matricula create(MatriculaDTO dto) {
        log.info("Creando matrícula estudiante {} sección {}", dto.estudianteId(), dto.seccionId());

        validarPrerrequisitos(dto.estudianteId(), dto.seccionId());

        Matricula m = new Matricula();
        m.setEstudianteId(dto.estudianteId());
        m.setSeccionId(dto.seccionId());
        m.setFechaMatricula(dto.fechaMatricula());
        m.setEstado("ACTIVA");

        Matricula guardada = repository.save(m);
        log.info("Matrícula creada con ID: {}", guardada.getId());
        return guardada;
    }

    @Transactional
    public Matricula update(UUID id, MatriculaDTO dto) {
        log.info("Actualizando matrícula ID: {}", id);
        Matricula m = findById(id);

        if ("INACTIVA".equals(m.getEstado())) {
            throw new IllegalStateException("No se puede modificar una matrícula inactiva");
        }

        m.setEstado(dto.estado());
        return repository.save(m);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Eliminando (lógica) matrícula ID: {}", id);
        Matricula m = findById(id);
        m.setEstado("INACTIVA");
        repository.save(m);
        log.info("Matrícula marcada como inactiva, ID: {}", id);
    }

    private void validarPrerrequisitos(UUID estudianteId, UUID seccionId) {
        log.info("Validando prerrequisitos del estudiante {} para sección {}", estudianteId, seccionId);
        try {
            PuedeMatricularResponse response = estudianteClient.puedeMatricular(estudianteId);
            if (response == null || response.puedeMatricular() == null || !response.puedeMatricular()) {
                log.warn("Estudiante {} no puede matricularse", estudianteId);
                throw new IllegalStateException("El estudiante no puede matricularse");
            }
        } catch (Exception e) {
            log.error("Error al validar prerrequisitos: {}", e.getMessage());
            throw new IllegalStateException("Error al validar prerrequisitos: " + e.getMessage());
        }
    }
}