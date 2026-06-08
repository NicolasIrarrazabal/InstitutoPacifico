package com.proyecto.ms_empresas.controller;

import com.proyecto.ms_empresas.dto.EmpresaDTO;
import com.proyecto.ms_empresas.model.Empresa;
import com.proyecto.ms_empresas.service.EmpresaService;
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

@Tag(name = "Empresas", description = "Gestión de empresas con convenio para prácticas profesionales (R5)")
@RestController
@RequestMapping("/api/v1/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService service;

    @Operation(summary = "Listar todas las empresas", description = "Retorna todas las empresas registradas con convenio")
    @GetMapping
    public ResponseEntity<List<Empresa>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Obtener empresa por ID", description = "Retorna una empresa por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Empresa encontrada"),
        @ApiResponse(responseCode = "404", description = "Empresa no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Empresa> getById(@PathVariable @Schema(description = "ID de la empresa") UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Crear empresa", description = "Registra una nueva empresa con convenio")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Empresa creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Empresa> create(@Valid @RequestBody @Schema(description = "Datos de la empresa a crear") EmpresaDTO dto) {
        Empresa creada = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @Operation(summary = "Actualizar empresa", description = "Actualiza los datos de una empresa existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Empresa actualizada"),
        @ApiResponse(responseCode = "404", description = "Empresa no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Empresa> update(@PathVariable @Schema(description = "ID de la empresa") UUID id,
                                          @Valid @RequestBody @Schema(description = "Datos actualizados de la empresa") EmpresaDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Desactivar empresa", description = "Desactiva lógicamente una empresa")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Empresa desactivada"),
        @ApiResponse(responseCode = "404", description = "Empresa no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable @Schema(description = "ID de la empresa") UUID id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of("mensaje", "Empresa desactivada correctamente"));
    }

    @Operation(summary = "Verificar convenio vigente", description = "Verifica si la empresa tiene un convenio vigente (R5)")
    @GetMapping("/{id}/tiene-convenio-vigente")
    public ResponseEntity<Map<String, Boolean>> tieneConvenioVigente(@PathVariable @Schema(description = "ID de la empresa") UUID id) {
        boolean resultado = service.tieneConvenioVigente(id);
        return ResponseEntity.ok(Map.of("tieneConvenioVigente", resultado));
    }
}
