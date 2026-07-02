package com.proyecto.ms_carreras.service;

import com.proyecto.ms_carreras.dto.CarreraDTO;
import com.proyecto.ms_carreras.model.Carrera;
import com.proyecto.ms_carreras.repository.CarreraRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Servicio que gestiona la lógica de negocio para el catálogo de carreras.
 * Administra el CRUD de carreras y la verificación de disponibilidad
 * para el proceso de matrícula (regla R1).
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CarreraService {

    private final CarreraRepository repository;

    /**
     * Obtiene todas las carreras disponibles en el catálogo.
     *
     * @return lista de carreras, vacía si no hay registros
     */
    public List<Carrera> findAll() {
        log.info("Listando todas las carreras");
        return repository.findAll();
    }

    /**
     * Busca una carrera por su ID.
     *
     * @param id identificador único de la carrera
     * @return la carrera encontrada
     * @throws jakarta.persistence.EntityNotFoundException si no existe con ese ID
     */
    public Carrera findById(UUID id) {
        log.info("Buscando carrera por ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Carrera no encontrada con ID: " + id));
    }

    /**
     * Crea una nueva carrera con estado disponible por defecto.
     *
     * @param dto datos de la carrera (nombre, descripción, duración, sede)
     * @return la carrera creada con su ID asignado
     */
    @Transactional
    public Carrera create(CarreraDTO dto) {
        log.info("Creando carrera: {}", dto.nombre());

        Carrera carrera = new Carrera();
        carrera.setNombre(dto.nombre());
        carrera.setDescripcion(dto.descripcion());
        carrera.setDuracionSemestres(dto.duracionSemestres());
        carrera.setSede(dto.sede());
        carrera.setDisponible(true);

        Carrera guardada = repository.save(carrera);
        log.info("Carrera creada con ID: {}", guardada.getId());
        return guardada;
    }

    /**
     * Verifica si una carrera está disponible para matrícula (regla R1).
     *
     * @param id identificador de la carrera
     * @return true si la carrera existe y está marcada como disponible
     */
    public boolean estaDisponible(UUID id) {
        log.info("[R1] Verificando disponibilidad de carrera: {}", id);
        Carrera carrera = findById(id);
        boolean disponible = Boolean.TRUE.equals(carrera.getDisponible());
        log.info("[R1] Carrera {} disponible={}", id, disponible);
        return disponible;
    }

    /**
     * Cambia el estado de disponibilidad de una carrera para nuevas matrículas.
     *
     * @param id          identificador de la carrera
     * @param disponible  nuevo estado de disponibilidad
     * @return la carrera actualizada
     */
    @Transactional
    public Carrera cambiarDisponibilidad(UUID id, boolean disponible) {
        log.info("Cambiando disponibilidad de carrera {} a {}", id, disponible);
        Carrera carrera = findById(id);
        carrera.setDisponible(disponible);
        return repository.save(carrera);
    }

    /**
     * Actualiza los datos de una carrera existente.
     *
     * @param id  identificador de la carrera a actualizar
     * @param dto datos actualizados de la carrera
     * @return la carrera actualizada
     */
    @Transactional
    public Carrera update(UUID id, CarreraDTO dto) {
        log.info("Actualizando carrera ID: {}", id);
        Carrera carrera = findById(id);

        carrera.setNombre(dto.nombre());
        carrera.setDescripcion(dto.descripcion());
        carrera.setDuracionSemestres(dto.duracionSemestres());
        carrera.setSede(dto.sede());

        return repository.save(carrera);
    }

    /**
     * Elimina una carrera del catálogo.
     *
     * @param id identificador de la carrera a eliminar
     */
    @Transactional
    public void delete(UUID id) {
        log.info("Eliminando carrera ID: {}", id);
        Carrera carrera = findById(id);
        repository.delete(carrera);
        log.info("Carrera eliminada, ID: {}", id);
    }
}