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

/**
 * Servicio que gestiona la lógica de negocio para el catálogo de asignaturas.
 * Administra la creación, actualización y eliminación de asignaturas,
 * así como la asociación con créditos académicos.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AsignaturasService {

    private final AsignaturasRepository asignaturasRepository;
    private final CreditoRepository creditoRepository;

    /**
     * Obtiene todas las asignaturas del catálogo.
     *
     * @return lista de asignaturas, vacía si no hay registros
     */
    public List<Asignatura> listarTodas(){
        log.info("Listando todas las asignaturas");
        return asignaturasRepository.findAll();
    }

    /**
     * Busca una asignatura por su ID.
     *
     * @param id identificador único de la asignatura
     * @return la asignatura encontrada
     * @throws jakarta.persistence.EntityNotFoundException si no existe con ese ID
     */
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

    /**
     * Crea una nueva asignatura con su crédito asociado.
     * Si el crédito no existe, lo crea automáticamente.
     *
     * @param dto datos de la asignatura (nombre, créditos)
     * @return la asignatura creada con su ID asignado
     */
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

    /**
     * Actualiza los datos de una asignatura existente.
     *
     * @param id  identificador de la asignatura a actualizar
     * @param dto datos actualizados de la asignatura
     * @return la asignatura actualizada
     */
    @Transactional
    public Asignatura actualizar(UUID id, AsignaturaDTO dto){
        log.info("Actualizando asignatura ID: {}", id);
        Asignatura asignatura = buscarPorId(id);

        asignatura.setNombre(dto.nombre());
        asignatura.setCredito(findOrCreateCredito(dto.creditos()));

        return asignaturasRepository.save(asignatura);
    }

    /**
     * Elimina una asignatura del catálogo.
     *
     * @param id identificador de la asignatura a eliminar
     */
    @Transactional
    public void eliminar(UUID id){
        log.info("Eliminando asignatura ID: {}", id);
        Asignatura asignatura = buscarPorId(id);
        asignaturasRepository.delete(asignatura);
        log.info("Asignatura eliminada, ID: {}", id);
    }
}