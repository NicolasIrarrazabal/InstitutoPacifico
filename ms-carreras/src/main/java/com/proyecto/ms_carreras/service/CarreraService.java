package com.proyecto.ms_carreras.service;

import com.proyecto.ms_carreras.dto.CarreraDTO;
import com.proyecto.ms_carreras.model.Carrera;
import com.proyecto.ms_carreras.repository.CarreraRepository;
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
public class CarreraService {

    private final CarreraRepository repository;

    public List<Carrera> findAll() {
        log.info("Listando todas las carreras");
        return repository.findAll();
    }

    public Carrera findById(UUID id) {
        log.info("Buscando carrera por ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Carrera no encontrada con ID: " + id));
    }

    @Transactional
    public Carrera create(CarreraDTO dto) {
        log.info("Creando carrera: {}", dto.nombre());

        Carrera carrera = new Carrera();
        carrera.setNombre(dto.nombre());
        carrera.setDescripcion(dto.descripcion());
        carrera.setDuracionSemestres(dto.duracionSemestres());
        carrera.setSede(dto.sede());

        Carrera guardada = repository.save(carrera);
        log.info("Carrera creada con ID: {}", guardada.getId());
        return guardada;
    }

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

    @Transactional
    public void delete(UUID id) {
        log.info("Eliminando carrera ID: {}", id);
        Carrera carrera = findById(id);
        repository.delete(carrera);
        log.info("Carrera eliminada, ID: {}", id);
    }
}