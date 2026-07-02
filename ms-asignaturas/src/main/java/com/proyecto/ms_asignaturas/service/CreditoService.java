package com.proyecto.ms_asignaturas.service;

import com.proyecto.ms_asignaturas.model.Credito;
import com.proyecto.ms_asignaturas.repository.CreditoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio que gestiona los créditos académicos asociados a las asignaturas.
 * Proporciona operaciones para listar y registrar créditos.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CreditoService {

    private final CreditoRepository  creditoRepository;

    /**
     * Obtiene todos los créditos académicos registrados.
     *
     * @return lista de créditos, vacía si no hay registros
     */
    public List<Credito> listarTodos(){
        return creditoRepository.findAll();
    }

    /**
     * Registra un nuevo crédito académico.
     *
     * @param credito datos del crédito a registrar
     * @return el crédito creado con su ID asignado
     */
    public Credito guardar(Credito credito){
        log.info("El credito se guardo, cantidad de creditos: {}", credito.getCantidad());
        return creditoRepository.save(credito);
    }
}
