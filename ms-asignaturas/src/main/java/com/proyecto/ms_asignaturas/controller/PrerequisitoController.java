package com.proyecto.ms_asignaturas.controller;

import com.proyecto.ms_asignaturas.model.Prerequisito;
import com.proyecto.ms_asignaturas.service.PrerequisitoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Tag(name = "Prerrequisitos", description = "Gestión de prerrequisitos entre asignaturas (R1)")
@RestController
@RequestMapping("/api/v1/prerequisitos")
@RequiredArgsConstructor
public class PrerequisitoController {

    private final PrerequisitoService prerequisitoService;

    @Operation(summary = "Listar prerrequisitos por asignatura", description = "Retorna todos los prerrequisitos de una asignatura")
    @GetMapping("/asignatura/{id}")
    public ResponseEntity<List<Prerequisito>> listarPorAsignatura(@PathVariable @Schema(description = "ID de la asignatura") UUID id) {
        return ResponseEntity.ok(prerequisitoService.listarPorAsignatura(id));
    }

    @Operation(summary = "Asignar prerrequisito", description = "Asigna un prerrequisito a una asignatura")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Prerrequisito asignado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping()
    public ResponseEntity<Prerequisito> asignarPrerequisito(@Valid @RequestBody @Schema(description = "Datos del prerrequisito") Prerequisito prerequisito) {
        return ResponseEntity.ok(prerequisitoService.asignarPrerequisito(prerequisito));
    }
}
