package com.proyecto.ms_aranceles.controller;

import com.proyecto.ms_aranceles.client.PuedeContinuarResponse;
import com.proyecto.ms_aranceles.dto.ArancelDTO;
import com.proyecto.ms_aranceles.model.Arancel;
import com.proyecto.ms_aranceles.service.ArancelService;
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

@Tag(name = "Aranceles", description = "Gestión de aranceles, pagos y deudas — regla R4")
@RestController
@RequestMapping("/api/v1/aranceles")
@Slf4j
@RequiredArgsConstructor
public class ArancelController {

    private final ArancelService service;

    @Operation(summary = "Listar todos los aranceles", description = "Retorna todos los aranceles registrados")
    @GetMapping
    public ResponseEntity<List<Arancel>> getAll() {
        log.info("GET /api/v1/aranceles");
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Obtener arancel por ID", description = "Retorna un arancel específico por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Arancel encontrado"),
        @ApiResponse(responseCode = "404", description = "Arancel no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Arancel> getById(@PathVariable @Schema(description = "ID del arancel") UUID id) {
        log.info("GET /api/v1/aranceles/{}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Listar aranceles por estudiante", description = "Retorna todos los aranceles de un estudiante")
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<Arancel>> getByEstudiante(@PathVariable @Schema(description = "ID del estudiante") UUID estudianteId) {
        log.info("GET /api/v1/aranceles/estudiante/{}", estudianteId);
        return ResponseEntity.ok(service.findByEstudiante(estudianteId));
    }

    @Operation(summary = "Crear arancel", description = "Registra un nuevo arancel para un estudiante")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Arancel creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Arancel> create(@Valid @RequestBody @Schema(description = "Datos del arancel") ArancelDTO dto) {
        log.info("POST /api/v1/aranceles");
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar arancel", description = "Actualiza un arancel existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Arancel actualizado"),
        @ApiResponse(responseCode = "404", description = "Arancel no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Arancel> update(@PathVariable @Schema(description = "ID del arancel") UUID id,
                                          @Valid @RequestBody @Schema(description = "Datos actualizados del arancel") ArancelDTO dto) {
        log.info("PUT /api/v1/aranceles/{}", id);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Anular arancel", description = "Anula lógicamente un arancel")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Arancel anulado"),
        @ApiResponse(responseCode = "404", description = "Arancel no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable @Schema(description = "ID del arancel") UUID id) {
        log.info("DELETE /api/v1/aranceles/{}", id);
        service.delete(id);
        return ResponseEntity.ok(Map.of("mensaje", "Arancel anulado correctamente"));
    }

    @Operation(summary = "Registrar pago", description = "Registra el pago de un arancel")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pago registrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Arancel no encontrado")
    })
    @PostMapping("/{id}/pagar")
    public ResponseEntity<Arancel> pagar(@PathVariable @Schema(description = "ID del arancel") UUID id) {
        log.info("POST /api/v1/aranceles/{}/pagar", id);
        return ResponseEntity.ok(service.registrarPago(id));
    }

    @Operation(summary = "Verificar deuda vencida (R4)", description = "Verifica si el estudiante tiene deuda vencida de más de 45 días (R4)")
    @GetMapping("/estudiante/{estudianteId}/tiene-deuda-vencida")
    public ResponseEntity<Map<String, Boolean>> tieneDeudaVencida(@PathVariable @Schema(description = "ID del estudiante") UUID estudianteId) {
        log.info("GET /api/v1/aranceles/estudiante/{}/tiene-deuda-vencida", estudianteId);
        boolean resultado = service.tieneDeudaVencida(estudianteId);
        return ResponseEntity.ok(Map.of("tieneDeudaVencida", resultado));
    }

    @Operation(summary = "Verificar si puede continuar (R4)", description = "Verifica si el estudiante puede continuar según su situación de arancel (R4)")
    @GetMapping("/estudiante/{estudianteId}/puede-continuar")
    public ResponseEntity<PuedeContinuarResponse> puedeContinuar(@PathVariable @Schema(description = "ID del estudiante") UUID estudianteId) {
        log.info("GET /api/v1/aranceles/estudiante/{}/puede-continuar", estudianteId);
        boolean puede = service.puedeContinuar(estudianteId);
        return ResponseEntity.ok(new PuedeContinuarResponse(puede));
    }
}
