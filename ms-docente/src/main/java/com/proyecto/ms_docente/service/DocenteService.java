package com.proyecto.ms_docente.service;

import com.proyecto.ms_docente.model.Docente;
import com.proyecto.ms_docente.repository.DocenteRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class DocenteService {

    private DocenteRepository repository;

    public List<Docente> listarTodos() {
        return repository.findAll();
    }

    public Docente guardar(Docente docente) {
        if (repository.existsByEmail(docente.getEmail())) {
            throw new RuntimeException("Email ya existe");
        }
        return repository.save(docente);
    }
}