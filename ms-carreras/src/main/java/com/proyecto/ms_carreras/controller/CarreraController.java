package com.proyecto.ms_carreras.controller;

import com.proyecto.ms_carreras.dto.CarreraDTO;
import com.proyecto.ms_carreras.model.Carrera;
import com.proyecto.ms_carreras.service.CarreraService;
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
@RequestMapping("/api/v1/carreras")
@Slf4j
@RequiredArgsConstructor
public class CarreraController {

    private final CarreraService service;

    @GetMapping
    public ResponseEntity<List<Carrera>> getAll() {
        log.info("GET /api/v1/carreras");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carrera> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/carreras/{}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Carrera> create(@Valid @RequestBody CarreraDTO dto) {
        log.info("POST /api/v1/carreras");
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Carrera> update(@PathVariable UUID id, @Valid @RequestBody CarreraDTO dto) {
        log.info("PUT /api/v1/carreras/{}", id);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/carreras/{}", id);
        service.delete(id);
        return ResponseEntity.ok(Map.of("mensaje", "Carrera eliminada correctamente"));
    }
}