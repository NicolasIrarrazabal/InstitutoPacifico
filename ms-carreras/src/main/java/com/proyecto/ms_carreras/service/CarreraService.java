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

@Tag(name = "Carrera Service", description = "Lógica de negocio para gestión de carreras")
@Service
@Slf4j
@RequiredArgsConstructor
public class CarreraService {

    private final CarreraRepository repository;

    @Operation(summary = "Listar todas las carreras", description = "Retorna todas las carreras disponibles")
    public List<Carrera> findAll() {
        log.info("Listando todas las carreras");
        return repository.findAll();
    }

    @Operation(summary = "Buscar carrera por ID", description = "Retorna una carrera por su ID")
    public Carrera findById(UUID id) {
        log.info("Buscando carrera por ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Carrera no encontrada con ID: " + id));
    }

    @Operation(summary = "Crear carrera", description = "Registra una nueva carrera en el catálogo")
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

    @Operation(summary = "Verificar disponibilidad de carrera (R1)", description = "Valida que la carrera exista y esté marcada como disponible")
    public boolean estaDisponible(UUID id) {
        log.info("[R1] Verificando disponibilidad de carrera: {}", id);
        Carrera carrera = findById(id);
        boolean disponible = Boolean.TRUE.equals(carrera.getDisponible());
        log.info("[R1] Carrera {} disponible={}", id, disponible);
        return disponible;
    }

    @Operation(summary = "Cambiar disponibilidad de carrera", description = "Marca una carrera como disponible o no disponible para nuevas matrículas")
    @Transactional
    public Carrera cambiarDisponibilidad(UUID id, boolean disponible) {
        log.info("Cambiando disponibilidad de carrera {} a {}", id, disponible);
        Carrera carrera = findById(id);
        carrera.setDisponible(disponible);
        return repository.save(carrera);
    }

    @Operation(summary = "Actualizar carrera", description = "Actualiza los datos de una carrera existente")
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

    @Operation(summary = "Eliminar carrera", description = "Elimina una carrera del catálogo")
    @Transactional
    public void delete(UUID id) {
        log.info("Eliminando carrera ID: {}", id);
        Carrera carrera = findById(id);
        repository.delete(carrera);
        log.info("Carrera eliminada, ID: {}", id);
    }
}