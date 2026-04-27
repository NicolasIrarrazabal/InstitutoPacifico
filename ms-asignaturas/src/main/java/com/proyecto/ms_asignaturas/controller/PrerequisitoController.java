package com.proyecto.ms_asignaturas.controller;

import com.proyecto.ms_asignaturas.model.Prerequisito;
import com.proyecto.ms_asignaturas.service.PrerequisitoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/prerequisitos")
@RequiredArgsConstructor
public class PrerequisitoController {

    private final PrerequisitoService prerequisitoService;

    @GetMapping("/asignatura/{id}")
    public ResponseEntity<List<Prerequisito>> listarPorAsignatura(@PathVariable UUID id) {
        return ResponseEntity.ok(prerequisitoService.listarPorAsignatura(id));
    }

    @PostMapping()
    public ResponseEntity<Prerequisito> asignarPrerequisito(@Valid @RequestBody Prerequisito prerequisito) {
        return ResponseEntity.ok(prerequisitoService.asignarPrerequisito(prerequisito));
    }
}
