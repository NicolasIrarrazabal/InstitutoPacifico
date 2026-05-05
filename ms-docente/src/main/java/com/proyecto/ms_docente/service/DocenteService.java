package com.proyecto.ms_docente.service;

import com.proyecto.ms_docente.dto.DocenteDTO;
import com.proyecto.ms_docente.model.Docente;
import com.proyecto.ms_docente.model.Especialidad;
import com.proyecto.ms_docente.repository.DocenteRepository;
import com.proyecto.ms_docente.repository.EspecialidadRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class DocenteService {

    private DocenteRepository repository;
    private EspecialidadRepository especialidadRepository;

    public List<Docente> listarTodos() {
        log.info("Listando todos los docentes");
        return repository.findAll();
    }

    public Docente buscarPorId(UUID id) {
        log.info("Buscando docente por ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Docente no encontrado con ID: " + id));
    }

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

    @Transactional
    public void eliminar(UUID id) {
        log.info("Eliminando docente con ID: {}", id);
        Docente docente = buscarPorId(id);
        repository.delete(docente);
        log.info("Docente eliminado, ID: {}", id);
    }
}