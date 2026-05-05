package com.proyecto.ms_matriculas.controller;

import com.proyecto.ms_matriculas.dto.MatriculaDTO;
import com.proyecto.ms_matriculas.model.Matricula;
import com.proyecto.ms_matriculas.service.MatriculaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/matriculas")
@Slf4j
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaService service;

    @GetMapping
    public ResponseEntity<List<Matricula>> getAll() {
        log.info("GET /api/v1/matriculas");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Matricula> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/matriculas/{}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Matricula> create(@Valid @RequestBody MatriculaDTO dto) {
        log.info("POST /api/v1/matriculas");
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Matricula> update(@PathVariable UUID id, @Valid @RequestBody MatriculaDTO dto) {
        log.info("PUT /api/v1/matriculas/{}", id);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/matriculas/{}", id);
        service.delete(id);
        return ResponseEntity.ok(Map.of("mensaje", "Matrícula eliminada correctamente"));
    }
}