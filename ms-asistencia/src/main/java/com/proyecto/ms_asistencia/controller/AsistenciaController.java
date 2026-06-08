package com.proyecto.ms_asistencia.controller;

import com.proyecto.ms_asistencia.dto.AsistenciaDTO;
import com.proyecto.ms_asistencia.dto.RegistroAsistenciaResponseDTO;
import com.proyecto.ms_asistencia.dto.ResumenAsistenciaDTO;
import com.proyecto.ms_asistencia.model.Asistencia;
import com.proyecto.ms_asistencia.service.AsistenciaService;
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

@Tag(name = "Asistencias", description = "Gestión de asistencia de estudiantes y evaluación de regla R2")
@RestController
@RequestMapping("/api/v1/asistencias")
@Slf4j
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaService service;

    @Operation(summary = "Registrar asistencia", description = "Crea un registro de asistencia y evalúa R2 (límite de inasistencia)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Asistencia registrada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o estudiante no matriculado"),
        @ApiResponse(responseCode = "409", description = "Ya existe un registro para esa fecha")
    })
    @PostMapping
    public ResponseEntity<RegistroAsistenciaResponseDTO> registrar(
            @Valid @RequestBody @Schema(description = "Datos para registrar asistencia") AsistenciaDTO dto) {
        log.info("POST /api/v1/asistencias");
        return new ResponseEntity<>(service.registrar(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar asistencia", description = "Corrige un registro de asistencia existente y recalcula R2")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asistencia actualizada correctamente"),
        @ApiResponse(responseCode = "404", description = "Registro no encontrado"),
        @ApiResponse(responseCode = "400", description = "No se puede modificar un registro anulado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RegistroAsistenciaResponseDTO> actualizar(
            @PathVariable @Schema(description = "ID del registro de asistencia") UUID id,
            @Valid @RequestBody @Schema(description = "Datos actualizados de asistencia") AsistenciaDTO dto) {
        log.info("PUT /api/v1/asistencias/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @Operation(summary = "Anular asistencia", description = "Anula lógicamente un registro de asistencia")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Registro anulado correctamente"),
        @ApiResponse(responseCode = "404", description = "Registro no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> anular(@PathVariable @Schema(description = "ID del registro de asistencia") UUID id) {
        log.info("DELETE /api/v1/asistencias/{}", id);
        service.anular(id);
        return ResponseEntity.ok(Map.of("mensaje", "Registro de asistencia anulado correctamente"));
    }

    @Operation(summary = "Obtener asistencia por ID", description = "Retorna un registro de asistencia por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Registro encontrado"),
        @ApiResponse(responseCode = "404", description = "Registro no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Asistencia> getById(@PathVariable @Schema(description = "ID del registro") UUID id) {
        log.info("GET /api/v1/asistencias/{}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Listar asistencias por sección", description = "Retorna todos los registros de asistencia de una sección")
    @GetMapping("/seccion/{seccionId}")
    public ResponseEntity<List<Asistencia>> getBySeccion(@PathVariable @Schema(description = "ID de la sección") UUID seccionId) {
        log.info("GET /api/v1/asistencias/seccion/{}", seccionId);
        return ResponseEntity.ok(service.findBySeccion(seccionId));
    }

    @Operation(summary = "Listar asistencias por estudiante", description = "Retorna todos los registros de asistencia de un estudiante")
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<Asistencia>> getByEstudiante(@PathVariable @Schema(description = "ID del estudiante") UUID estudianteId) {
        log.info("GET /api/v1/asistencias/estudiante/{}", estudianteId);
        return ResponseEntity.ok(service.findByEstudiante(estudianteId));
    }

    @Operation(summary = "Listar asistencias por estudiante y sección", description = "Retorna los registros de asistencia de un estudiante en una sección específica")
    @GetMapping("/estudiante/{estudianteId}/seccion/{seccionId}")
    public ResponseEntity<List<Asistencia>> getByEstudianteYSeccion(
            @PathVariable @Schema(description = "ID del estudiante") UUID estudianteId,
            @PathVariable @Schema(description = "ID de la sección") UUID seccionId) {
        log.info("GET /api/v1/asistencias/estudiante/{}/seccion/{}", estudianteId, seccionId);
        return ResponseEntity.ok(service.findByEstudianteYSeccion(estudianteId, seccionId));
    }

    @Operation(summary = "Resumen R2 de asistencia", description = "Calcula el resumen de asistencia y determina si el estudiante reprobó por la regla R2 (límite de inasistencia)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resumen calculado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Estudiante o sección no encontrados")
    })
    @GetMapping("/estudiante/{estudianteId}/seccion/{seccionId}/resumen")
    public ResponseEntity<ResumenAsistenciaDTO> getResumenR2(
            @PathVariable @Schema(description = "ID del estudiante") UUID estudianteId,
            @PathVariable @Schema(description = "ID de la sección") UUID seccionId) {
        log.info("GET /api/v1/asistencias/estudiante/{}/seccion/{}/resumen [R2]",
                estudianteId, seccionId);
        return ResponseEntity.ok(service.calcularResumenR2(estudianteId, seccionId));
    }
}
