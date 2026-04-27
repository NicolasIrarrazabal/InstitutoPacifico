package com.proyecto.ms_asignaturas.service;

import com.proyecto.ms_asignaturas.model.Asignatura;
import com.proyecto.ms_asignaturas.repository.AsignaturasRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsignaturasService {

    private AsignaturasRepository asignaturasRepository;

    public List<Asignatura> listarTodas(){
        return asignaturasRepository.findAll();
    }

    public Asignatura buscarPorId(UUID id){
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
