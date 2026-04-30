package com.proyecto.ms_docente.controller;


import com.proyecto.ms_docente.model.Docente;
import com.proyecto.ms_docente.service.DocenteService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/docentes")
@Slf4j
@AllArgsConstructor
public class DocenteController {

    private DocenteService service;

    @GetMapping
    public ResponseEntity<List<Docente>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @PostMapping
    public ResponseEntity<Docente> crear(@Valid @RequestBody Docente docente) {
        try {
            return new ResponseEntity<>(service.guardar(docente), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
