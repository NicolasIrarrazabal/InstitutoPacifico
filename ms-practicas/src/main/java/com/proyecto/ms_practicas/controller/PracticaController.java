package com.proyecto.ms_practicas.controller;

import com.proyecto.ms_practicas.dto.FinalizarPracticaDTO;
import com.proyecto.ms_practicas.dto.PracticaDTO;
import com.proyecto.ms_practicas.dto.ValidacionR5Response;
import com.proyecto.ms_practicas.model.Practica;
import com.proyecto.ms_practicas.service.PracticaService;
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
@RequestMapping("/api/v1/practicas")
@Slf4j
@RequiredArgsConstructor
public class PracticaController {

    private final PracticaService service;

    @GetMapping
    public ResponseEntity<List<Practica>> getAll() {
        log.info("GET /api/v1/practicas");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Practica> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/practicas/{}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<Practica>> getByEstudiante(@PathVariable UUID estudianteId) {
        log.info("GET /api/v1/practicas/estudiante/{}", estudianteId);
        return ResponseEntity.ok(service.findByEstudiante(estudianteId));
    }

    @GetMapping("/verificar")
    public ResponseEntity<ValidacionR5Response> verificarRequisitosR5(
            @RequestParam UUID estudianteId,
            @RequestParam UUID empresaId) {
        log.info("GET /api/v1/practicas/verificar — estudiante: {} empresa: {}", estudianteId, empresaId);
        return ResponseEntity.ok(service.verificarRequisitosR5(estudianteId, empresaId));
    }

    @PostMapping
    public ResponseEntity<Practica> create(@Valid @RequestBody PracticaDTO dto) {
        log.info("POST /api/v1/practicas — estudiante: {} empresa: {}", dto.estudianteId(), dto.empresaId());
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<Practica> finalizar(@PathVariable UUID id,
                                              @Valid @RequestBody FinalizarPracticaDTO dto) {
        log.info("PUT /api/v1/practicas/{}/finalizar", id);
        return ResponseEntity.ok(service.finalizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/practicas/{}", id);
        service.delete(id);
        return ResponseEntity.ok(Map.of("mensaje", "Práctica anulada correctamente"));
    }
}
