package com.proyecto.ms_practicas.controller;

import com.proyecto.ms_practicas.dto.FinalizarPracticaDTO;
import com.proyecto.ms_practicas.dto.PracticaDTO;
import com.proyecto.ms_practicas.dto.ValidacionR5Response;
import com.proyecto.ms_practicas.model.Practica;
import com.proyecto.ms_practicas.service.PracticaService;
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

@Tag(name = "Prácticas", description = "Gestión de prácticas profesionales con validación de regla R5")
@RestController
@RequestMapping("/api/v1/practicas")
@Slf4j
@RequiredArgsConstructor
public class PracticaController {

    private final PracticaService service;

    @Operation(summary = "Listar todas las prácticas", description = "Retorna todas las prácticas registradas")
    @GetMapping
    public ResponseEntity<List<Practica>> getAll() {
        log.info("GET /api/v1/practicas");
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Obtener práctica por ID", description = "Retorna una práctica específica por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Práctica encontrada"),
        @ApiResponse(responseCode = "404", description = "Práctica no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Practica> getById(@PathVariable @Schema(description = "ID de la práctica") UUID id) {
        log.info("GET /api/v1/practicas/{}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Listar prácticas por estudiante", description = "Retorna todas las prácticas de un estudiante")
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<Practica>> getByEstudiante(@PathVariable @Schema(description = "ID del estudiante") UUID estudianteId) {
        log.info("GET /api/v1/practicas/estudiante/{}", estudianteId);
        return ResponseEntity.ok(service.findByEstudiante(estudianteId));
    }

    @Operation(summary = "Verificar requisitos R5", description = "Verifica si el estudiante cumple los 3 requisitos de la R5 (créditos, arancel, convenio)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resultado de la verificación R5"),
        @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    })
    @GetMapping("/verificar")
    public ResponseEntity<ValidacionR5Response> verificarRequisitosR5(
            @RequestParam @Schema(description = "ID del estudiante") UUID estudianteId,
            @RequestParam @Schema(description = "ID de la empresa") UUID empresaId) {
        log.info("GET /api/v1/practicas/verificar — estudiante: {} empresa: {}", estudianteId, empresaId);
        return ResponseEntity.ok(service.verificarRequisitosR5(estudianteId, empresaId));
    }

    @Operation(summary = "Inscribir práctica", description = "Inscribe una nueva práctica profesional validando la regla R5")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Práctica inscrita exitosamente"),
        @ApiResponse(responseCode = "400", description = "No cumple requisitos R5 o datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Practica> create(@Valid @RequestBody @Schema(description = "Datos de la práctica a inscribir") PracticaDTO dto) {
        log.info("POST /api/v1/practicas — estudiante: {} empresa: {}", dto.estudianteId(), dto.empresaId());
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Finalizar práctica", description = "Registra la finalización de una práctica con la nota y observaciones")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Práctica finalizada"),
        @ApiResponse(responseCode = "404", description = "Práctica no encontrada")
    })
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<Practica> finalizar(@PathVariable @Schema(description = "ID de la práctica") UUID id,
                                              @Valid @RequestBody @Schema(description = "Datos de finalización") FinalizarPracticaDTO dto) {
        log.info("PUT /api/v1/practicas/{}/finalizar", id);
        return ResponseEntity.ok(service.finalizar(id, dto));
    }

    @Operation(summary = "Anular práctica", description = "Anula lógicamente una práctica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Práctica anulada"),
        @ApiResponse(responseCode = "404", description = "Práctica no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable @Schema(description = "ID de la práctica") UUID id) {
        log.info("DELETE /api/v1/practicas/{}", id);
        service.delete(id);
        return ResponseEntity.ok(Map.of("mensaje", "Práctica anulada correctamente"));
    }
}
