package com.proyecto.ms_notas.controller;

import com.proyecto.ms_notas.dto.AvanceResponseDTO;
import com.proyecto.ms_notas.dto.NotaDTO;
import com.proyecto.ms_notas.dto.PromedioResponseDTO;
import com.proyecto.ms_notas.model.Nota;
import com.proyecto.ms_notas.service.NotaService;
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

@Tag(name = "Notas", description = "Gestión de notas, promedios (R3) y avance académico (R5)")
@RestController
@RequestMapping("/api/v1/notas")
@Slf4j
@RequiredArgsConstructor
public class NotaController {

    private final NotaService service;

    @Operation(summary = "Listar todas las notas", description = "Retorna todas las notas registradas en el sistema")
    @GetMapping
    public ResponseEntity<List<Nota>> getAll() {
        log.info("GET /api/v1/notas");
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Obtener nota por ID", description = "Retorna una nota específica por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nota encontrada"),
        @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Nota> getById(@PathVariable @Schema(description = "ID de la nota") UUID id) {
        log.info("GET /api/v1/notas/{}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Listar notas por estudiante", description = "Retorna todas las notas de un estudiante")
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<Nota>> getByEstudiante(@PathVariable @Schema(description = "ID del estudiante") UUID estudianteId) {
        log.info("GET /api/v1/notas/estudiante/{}", estudianteId);
        return ResponseEntity.ok(service.findByEstudiante(estudianteId));
    }

    @Operation(summary = "Crear nota", description = "Registra una nueva nota para un estudiante")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Nota creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Nota> create(@Valid @RequestBody @Schema(description = "Datos de la nota a registrar") NotaDTO dto) {
        log.info("POST /api/v1/notas - estudiante: {}", dto.estudianteId());
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar nota", description = "Actualiza una nota existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nota actualizada"),
        @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Nota> update(@PathVariable @Schema(description = "ID de la nota") UUID id,
                                       @Valid @RequestBody @Schema(description = "Datos actualizados de la nota") NotaDTO dto) {
        log.info("PUT /api/v1/notas/{}", id);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Anular nota", description = "Anula lógicamente una nota")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nota anulada correctamente"),
        @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable @Schema(description = "ID de la nota") UUID id) {
        log.info("DELETE /api/v1/notas/{}", id);
        service.delete(id);
        return ResponseEntity.ok(Map.of("mensaje", "Nota anulada correctamente"));
    }

    @Operation(summary = "Obtener promedio global R3", description = "Calcula el promedio ponderado global del estudiante y evalúa la regla R3")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Promedio calculado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    @GetMapping("/estudiante/{estudianteId}/promedio")
    public ResponseEntity<PromedioResponseDTO> getPromedio(@PathVariable @Schema(description = "ID del estudiante") UUID estudianteId) {
        log.info("GET /api/v1/notas/estudiante/{}/promedio [R3 global]", estudianteId);
        return ResponseEntity.ok(service.calcularPromedio(estudianteId));
    }

    @Operation(summary = "Obtener promedio por sección R3", description = "Calcula el promedio ponderado del estudiante en una sección específica (R3)")
    @GetMapping("/estudiante/{estudianteId}/promedio/seccion/{seccionId}")
    public ResponseEntity<PromedioResponseDTO> getPromedioSeccion(
            @PathVariable @Schema(description = "ID del estudiante") UUID estudianteId,
            @PathVariable @Schema(description = "ID de la sección") UUID seccionId) {
        log.info("GET /api/v1/notas/estudiante/{}/promedio/seccion/{} [R3 por sección]",
                estudianteId, seccionId);
        return ResponseEntity.ok(service.calcularPromedioSeccion(estudianteId, seccionId));
    }

    @Operation(summary = "Calcular avance R5", description = "Calcula el porcentaje de avance del estudiante para la regla R5 (mínimo 80%)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Avance calculado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    @GetMapping("/estudiante/{estudianteId}/avance")
    public ResponseEntity<AvanceResponseDTO> getAvance(@PathVariable @Schema(description = "ID del estudiante") UUID estudianteId) {
        log.info("GET /api/v1/notas/estudiante/{}/avance [R5]", estudianteId);
        return ResponseEntity.ok(service.calcularAvance(estudianteId));
    }
}
