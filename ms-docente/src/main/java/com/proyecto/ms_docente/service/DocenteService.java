package com.proyecto.ms_docente.service;

import com.proyecto.ms_docente.dto.DocenteDTO;
import com.proyecto.ms_docente.model.Docente;
import com.proyecto.ms_docente.model.Especialidad;
import com.proyecto.ms_docente.repository.DocenteRepository;
import com.proyecto.ms_docente.repository.EspecialidadRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Tag(name = "Docente Service", description = "Lógica de negocio para gestión de docentes")
@Service
@Slf4j
@AllArgsConstructor
public class DocenteService {

    private DocenteRepository repository;
    private EspecialidadRepository especialidadRepository;

    @Operation(summary = "Listar todos los docentes", description = "Retorna todos los docentes registrados")
    public List<Docente> listarTodos() {
        log.info("Listando todos los docentes");
        return repository.findAll();
    }

    @Operation(summary = "Buscar docente por ID", description = "Retorna un docente por su ID")
    public Docente buscarPorId(UUID id) {
        log.info("Buscando docente por ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Docente no encontrado con ID: " + id));
    }

    @Operation(summary = "Guardar docente", description = "Registra un nuevo docente validando email único")
    @Transactional
    public Docente guardar(DocenteDTO dto) {
        log.info("Guardando nuevo docente con email: {}", dto.email());

        if (repository.existsByEmail(dto.email())) {
            log.warn("Email ya existe: {}", dto.email());
            throw new IllegalArgumentException("Ya existe un docente registrado con el email: " + dto.email());
        }

        Especialidad especialidad = especialidadRepository.findById(dto.especialidadId())
                .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada con ID: " + dto.especialidadId()));

        Docente docente = new Docente();
        docente.setNombre(dto.nombre());
        docente.setApellido(dto.apellido());
        docente.setEmail(dto.email());
        docente.setEspecialidad(especialidad);

        Docente guardado = repository.save(docente);
        log.info("Docente creado correctamente con ID {}", guardado.getId());
        return guardado;
    }

    @Operation(summary = "Actualizar docente", description = "Actualiza los datos de un docente existente")
    @Transactional
    public Docente actualizar(UUID id, DocenteDTO dto) {
        log.info("Actualizando docente con ID: {}", id);

        Docente docente = buscarPorId(id);

        if (!docente.getEmail().equals(dto.email()) && repository.existsByEmail(dto.email())) {
            log.warn("Email ya en uso: {}", dto.email());
            throw new IllegalArgumentException("El email ya está en uso por otro docente");
        }

        if (dto.especialidadId() != null) {
            Especialidad especialidad = especialidadRepository.findById(dto.especialidadId())
                    .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada con ID: " + dto.especialidadId()));
            docente.setEspecialidad(especialidad);
        }

        docente.setNombre(dto.nombre());
        docente.setApellido(dto.apellido());
        docente.setEmail(dto.email());

        return repository.save(docente);
    }

    @Operation(summary = "Eliminar docente", description = "Elimina un docente del sistema")
    @Transactional
    public void eliminar(UUID id) {
        log.info("Eliminando docente con ID: {}", id);
        Docente docente = buscarPorId(id);
        repository.delete(docente);
        log.info("Docente eliminado, ID: {}", id);
    }
}