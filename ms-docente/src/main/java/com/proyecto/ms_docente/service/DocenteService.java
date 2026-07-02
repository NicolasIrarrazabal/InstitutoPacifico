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

/**
 * Servicio que gestiona la lógica de negocio para docentes.
 * Administra el CRUD de docentes con validación de email único
 * y asociación con especialidades.
 */
@Service
@Slf4j
@AllArgsConstructor
public class DocenteService {

    private DocenteRepository repository;
    private EspecialidadRepository especialidadRepository;

    /**
     * Obtiene todos los docentes registrados.
     *
     * @return lista de docentes, vacía si no hay registros
     */
    public List<Docente> listarTodos() {
        log.info("Listando todos los docentes");
        return repository.findAll();
    }

    /**
     * Busca un docente por su ID.
     *
     * @param id identificador único del docente
     * @return el docente encontrado
     * @throws jakarta.persistence.EntityNotFoundException si no existe con ese ID
     */
    public Docente buscarPorId(UUID id) {
        log.info("Buscando docente por ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Docente no encontrado con ID: " + id));
    }

    /**
     * Crea un nuevo docente validando que el email sea único
     * y asociándolo a una especialidad existente.
     *
     * @param dto datos del docente (nombre, apellido, email, especialidad)
     * @return el docente creado con su ID asignado
     * @throws IllegalArgumentException si el email ya está registrado
     */
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

    /**
     * Actualiza los datos de un docente existente.
     * Valida que el email no esté en uso por otro docente.
     *
     * @param id  identificador del docente a actualizar
     * @param dto datos actualizados del docente
     * @return el docente actualizado
     * @throws IllegalArgumentException si el email ya está en uso
     */
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

    /**
     * Elimina un docente del sistema.
     *
     * @param id identificador del docente a eliminar
     */
    @Transactional
    public void eliminar(UUID id) {
        log.info("Eliminando docente con ID: {}", id);
        Docente docente = buscarPorId(id);
        repository.delete(docente);
        log.info("Docente eliminado, ID: {}", id);
    }
}