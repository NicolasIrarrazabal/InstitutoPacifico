package com.proyecto.ms_carreras.controller;

import com.proyecto.ms_carreras.dto.CarreraDTO;
import com.proyecto.ms_carreras.model.Carrera;
import com.proyecto.ms_carreras.service.CarreraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Carreras", description = "Gestión del catálogo de carreras del Instituto Pacífico")
@RestController
@RequestMapping("/api/v1/carreras")
@Slf4j
@RequiredArgsConstructor
public class CarreraController {

    private final CarreraService service;

    @Operation(summary = "Listar todas las carreras", description = "Retorna todas las carreras disponibles en el instituto")
    @GetMapping
    public ResponseEntity<List<Carrera>> getAll() {
        log.info("GET /api/v1/carreras");
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Obtener carrera por ID", description = "Retorna una carrera específica por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrera encontrada"),
        @ApiResponse(responseCode = "404", description = "Carrera no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Carrera> getById(@PathVariable @Schema(description = "ID de la carrera") UUID id) {
        log.info("GET /api/v1/carreras/{}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Crear carrera", description = "Registra una nueva carrera en el catálogo")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Carrera creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Carrera> create(@Valid @RequestBody @Schema(description = "Datos de la carrera") CarreraDTO dto) {
        log.info("POST /api/v1/carreras");
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar carrera", description = "Actualiza los datos de una carrera existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrera actualizada"),
        @ApiResponse(responseCode = "404", description = "Carrera no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Carrera> update(@PathVariable @Schema(description = "ID de la carrera") UUID id,
                                          @Valid @RequestBody @Schema(description = "Datos actualizados de la carrera") CarreraDTO dto) {
        log.info("PUT /api/v1/carreras/{}", id);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Eliminar carrera", description = "Elimina lógicamente una carrera")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrera eliminada"),
        @ApiResponse(responseCode = "404", description = "Carrera no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable @Schema(description = "ID de la carrera") UUID id) {
        log.info("DELETE /api/v1/carreras/{}", id);
        service.delete(id);
        return ResponseEntity.ok(Map.of("mensaje", "Carrera eliminada correctamente"));
    }
}