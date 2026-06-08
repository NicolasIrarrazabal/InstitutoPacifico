package com.proyecto.ms_docente.service;

import com.proyecto.ms_docente.model.Contrato;
import com.proyecto.ms_docente.repository.ContratoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Tag(name = "Contrato Service", description = "Lógica de negocio para contratos de docentes")
@AllArgsConstructor
@Service
@Slf4j
public class ContratoService {

    private ContratoRepository repository;

    @Operation(summary = "Listar todos los contratos", description = "Retorna todos los contratos registrados")
    public List<Contrato> listarTodos() {
        return repository.findAll();
    }

    @Operation(summary = "Guardar contrato", description = "Registra un nuevo contrato para un docente")
    public Contrato guardar(Contrato contrato) {
        log.info("Guardando contrato para docente ID {}", contrato.getDocente().getId());
        Contrato guardado = repository.save(contrato);
        log.info("Contrato guardado con ID {}", guardado.getId());
        return guardado;
    }
}