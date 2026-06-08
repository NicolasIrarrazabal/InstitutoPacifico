package com.proyecto.ms_docente.controller;

import com.proyecto.ms_docente.dto.DocenteDTO;
import com.proyecto.ms_docente.model.Docente;
import com.proyecto.ms_docente.service.DocenteService;
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
import java.util.Map;
import java.util.UUID;

@Tag(name = "Docentes", description = "Gestión de docentes del Instituto Pacífico")
@RestController
@RequestMapping("/api/v1/docentes")
@Slf4j
@AllArgsConstructor
public class DocenteController {

    private DocenteService service;

    @Operation(summary = "Listar todos los docentes", description = "Retorna todos los docentes registrados")
    @GetMapping
    public ResponseEntity<List<Docente>> listar() {
        log.info("GET /api/v1/docentes");
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Buscar docente por ID", description = "Retorna un docente por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Docente encontrado"),
        @ApiResponse(responseCode = "404", description = "Docente no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Docente> buscarPorId(@PathVariable @Schema(description = "ID del docente") UUID id) {
        log.info("GET /api/v1/docentes/{}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Crear docente", description = "Registra un nuevo docente")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Docente creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Docente> crear(@Valid @RequestBody @Schema(description = "Datos del docente") DocenteDTO dto) {
        log.info("POST /api/v1/docentes");
        return new ResponseEntity<>(service.guardar(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar docente", description = "Actualiza los datos de un docente existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Docente actualizado"),
        @ApiResponse(responseCode = "404", description = "Docente no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Docente> actualizar(@PathVariable @Schema(description = "ID del docente") UUID id,
                                              @Valid @RequestBody @Schema(description = "Datos actualizados del docente") DocenteDTO dto) {
        log.info("PUT /api/v1/docentes/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar docente", description = "Elimina lógicamente un docente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Docente eliminado"),
        @ApiResponse(responseCode = "404", description = "Docente no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable @Schema(description = "ID del docente") UUID id) {
        log.info("DELETE /api/v1/docentes/{}", id);
        service.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Docente eliminado correctamente"));
    }
}