package com.proyecto.ms_practicas.controller;

import com.proyecto.ms_practicas.dto.FinalizarPracticaDTO;
import com.proyecto.ms_practicas.dto.PracticaDTO;
import com.proyecto.ms_practicas.dto.ValidacionR5Response;
import com.proyecto.ms_practicas.model.Practica;
import com.proyecto.ms_practicas.service.PracticaService;
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
@RequestMapping("/api/v1/practicas")
@Slf4j
@RequiredArgsConstructor
public class PracticaController {

    private final PracticaService service;


    @GetMapping
    public ResponseEntity<List<Practica>> getAll() {
        log.info("GET /api/v1/practicas");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Practica> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/practicas/{}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    // Prácticas de un estudiante específico
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<Practica>> getByEstudiante(@PathVariable UUID estudianteId) {
        log.info("GET /api/v1/practicas/estudiante/{}", estudianteId);
        return ResponseEntity.ok(service.findByEstudiante(estudianteId));
    }


    /*
     * Endpoint de consulta: verifica si el estudiante cumple los 3 requisitos de la R5
     * SIN inscribir nada. Útil para mostrar en el frontend el estado de cada condición.
     *
     * Ejemplo de uso:
     *   GET /api/v1/practicas/verificar?estudianteId=uuid&empresaId=uuid
     *
     * Devuelve algo como:
     * {
     *   "creditosAprobados": true,
     *   "arancelAlDia": false,
     *   "empresaConConvenio": true,
     *   "puedeInscribir": false,
     *   "mensaje": "No se puede inscribir... [El estudiante tiene deuda de arancel pendiente]"
     * }
     */
    @GetMapping("/verificar")
    public ResponseEntity<ValidacionR5Response> verificarRequisitosR5(
            @RequestParam UUID estudianteId,
            @RequestParam UUID empresaId) {
        log.info("GET /api/v1/practicas/verificar — estudiante: {} empresa: {}", estudianteId, empresaId);
        return ResponseEntity.ok(service.verificarRequisitosR5(estudianteId, empresaId));
    }


    /*
     * Inscribe la práctica. Internamente valida los 3 requisitos de la R5.
     * Si alguno falla, devuelve 409 CONFLICT con el detalle del bloqueo.
     */
    @PostMapping
    public ResponseEntity<Practica> create(@Valid @RequestBody PracticaDTO dto) {
        log.info("POST /api/v1/practicas — estudiante: {} empresa: {}", dto.estudianteId(), dto.empresaId());
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }


    // Finaliza la práctica con estado COMPLETADA o REPROBADA
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<Practica> finalizar(@PathVariable UUID id,
                                              @Valid @RequestBody FinalizarPracticaDTO dto) {
        log.info("PUT /api/v1/practicas/{}/finalizar", id);
        return ResponseEntity.ok(service.finalizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/practicas/{}", id);
        service.delete(id);
        return ResponseEntity.ok(Map.of("mensaje", "Práctica anulada correctamente"));
    }
}
