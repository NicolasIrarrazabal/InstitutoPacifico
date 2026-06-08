package com.proyecto.ms_notas.controller;

import com.proyecto.ms_notas.dto.AvanceResponseDTO;
import com.proyecto.ms_notas.dto.NotaDTO;
import com.proyecto.ms_notas.dto.PromedioResponseDTO;
import com.proyecto.ms_notas.model.Nota;
import com.proyecto.ms_notas.service.NotaService;
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
@RequestMapping("/api/v1/notas")
@Slf4j
@RequiredArgsConstructor
public class NotaController {

    private final NotaService service;

    @GetMapping
    public ResponseEntity<List<Nota>> getAll() {
        log.info("GET /api/v1/notas");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Nota> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/notas/{}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<Nota>> getByEstudiante(@PathVariable UUID estudianteId) {
        log.info("GET /api/v1/notas/estudiante/{}", estudianteId);
        return ResponseEntity.ok(service.findByEstudiante(estudianteId));
    }

    @PostMapping
    public ResponseEntity<Nota> create(@Valid @RequestBody NotaDTO dto) {
        log.info("POST /api/v1/notas - estudiante: {}", dto.estudianteId());
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Nota> update(@PathVariable UUID id, @Valid @RequestBody NotaDTO dto) {
        log.info("PUT /api/v1/notas/{}", id);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/notas/{}", id);
        service.delete(id);
        return ResponseEntity.ok(Map.of("mensaje", "Nota anulada correctamente"));
    }

    @GetMapping("/estudiante/{estudianteId}/promedio")
    public ResponseEntity<PromedioResponseDTO> getPromedio(@PathVariable UUID estudianteId) {
        log.info("GET /api/v1/notas/estudiante/{}/promedio [R3 global]", estudianteId);
        return ResponseEntity.ok(service.calcularPromedio(estudianteId));
    }

    @GetMapping("/estudiante/{estudianteId}/promedio/seccion/{seccionId}")
    public ResponseEntity<PromedioResponseDTO> getPromedioSeccion(
            @PathVariable UUID estudianteId,
            @PathVariable UUID seccionId) {
        log.info("GET /api/v1/notas/estudiante/{}/promedio/seccion/{} [R3 por sección]",
                estudianteId, seccionId);
        return ResponseEntity.ok(service.calcularPromedioSeccion(estudianteId, seccionId));
    }

    @GetMapping("/estudiante/{estudianteId}/avance")
    public ResponseEntity<AvanceResponseDTO> getAvance(@PathVariable UUID estudianteId) {
        log.info("GET /api/v1/notas/estudiante/{}/avance [R5]", estudianteId);
        return ResponseEntity.ok(service.calcularAvance(estudianteId));
    }
}
