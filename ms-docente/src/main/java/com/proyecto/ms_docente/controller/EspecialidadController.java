package com.proyecto.ms_docente.controller;

import com.proyecto.ms_docente.model.Especialidad;
import com.proyecto.ms_docente.service.EspecialidadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Especialidades", description = "Gestión de especialidades de docentes")
@RestController
@RequestMapping("/api/v1/especialidades")
@Slf4j
@AllArgsConstructor
public class EspecialidadController {

    private EspecialidadService service;

    @Operation(summary = "Listar todas las especialidades", description = "Retorna todas las especialidades registradas")
    @GetMapping
    public ResponseEntity<List<Especialidad>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @Operation(summary = "Crear especialidad", description = "Registra una nueva especialidad")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Especialidad creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Especialidad> crear(@Valid @RequestBody @Schema(description = "Datos de la especialidad") Especialidad especialidad) {
        return new ResponseEntity<>(service.guardar(especialidad), HttpStatus.CREATED);
    }
}
