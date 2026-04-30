package com.proyecto.ms_docente.service;

import com.proyecto.ms_docente.model.Contrato;
import com.proyecto.ms_docente.repository.ContratoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class ContratoService {

    private ContratoRepository repository;

    public List<Contrato> listarTodos() {
        return repository.findAll();
    }

    public Contrato guardar(Contrato contrato) {
        return repository.save(contrato);
    }
}