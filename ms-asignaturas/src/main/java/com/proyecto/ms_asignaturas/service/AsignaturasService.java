package com.proyecto.ms_asignaturas.service;

<<<<<<< HEAD
import com.proyecto.ms_asignaturas.model.Asignatura;
import com.proyecto.ms_asignaturas.repository.AsignaturasRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
=======
import com.proyecto.ms_asignaturas.dto.AsignaturaDTO;
import com.proyecto.ms_asignaturas.model.Asignatura;
import com.proyecto.ms_asignaturas.model.Credito;
import com.proyecto.ms_asignaturas.repository.AsignaturasRepository;
import com.proyecto.ms_asignaturas.repository.CreditoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
>>>>>>> fa0c9f7d3e1e5d3ade7459411904e5793176644e

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsignaturasService {

    private final AsignaturasRepository asignaturasRepository;
<<<<<<< HEAD

    public List<Asignatura> listarTodas(){
=======
    private final CreditoRepository creditoRepository;

    public List<Asignatura> listarTodas(){
        log.info("Listando todas las asignaturas");
>>>>>>> fa0c9f7d3e1e5d3ade7459411904e5793176644e
        return asignaturasRepository.findAll();
    }

    public Asignatura buscarPorId(UUID id){
<<<<<<< HEAD
        return asignaturasRepository.findById(id).orElseThrow(()-> new RuntimeException("No se encontro la asignatura con el id"));
    }

    public Asignatura guardar(Asignatura asignatura){
        log.info("La asignatura se guardo, nombre de la asignatura: {}", asignatura.getNombre());
        return asignaturasRepository.save(asignatura);
    }

    public Asignatura actualizarAsignatura(UUID id,Asignatura asignatura){
        Asignatura asignaturaNueva= asignaturasRepository.findById(id).orElseThrow(()-> new RuntimeException("No se encontro la asignatura con el id"));

        asignaturaNueva.setNombre(asignatura.getNombre());
        asignaturaNueva.setCredito(asignatura.getCredito());
        log.info("La asignatura se actualizo, nombre de la asignatura: {}", asignatura.getNombre());
        return asignaturasRepository.save(asignaturaNueva);

    }
    public void elimiarPorId(UUID id){
        log.warn("Se elimino la asignatura");
        asignaturasRepository.deleteById(id);
    }
}
=======
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

    @Transactional
    public Asignatura actualizar(UUID id, AsignaturaDTO dto){
        log.info("Actualizando asignatura ID: {}", id);
        Asignatura asignatura = buscarPorId(id);

        asignatura.setNombre(dto.nombre());
        asignatura.setCredito(findOrCreateCredito(dto.creditos()));

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
>>>>>>> fa0c9f7d3e1e5d3ade7459411904e5793176644e
