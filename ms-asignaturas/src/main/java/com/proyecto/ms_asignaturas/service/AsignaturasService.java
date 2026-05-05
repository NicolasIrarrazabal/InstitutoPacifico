package com.proyecto.ms_asignaturas.service;

import com.proyecto.ms_asignaturas.dto.AsignaturaDTO;
import com.proyecto.ms_asignaturas.model.Asignatura;
import com.proyecto.ms_asignaturas.repository.AsignaturasRepository;
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
public class AsignaturasService {

    private final AsignaturasRepository asignaturasRepository;

    public List<Asignatura> listarTodas(){
        log.info("Listando todas las asignaturas");
        return asignaturasRepository.findAll();
    }

    public Asignatura buscarPorId(UUID id){
        log.info("Buscando asignatura por ID: {}", id);
        return asignaturasRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Asignatura no encontrada con ID: " + id));
    }

    @Transactional
    public Asignatura crear(AsignaturaDTO dto){
        log.info("Creando asignatura: {}", dto.nombre());

        Asignatura asignatura = new Asignatura();
        asignatura.setNombre(dto.nombre());
        asignatura.setCredito(dto.creditos());

        Asignatura guardada = asignaturasRepository.save(asignatura);
        log.info("Asignatura creada con ID: {}", guardada.getId());
        return guardada;
    }

    @Transactional
    public Asignatura actualizar(UUID id, AsignaturaDTO dto){
        log.info("Actualizando asignatura ID: {}", id);
        Asignatura asignatura = buscarPorId(id);

        asignatura.setNombre(dto.nombre());
        asignatura.setCredito(dto.creditos());

        return asignaturasRepository.save(asignatura);
    }

    @Transactional
    public void eliminar(UUID id){
        log.info("Eliminando asignatura ID: {}", id);
        Asignatura asignatura = buscarPorId(id);
        asignaturasRepository.delete(asignatura);
        log.info("Asignatura eliminada, ID: {}", id);
    }
}