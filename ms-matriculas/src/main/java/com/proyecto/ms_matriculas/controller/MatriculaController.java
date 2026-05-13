package com.proyecto.ms_matriculas.controller;

import com.proyecto.ms_matriculas.dto.MatriculaDTO;
import com.proyecto.ms_matriculas.model.Matricula;
import com.proyecto.ms_matriculas.service.MatriculaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/matriculas")
@RequiredArgsConstructor
public class MatriculaController {

    private static final Logger log = LoggerFactory.getLogger(MatriculaController.class);

    private final MatriculaService service;

    @GetMapping
    public ResponseEntity<List<Matricula>> getAll() {
        log.info("GET /matriculas");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Matricula> getById(@PathVariable Long id) {
        log.info("GET /matriculas/{}", id);
        return ResponseEntity.ok(service.findById(id));
    }


    @PostMapping
    public ResponseEntity<Matricula> create(@Valid @RequestBody MatriculaDTO dto) {
        log.info("POST /matriculas");

        Matricula created = service.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Matricula> update(
            @PathVariable Long id,
            @Valid @RequestBody MatriculaDTO dto) {

        log.info("PUT /matriculas/{}", id);

        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        log.info("DELETE /matriculas/{}", id);

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}