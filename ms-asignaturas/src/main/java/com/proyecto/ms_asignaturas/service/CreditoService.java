package com.proyecto.ms_asignaturas.service;

import com.proyecto.ms_asignaturas.model.Credito;
import com.proyecto.ms_asignaturas.repository.CreditoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreditoService {

    private final CreditoRepository  creditoRepository;

    public List<Credito> listarTodos(){
        return creditoRepository.findAll();
    }

    public Credito guardar(Credito credito){
        log.info("El credito se guardo, cantidad de creditos: {}", credito.getCantidad());
        return creditoRepository.save(credito);
    }
}
