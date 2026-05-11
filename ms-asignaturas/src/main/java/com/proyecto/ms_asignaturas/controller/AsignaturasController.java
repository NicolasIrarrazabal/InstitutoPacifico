package com.proyecto.ms_asignaturas.controller;

<<<<<<< HEAD
=======
import com.proyecto.ms_asignaturas.dto.AsignaturaDTO;
>>>>>>> fa0c9f7d3e1e5d3ade7459411904e5793176644e
import com.proyecto.ms_asignaturas.model.Asignatura;
import com.proyecto.ms_asignaturas.service.AsignaturasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
=======
import lombok.extern.slf4j.Slf4j;
>>>>>>> fa0c9f7d3e1e5d3ade7459411904e5793176644e
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
<<<<<<< HEAD
=======
import java.util.Map;
>>>>>>> fa0c9f7d3e1e5d3ade7459411904e5793176644e
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/asignaturas")
<<<<<<< HEAD
=======
@Slf4j
>>>>>>> fa0c9f7d3e1e5d3ade7459411904e5793176644e
@RequiredArgsConstructor
public class AsignaturasController {

    private final AsignaturasService service;

<<<<<<< HEAD
    @GetMapping()
    public ResponseEntity<List<Asignatura>> listarAsignaturas() {
=======
    @GetMapping
    public ResponseEntity<List<Asignatura>> listarAsignaturas() {
        log.info("GET /api/v1/asignaturas");
>>>>>>> fa0c9f7d3e1e5d3ade7459411904e5793176644e
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asignatura> obtenerAsignaturaPorId(@PathVariable UUID id) {
<<<<<<< HEAD
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping()
    public ResponseEntity<Asignatura> crearAsignatura(@Valid @RequestBody Asignatura asignatura) {
        return new ResponseEntity<>(service.guardar(asignatura), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Asignatura> actualizar(@PathVariable UUID id, @Valid @RequestBody Asignatura asignatura) {
        return ResponseEntity.ok(service.actualizarAsignatura(id, asignatura));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        service.elimiarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/info")
    public ResponseEntity<Asignatura> obtenerInfoPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}
=======
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
>>>>>>> fa0c9f7d3e1e5d3ade7459411904e5793176644e
