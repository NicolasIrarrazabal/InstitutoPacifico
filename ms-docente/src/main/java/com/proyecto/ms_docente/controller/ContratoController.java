package com.proyecto.ms_docente.controller;

import com.proyecto.ms_docente.model.Contrato;
import com.proyecto.ms_docente.service.ContratoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@AllArgsConstructor
@RestController
@RequestMapping("/api/contratos")
@Slf4j
public class ContratoController {

    private ContratoService service;

    @GetMapping
    public ResponseEntity<List<Contrato>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @PostMapping
    public ResponseEntity<Contrato> crear(@Valid @RequestBody Contrato contrato) {
        return new ResponseEntity<>(service.guardar(contrato), HttpStatus.CREATED);
    }
}