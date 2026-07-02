package com.proyecto.ms_docente.controller;

import com.proyecto.ms_docente.model.Contrato;
import com.proyecto.ms_docente.service.ContratoService;
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
@Tag(name = "Contratos", description = "Gestión de contratos de docentes")
@AllArgsConstructor
@RestController
@RequestMapping("/api/contratos")
@Slf4j
public class ContratoController {

    private ContratoService service;

    @Operation(summary = "Listar todos los contratos", description = "Retorna todos los contratos registrados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de contratos encontrada")
    })
    @GetMapping
    public ResponseEntity<List<Contrato>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Crear contrato", description = "Registra un nuevo contrato para un docente")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Contrato creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Contrato> crear(@Valid @RequestBody @Schema(description = "Datos del contrato") Contrato contrato) {
        return new ResponseEntity<>(service.guardar(contrato), HttpStatus.CREATED);
    }
}