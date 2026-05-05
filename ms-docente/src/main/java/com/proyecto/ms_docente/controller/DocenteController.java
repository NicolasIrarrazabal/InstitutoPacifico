package com.proyecto.ms_docente.controller;

import com.proyecto.ms_docente.dto.DocenteDTO;
import com.proyecto.ms_docente.model.Docente;
import com.proyecto.ms_docente.service.DocenteService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/docentes")
@Slf4j
@AllArgsConstructor
public class DocenteController {

    private DocenteService service;

    @GetMapping
    public ResponseEntity<List<Docente>> listar() {
        log.info("GET /api/v1/docentes");
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Docente> buscarPorId(@PathVariable UUID id) {
        log.info("GET /api/v1/docentes/{}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Docente> crear(@Valid @RequestBody DocenteDTO dto) {
        log.info("POST /api/v1/docentes");
        return new ResponseEntity<>(service.guardar(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Docente> actualizar(@PathVariable UUID id, @Valid @RequestBody DocenteDTO dto) {
        log.info("PUT /api/v1/docentes/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable UUID id) {
        log.info("DELETE /api/v1/docentes/{}", id);
        service.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Docente eliminado correctamente"));
    }
}