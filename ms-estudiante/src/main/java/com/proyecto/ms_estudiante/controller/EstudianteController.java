package com.proyecto.ms_estudiante.controller;

import com.proyecto.ms_estudiante.dto.DetalleEstudianteResponse;
import com.proyecto.ms_estudiante.dto.EstudianteDTO;
import com.proyecto.ms_estudiante.model.Estudiante;
import com.proyecto.ms_estudiante.service.EstudianteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Estudiantes", description = "Gestión de estudiantes del Instituto Pacífico")
@RestController
@RequestMapping("/api/v1/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {

    private final EstudianteService service;

    @Operation(summary = "Listar todos los estudiantes", description = "Retorna todos los estudiantes registrados")
    @GetMapping
    public ResponseEntity<List<Estudiante>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Obtener estudiante por ID", description = "Retorna un estudiante por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estudiante encontrado"),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Estudiante> getById(@PathVariable @Schema(description = "ID del estudiante") UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Obtener estudiante por RUT", description = "Retorna un estudiante por su RUT")
    @GetMapping("/rut/{rut}")
    public ResponseEntity<Estudiante> getByRut(@PathVariable @Schema(description = "RUT del estudiante", example = "12345678-9") String rut) {
        return ResponseEntity.ok(service.findByRut(rut));
    }

    @Operation(summary = "Verificar si puede matricular", description = "Verifica si el estudiante cumple los requisitos para matricularse")
    @GetMapping("/{id}/puede-matricular")
    public ResponseEntity<Map<String, Boolean>> puedeMatricular(@PathVariable @Schema(description = "ID del estudiante") UUID id) {
        boolean resultado = service.puedeMatricular(id);
        return ResponseEntity.ok(Map.of("puedeMatricular", resultado));
    }

    @Operation(summary = "Obtener detalle completo del estudiante", description = "Retorna el detalle del estudiante incluyendo matrículas, notas y asistencia")
    @GetMapping("/{id}/detalle")
    public ResponseEntity<DetalleEstudianteResponse> getDetalle(@PathVariable @Schema(description = "ID del estudiante") UUID id) {
        return ResponseEntity.ok(service.obtenerDetalle(id));
    }

    @Operation(summary = "Crear estudiante", description = "Registra un nuevo estudiante en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Estudiante creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Estudiante> create(@Valid @RequestBody @Schema(description = "Datos del estudiante a crear") EstudianteDTO dto) {
        Estudiante creado = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Actualizar estudiante", description = "Actualiza los datos de un estudiante existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estudiante actualizado"),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Estudiante> update(@PathVariable @Schema(description = "ID del estudiante") UUID id,
                                             @Valid @RequestBody @Schema(description = "Datos actualizados del estudiante") EstudianteDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Desactivar estudiante", description = "Desactiva lógicamente un estudiante")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estudiante desactivado"),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable @Schema(description = "ID del estudiante") UUID id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of("mensaje", "Estudiante desactivado correctamente"));
    }
}
