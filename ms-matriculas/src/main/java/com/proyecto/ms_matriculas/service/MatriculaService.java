package com.proyecto.ms_matriculas.service;

import com.proyecto.ms_matriculas.client.AsignaturaClientService;
import com.proyecto.ms_matriculas.client.NotaClientService;
import com.proyecto.ms_matriculas.client.PrerequisitosResponse;
import com.proyecto.ms_matriculas.dto.MatriculaDTO;
import com.proyecto.ms_matriculas.model.Matricula;
import com.proyecto.ms_matriculas.repository.MatriculaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Tag(name = "Matricula Service", description = "Lógica de negocio para matrículas y validación de regla R1")
@Service
@Slf4j
@RequiredArgsConstructor
public class MatriculaService {

    private final MatriculaRepository repository;
    private final AsignaturaClientService asignaturaClient;
    private final NotaClientService notaClient;

    @Operation(summary = "Listar todas las matrículas", description = "Retorna todas las matrículas registradas")
    public List<Matricula> findAll() {
        log.info("Listando todas las matrículas");
        return repository.findAll();
    }

    @Operation(summary = "Buscar matrícula por ID", description = "Retorna una matrícula por su ID")
    public Matricula findById(UUID id) {
        log.info("Buscando matrícula por ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Matrícula no encontrada con ID: " + id));
    }

    @Operation(summary = "Listar matrículas por estudiante", description = "Retorna las matrículas activas de un estudiante")
    public List<Matricula> findByEstudiante(UUID estudianteId) {
        log.info("Listando matrículas activas del estudiante {}", estudianteId);
        return repository.findByEstudianteIdAndEstado(estudianteId, "ACTIVA");
    }

    @Operation(summary = "Crear matrícula", description = "Registra una nueva matrícula validando la regla R1 (prerrequisitos)")
    @Transactional
    public Matricula create(MatriculaDTO dto) {
        log.info("Intentando matricular estudiante {} en sección {}", dto.estudianteId(), dto.seccionId());

        if (repository.existsByEstudianteIdAndSeccionIdAndEstado(dto.estudianteId(), dto.seccionId(), "ACTIVA")) {
            log.warn("Estudiante {} ya está matriculado en la sección {}", dto.estudianteId(), dto.seccionId());
            throw new IllegalStateException("El estudiante ya está matriculado en esta sección");
        }

        validarPrerrequisitosR1(dto.estudianteId(), dto.seccionId());

        Matricula m = new Matricula();
        m.setEstudianteId(dto.estudianteId());
        m.setSeccionId(dto.seccionId());
        m.setFechaMatricula(dto.fechaMatricula());
        m.setEstado("ACTIVA");

        Matricula guardada = repository.save(m);
        log.info("Matrícula creada con ID: {}", guardada.getId());
        return guardada;
    }

    @Operation(summary = "Actualizar matrícula", description = "Actualiza el estado de una matrícula existente")
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

    @Operation(summary = "Eliminar matrícula", description = "Elimina lógicamente una matrícula")
    @Transactional
    public void delete(UUID id) {
        log.info("Eliminando (lógica) matrícula ID: {}", id);
        Matricula m = findById(id);
        m.setEstado("INACTIVA");
        repository.save(m);
        log.info("Matrícula marcada como inactiva, ID: {}", id);
    }

    private void validarPrerrequisitosR1(UUID estudianteId, UUID asignaturaId) {
        log.info("[R1] Validando prerrequisitos — estudiante: {} | asignatura: {}", estudianteId, asignaturaId);

        List<PrerequisitosResponse> prerequisitos;
        try {
            prerequisitos = asignaturaClient.obtenerPrerequisitos(asignaturaId);
        } catch (Exception e) {
            log.error("[R1] Error consultando ms-asignaturas: {}", e.getMessage());
            throw new IllegalStateException("No se pudo verificar los prerrequisitos: " + e.getMessage());
        }

        if (prerequisitos.isEmpty()) {
            log.info("[R1] La asignatura {} no tiene prerrequisitos. Matrícula permitida.", asignaturaId);
            return;
        }

        List<String> faltantes = new ArrayList<>();

        for (PrerequisitosResponse prereq : prerequisitos) {
            UUID asigRequisito = prereq.asignaturaRequisito().id();
            String nombreRequisito = prereq.asignaturaRequisito().nombre();

            log.info("[R1] Verificando si estudiante {} aprobó prerrequisito '{}' ({})",
                    estudianteId, nombreRequisito, asigRequisito);

            boolean aprobo;
            try {
                aprobo = notaClient.estudianteAproboAsignatura(estudianteId, asigRequisito);
            } catch (Exception e) {
                log.error("[R1] Error consultando ms-notas para prerrequisito {}: {}", asigRequisito, e.getMessage());
                throw new IllegalStateException(
                        "No se pudo verificar si el estudiante aprobó '" + nombreRequisito + "': " + e.getMessage());
            }

            if (!aprobo) {
                log.warn("[R1] Estudiante {} NO aprobó prerrequisito '{}'", estudianteId, nombreRequisito);
                faltantes.add(nombreRequisito);
            }
        }

        if (!faltantes.isEmpty()) {
            String detalle = String.join(", ", faltantes);
            log.warn("[R1] Matrícula bloqueada — estudiante: {} | prerrequisitos faltantes: {}", estudianteId, detalle);
            throw new IllegalStateException(
                    "No se puede matricular. El estudiante no ha aprobado los siguientes prerrequisitos: " + detalle);
        }

        log.info("[R1] Todos los prerrequisitos cumplidos para estudiante {} en asignatura {}",
                estudianteId, asignaturaId);
    }
}
