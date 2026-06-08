package com.proyecto.ms_asignaturas.controller;

import com.proyecto.ms_asignaturas.dto.AsignaturaDTO;
import com.proyecto.ms_asignaturas.model.Asignatura;
import com.proyecto.ms_asignaturas.model.Prerequisito;
import com.proyecto.ms_asignaturas.service.AsignaturasService;
import com.proyecto.ms_asignaturas.service.PrerequisitoService;
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

@Tag(name = "Asignaturas", description = "Gestión de asignaturas y prerrequisitos (R1)")
@RestController
@RequestMapping("/api/v1/asignaturas")
@Slf4j
@RequiredArgsConstructor
public class AsignaturasController {

    private final AsignaturasService service;
    private final PrerequisitoService prerequisitoService;

    @Operation(summary = "Listar todas las asignaturas", description = "Retorna todas las asignaturas del catálogo")
    @GetMapping
    public ResponseEntity<List<Asignatura>> listarAsignaturas() {
        log.info("GET /api/v1/asignaturas");
        return ResponseEntity.ok(service.listarTodas());
    }

    @Operation(summary = "Obtener asignatura por ID", description = "Retorna una asignatura por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asignatura encontrada"),
        @ApiResponse(responseCode = "404", description = "Asignatura no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Asignatura> obtenerAsignaturaPorId(@PathVariable @Schema(description = "ID de la asignatura") UUID id) {
        log.info("GET /api/v1/asignaturas/{}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Obtener prerrequisitos", description = "Retorna los prerrequisitos de una asignatura")
    @GetMapping("/{id}/prerequisitos")
    public ResponseEntity<List<Prerequisito>> obtenerPrerequisitos(@PathVariable @Schema(description = "ID de la asignatura") UUID id) {
        log.info("GET /api/v1/asignaturas/{}/prerequisitos", id);
        return ResponseEntity.ok(prerequisitoService.listarPorAsignatura(id));
    }

    @Operation(summary = "Crear asignatura", description = "Registra una nueva asignatura en el catálogo")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Asignatura creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Asignatura> crearAsignatura(@Valid @RequestBody @Schema(description = "Datos de la asignatura") AsignaturaDTO dto) {
        log.info("POST /api/v1/asignaturas");
        return new ResponseEntity<>(service.crear(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar asignatura", description = "Actualiza los datos de una asignatura existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asignatura actualizada"),
        @ApiResponse(responseCode = "404", description = "Asignatura no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Asignatura> actualizar(@PathVariable @Schema(description = "ID de la asignatura") UUID id,
                                                 @Valid @RequestBody @Schema(description = "Datos actualizados de la asignatura") AsignaturaDTO dto) {
        log.info("PUT /api/v1/asignaturas/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar asignatura", description = "Elimina lógicamente una asignatura")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asignatura eliminada"),
        @ApiResponse(responseCode = "404", description = "Asignatura no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable @Schema(description = "ID de la asignatura") UUID id) {
        log.info("DELETE /api/v1/asignaturas/{}", id);
        service.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Asignatura eliminada correctamente"));
    }
}
