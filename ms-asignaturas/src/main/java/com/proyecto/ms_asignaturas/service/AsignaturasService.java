package com.proyecto.ms_asignaturas.service;

import com.proyecto.ms_asignaturas.dto.AsignaturaDTO;
import com.proyecto.ms_asignaturas.model.Asignatura;
import com.proyecto.ms_asignaturas.model.Credito;
import com.proyecto.ms_asignaturas.repository.AsignaturasRepository;
import com.proyecto.ms_asignaturas.repository.CreditoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Tag(name = "Asignatura Service", description = "Lógica de negocio para asignaturas del catálogo")
@Service
@Slf4j
@RequiredArgsConstructor
public class AsignaturasService {

    private final AsignaturasRepository asignaturasRepository;
    private final CreditoRepository creditoRepository;

    @Operation(summary = "Listar todas las asignaturas", description = "Retorna todas las asignaturas del catálogo")
    public List<Asignatura> listarTodas(){
        log.info("Listando todas las asignaturas");
        return asignaturasRepository.findAll();
    }

    @Operation(summary = "Buscar asignatura por ID", description = "Retorna una asignatura por su ID")
    public Asignatura buscarPorId(UUID id){
        log.info("Buscando asignatura por ID: {}", id);
        return asignaturasRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Asignatura no encontrada con ID: " + id));
    }

    private Credito findOrCreateCredito(Integer cantidad) {
        return creditoRepository.findAll().stream()
                .filter(c -> c.getCantidad().equals(cantidad))
                .findFirst()
                .orElseGet(() -> {
                    Credito nuevo = new Credito();
                    nuevo.setCantidad(cantidad);
                    return creditoRepository.save(nuevo);
                });
    }

    @Operation(summary = "Crear asignatura", description = "Registra una nueva asignatura en el catálogo")
    @Transactional
    public Asignatura crear(AsignaturaDTO dto){
        log.info("Creando asignatura: {}", dto.nombre());

        Asignatura asignatura = new Asignatura();
        asignatura.setNombre(dto.nombre());
        asignatura.setCredito(findOrCreateCredito(dto.creditos()));

        Asignatura guardada = asignaturasRepository.save(asignatura);
        log.info("Asignatura creada con ID: {}", guardada.getId());
        return guardada;
    }

    @Operation(summary = "Actualizar asignatura", description = "Actualiza los datos de una asignatura existente")
    @Transactional
    public Asignatura actualizar(UUID id, AsignaturaDTO dto){
        log.info("Actualizando asignatura ID: {}", id);
        Asignatura asignatura = buscarPorId(id);

        asignatura.setNombre(dto.nombre());
        asignatura.setCredito(findOrCreateCredito(dto.creditos()));

        return asignaturasRepository.save(asignatura);
    }

    @Operation(summary = "Eliminar asignatura", description = "Elimina una asignatura del catálogo")
    @Transactional
    public void eliminar(UUID id){
        log.info("Eliminando asignatura ID: {}", id);
        Asignatura asignatura = buscarPorId(id);
        asignaturasRepository.delete(asignatura);
        log.info("Asignatura eliminada, ID: {}", id);
    }
}