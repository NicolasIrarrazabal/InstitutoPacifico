package com.proyecto.ms_docente.controller;

import com.proyecto.ms_docente.model.Especialidad;
import com.proyecto.ms_docente.service.EspecialidadService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/especialidades")
@Slf4j
@AllArgsConstructor
public class EspecialidadController {

    private EspecialidadService service;

    @GetMapping
    public ResponseEntity<List<Especialidad>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @PostMapping
    public ResponseEntity<Especialidad> crear(@Valid @RequestBody Especialidad especialidad) {
        return new ResponseEntity<>(service.guardar(especialidad), HttpStatus.CREATED);
    }
}
