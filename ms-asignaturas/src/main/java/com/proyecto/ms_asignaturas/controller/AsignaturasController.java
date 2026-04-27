package com.proyecto.ms_asignaturas.controller;

import com.proyecto.ms_asignaturas.model.Asignatura;
import com.proyecto.ms_asignaturas.service.AsignaturasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/asignaturas")
@RequiredArgsConstructor
public class AsignaturasController {

    private final AsignaturasService service;

    @GetMapping()
    public ResponseEntity<List<Asignatura>> listarAsignaturas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asignatura> obtenerAsignaturaPorId(@PathVariable UUID id) {
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
