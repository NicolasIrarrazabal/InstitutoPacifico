package com.proyecto.ms_asignaturas.controller;

import com.proyecto.ms_asignaturas.dto.AsignaturaDTO;
import com.proyecto.ms_asignaturas.model.Asignatura;
import com.proyecto.ms_asignaturas.service.AsignaturasService;
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
@RequestMapping("/api/v1/asignaturas")
@Slf4j
@RequiredArgsConstructor
public class AsignaturasController {

    private final AsignaturasService service;

    @GetMapping
    public ResponseEntity<List<Asignatura>> listarAsignaturas() {
        log.info("GET /api/v1/asignaturas");
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asignatura> obtenerAsignaturaPorId(@PathVariable UUID id) {
        log.info("GET /api/v1/asignaturas/{}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Asignatura> crearAsignatura(@Valid @RequestBody AsignaturaDTO dto) {
        log.info("POST /api/v1/asignaturas");
        return new ResponseEntity<>(service.crear(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Asignatura> actualizar(@PathVariable UUID id, @Valid @RequestBody AsignaturaDTO dto) {
        log.info("PUT /api/v1/asignaturas/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable UUID id) {
        log.info("DELETE /api/v1/asignaturas/{}", id);
        service.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Asignatura eliminada correctamente"));
    }
}