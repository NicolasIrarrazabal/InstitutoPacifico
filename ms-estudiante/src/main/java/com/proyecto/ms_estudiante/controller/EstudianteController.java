package com.proyecto.ms_estudiante.controller;

import com.proyecto.ms_estudiante.dto.DetalleEstudianteResponse;
import com.proyecto.ms_estudiante.dto.EstudianteDTO;
import com.proyecto.ms_estudiante.model.Estudiante;
import com.proyecto.ms_estudiante.service.EstudianteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {

    private final EstudianteService service;

    @GetMapping
    public ResponseEntity<List<Estudiante>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estudiante> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/rut/{rut}")
    public ResponseEntity<Estudiante> getByRut(@PathVariable String rut) {
        return ResponseEntity.ok(service.findByRut(rut));
    }

    @GetMapping("/{id}/puede-matricular")
    public ResponseEntity<Map<String, Boolean>> puedeMatricular(@PathVariable UUID id) {
        boolean resultado = service.puedeMatricular(id);
        return ResponseEntity.ok(Map.of("puedeMatricular", resultado));
    }

    // endpoint que consolida datos de otros microservicios
    @GetMapping("/{id}/detalle")
    public ResponseEntity<DetalleEstudianteResponse> getDetalle(@PathVariable UUID id) {
        return ResponseEntity.ok(service.obtenerDetalle(id));
    }

    @PostMapping
    public ResponseEntity<Estudiante> create(@Valid @RequestBody EstudianteDTO dto) {
        Estudiante creado = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Estudiante> update(@PathVariable UUID id,
                                             @Valid @RequestBody EstudianteDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of("mensaje", "Estudiante desactivado correctamente"));
    }
}
