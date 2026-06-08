package com.proyecto.ms_aranceles.controller;

import com.proyecto.ms_aranceles.client.PuedeContinuarResponse;
import com.proyecto.ms_aranceles.dto.ArancelDTO;
import com.proyecto.ms_aranceles.model.Arancel;
import com.proyecto.ms_aranceles.service.ArancelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/aranceles")
@Slf4j
@RequiredArgsConstructor
public class ArancelController {

    private final ArancelService service;

    @GetMapping
    public ResponseEntity<List<Arancel>> getAll() {
        log.info("GET /api/v1/aranceles");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Arancel> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/aranceles/{}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<Arancel>> getByEstudiante(@PathVariable UUID estudianteId) {
        log.info("GET /api/v1/aranceles/estudiante/{}", estudianteId);
        return ResponseEntity.ok(service.findByEstudiante(estudianteId));
    }

    @PostMapping
    public ResponseEntity<Arancel> create(@Valid @RequestBody ArancelDTO dto) {
        log.info("POST /api/v1/aranceles");
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Arancel> update(@PathVariable UUID id, @Valid @RequestBody ArancelDTO dto) {
        log.info("PUT /api/v1/aranceles/{}", id);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/aranceles/{}", id);
        service.delete(id);
        return ResponseEntity.ok(Map.of("mensaje", "Arancel anulado correctamente"));
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<Arancel> pagar(@PathVariable UUID id) {
        log.info("POST /api/v1/aranceles/{}/pagar", id);
        return ResponseEntity.ok(service.registrarPago(id));
    }

    @GetMapping("/estudiante/{estudianteId}/tiene-deuda-vencida")
    public ResponseEntity<Map<String, Boolean>> tieneDeudaVencida(@PathVariable UUID estudianteId) {
        log.info("GET /api/v1/aranceles/estudiante/{}/tiene-deuda-vencida", estudianteId);
        boolean resultado = service.tieneDeudaVencida(estudianteId);
        return ResponseEntity.ok(Map.of("tieneDeudaVencida", resultado));
    }

    @GetMapping("/estudiante/{estudianteId}/puede-continuar")
    public ResponseEntity<PuedeContinuarResponse> puedeContinuar(@PathVariable UUID estudianteId) {
        log.info("GET /api/v1/aranceles/estudiante/{}/puede-continuar", estudianteId);
        boolean puede = service.puedeContinuar(estudianteId);
        return ResponseEntity.ok(new PuedeContinuarResponse(puede));
    }
}
