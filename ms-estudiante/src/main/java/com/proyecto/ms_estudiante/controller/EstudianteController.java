package com.proyecto.ms_estudiante.controller;

import com.proyecto.ms_estudiante.dto.EstudianteDTO;
import com.proyecto.ms_estudiante.service.EstudianteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {

    private final EstudianteService service;

    @GetMapping
    public ResponseEntity<List<EstudianteDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstudianteDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/rut/{rut}")
    public ResponseEntity<EstudianteDTO> getByRut(@PathVariable String rut) {
        return ResponseEntity.ok(service.findByRut(rut));
    }

    @GetMapping("/{id}/puede-matricular")
    public ResponseEntity<Map<String, Boolean>> puedeMatricular(@PathVariable Long id) {
        boolean resultado = service.puedeMatricular(id);
        return ResponseEntity.ok(Map.of("puedeMatricular", resultado));
    }

    @PostMapping
    public ResponseEntity<EstudianteDTO> create(@Valid @RequestBody EstudianteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstudianteDTO> update(@PathVariable Long id,
                                                @Valid @RequestBody EstudianteDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of("mensaje", "Estudiante desactivado correctamente"));
    }
}