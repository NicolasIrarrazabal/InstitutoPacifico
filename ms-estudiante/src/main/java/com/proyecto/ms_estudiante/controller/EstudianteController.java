package com.proyecto.ms_estudiante.controller;

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

@RestController
@RequestMapping("/api/v1/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {

    private final EstudianteService service;

    // GET /api/v1/estudiantes
    @GetMapping
    public ResponseEntity<List<Estudiante>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // GET /api/v1/estudiantes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Estudiante> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // GET /api/v1/estudiantes/rut/{rut}
    @GetMapping("/rut/{rut}")
    public ResponseEntity<Estudiante> getByRut(@PathVariable String rut) {
        return ResponseEntity.ok(service.findByRut(rut));
    }

    // GET /api/v1/estudiantes/{id}/puede-matricular
    // Usado por ms-matriculas via WebClient para validar R1/R4
    @GetMapping("/{id}/puede-matricular")
    public ResponseEntity<Map<String, Boolean>> puedeMatricular(@PathVariable Long id) {
        boolean resultado = service.puedeMatricular(id);
        return ResponseEntity.ok(Map.of("puedeMatricular", resultado));
    }

    // POST /api/v1/estudiantes
    @PostMapping
    public ResponseEntity<Estudiante> create(@Valid @RequestBody EstudianteDTO dto) {
        Estudiante creado = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // PUT /api/v1/estudiantes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Estudiante> update(@PathVariable Long id,
                                             @Valid @RequestBody EstudianteDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // DELETE /api/v1/estudiantes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of("mensaje", "Estudiante desactivado correctamente"));
    }
}