package com.proyecto.ms_docente.service;

import com.proyecto.ms_docente.model.Contrato;
import com.proyecto.ms_docente.repository.ContratoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Servicio que gestiona los contratos de docentes.
 * Proporciona operaciones para listar y registrar contratos.
 */
@AllArgsConstructor
@Service
@Slf4j
public class ContratoService {

    private ContratoRepository repository;

    /**
     * Obtiene todos los contratos registrados.
     *
     * @return lista de contratos, vacía si no hay registros
     */
    public List<Contrato> listarTodos() {
        return repository.findAll();
    }

    /**
     * Registra un nuevo contrato para un docente.
     *
     * @param contrato datos del contrato a registrar
     * @return el contrato creado con su ID asignado
     */
    public Contrato guardar(Contrato contrato) {
        log.info("Guardando contrato para docente ID {}", contrato.getDocente().getId());
        Contrato guardado = repository.save(contrato);
        log.info("Contrato guardado con ID {}", guardado.getId());
        return guardado;
    }
}