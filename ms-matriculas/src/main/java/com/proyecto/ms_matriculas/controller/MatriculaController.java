package com.proyecto.ms_matriculas.controller;

import com.proyecto.ms_matriculas.dto.MatriculaDTO;
import com.proyecto.ms_matriculas.model.Matricula;
import com.proyecto.ms_matriculas.service.MatriculaService;
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

@Tag(name = "Matrículas", description = "Gestión de matrículas con validación de regla R1 (prerrequisitos)")
@RestController
@RequestMapping("/api/v1/matriculas")
@Slf4j
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaService service;

    @Operation(summary = "Listar todas las matrículas", description = "Retorna todas las matrículas registradas")
    @GetMapping
    public ResponseEntity<List<Matricula>> getAll() {
        log.info("GET /api/v1/matriculas");
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Obtener matrícula por ID", description = "Retorna una matrícula específica por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Matrícula encontrada"),
        @ApiResponse(responseCode = "404", description = "Matrícula no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Matricula> getById(@PathVariable @Schema(description = "ID de la matrícula") UUID id) {
        log.info("GET /api/v1/matriculas/{}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Crear matrícula", description = "Registra una nueva matrícula validando la regla R1 (prerrequisitos)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Matrícula creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o no cumple prerrequisitos (R1)")
    })
    @PostMapping
    public ResponseEntity<Matricula> create(@Valid @RequestBody @Schema(description = "Datos de la matrícula") MatriculaDTO dto) {
        log.info("POST /api/v1/matriculas");
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar matrícula", description = "Actualiza una matrícula existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Matrícula actualizada"),
        @ApiResponse(responseCode = "404", description = "Matrícula no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Matricula> update(@PathVariable @Schema(description = "ID de la matrícula") UUID id,
                                            @Valid @RequestBody @Schema(description = "Datos actualizados de la matrícula") MatriculaDTO dto) {
        log.info("PUT /api/v1/matriculas/{}", id);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Eliminar matrícula", description = "Elimina lógicamente una matrícula")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Matrícula eliminada"),
        @ApiResponse(responseCode = "404", description = "Matrícula no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable @Schema(description = "ID de la matrícula") UUID id) {
        log.info("DELETE /api/v1/matriculas/{}", id);
        service.delete(id);
        return ResponseEntity.ok(Map.of("mensaje", "Matrícula eliminada correctamente"));
    }
}