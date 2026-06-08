package com.proyecto.ms_asignaturas.controller;

import com.proyecto.ms_asignaturas.model.Credito;
import com.proyecto.ms_asignaturas.service.CreditoService;
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

@Tag(name = "Créditos", description = "Gestión de créditos académicos")
@RestController
@RequestMapping("/api/v1/creditos")
@RequiredArgsConstructor
public class CreditoController {

    private final CreditoService creditoService;

    @Operation(summary = "Listar todos los créditos", description = "Retorna todos los créditos registrados")
    @GetMapping()
    public ResponseEntity<List<Credito>> listar() {
        return ResponseEntity.ok(creditoService.listarTodos());
    }

    @Operation(summary = "Crear crédito", description = "Registra un nuevo crédito académico")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Crédito creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping()
    public ResponseEntity<Credito> crearCredito(@Valid @RequestBody @Schema(description = "Datos del crédito") Credito credito) {
        return new ResponseEntity<>(creditoService.guardar(credito), HttpStatus.CREATED);
    }
}
