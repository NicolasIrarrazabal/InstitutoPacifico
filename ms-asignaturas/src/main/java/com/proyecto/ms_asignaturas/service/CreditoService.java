package com.proyecto.ms_asignaturas.service;

import com.proyecto.ms_asignaturas.model.Credito;
import com.proyecto.ms_asignaturas.repository.CreditoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Tag(name = "Credito Service", description = "Lógica de negocio para créditos académicos")
@Service
@Slf4j
@RequiredArgsConstructor
public class CreditoService {

    private final CreditoRepository  creditoRepository;

    @Operation(summary = "Listar todos los créditos", description = "Retorna todos los créditos académicos registrados")
    public List<Credito> listarTodos(){
        return creditoRepository.findAll();
    }

    @Operation(summary = "Guardar crédito", description = "Registra un nuevo crédito académico")
    public Credito guardar(Credito credito){
        log.info("El credito se guardo, cantidad de creditos: {}", credito.getCantidad());
        return creditoRepository.save(credito);
    }
}
