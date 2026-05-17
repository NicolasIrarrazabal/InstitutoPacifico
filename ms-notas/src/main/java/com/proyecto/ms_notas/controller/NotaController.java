package com.proyecto.ms_notas.controller;

import com.proyecto.ms_notas.dto.AvanceResponseDTO;
import com.proyecto.ms_notas.dto.NotaDTO;
import com.proyecto.ms_notas.dto.PromedioResponseDTO;
import com.proyecto.ms_notas.model.Nota;
import com.proyecto.ms_notas.service.NotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

// @RestController: combina @Controller + @ResponseBody
// Indica que esta clase maneja peticiones HTTP y devuelve JSON automáticamente
// @RequestMapping: prefijo de todas las rutas de este controller
@RestController
@RequestMapping("/api/v1/notas")
@Slf4j
@RequiredArgsConstructor
public class NotaController {

    private final NotaService service;

    // GET /api/v1/notas
    // Devuelve todas las notas activas del sistema
    @GetMapping
    public ResponseEntity<List<Nota>> getAll() {
        log.info("GET /api/v1/notas");
        return ResponseEntity.ok(service.findAll());
    }

    // GET /api/v1/notas/{id}
    // Devuelve una nota específica por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Nota> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/notas/{}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    // GET /api/v1/notas/estudiante/{estudianteId}
    // Devuelve todas las notas de un estudiante
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<Nota>> getByEstudiante(@PathVariable UUID estudianteId) {
        log.info("GET /api/v1/notas/estudiante/{}", estudianteId);
        return ResponseEntity.ok(service.findByEstudiante(estudianteId));
    }

    // POST /api/v1/notas
    // Crea una nueva nota (valida R1 internamente)
    // @Valid: activa las validaciones del DTO (@NotNull, @DecimalMin, etc.)
    @PostMapping
    public ResponseEntity<Nota> create(@Valid @RequestBody NotaDTO dto) {
        log.info("POST /api/v1/notas - estudiante: {}", dto.estudianteId());
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    // PUT /api/v1/notas/{id}
    // Actualiza una nota existente
    @PutMapping("/{id}")
    public ResponseEntity<Nota> update(@PathVariable UUID id, @Valid @RequestBody NotaDTO dto) {
        log.info("PUT /api/v1/notas/{}", id);
        return ResponseEntity.ok(service.update(id, dto));
    }

    // DELETE /api/v1/notas/{id}
    // Anula una nota (eliminación lógica, no física)
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/notas/{}", id);
        service.delete(id);
        return ResponseEntity.ok(Map.of("mensaje", "Nota anulada correctamente"));
    }

    // GET /api/v1/notas/estudiante/{id}/promedio
    // R3: promedio global del estudiante
    @GetMapping("/estudiante/{estudianteId}/promedio")
    public ResponseEntity<PromedioResponseDTO> getPromedio(@PathVariable UUID estudianteId) {
        log.info("GET /api/v1/notas/estudiante/{}/promedio [R3 global]", estudianteId);
        return ResponseEntity.ok(service.calcularPromedio(estudianteId));
    }

    // GET /api/v1/notas/estudiante/{id}/promedio/seccion/{seccionId}
    //
    // R3: promedio por sección con estado de aprobación
    //
    // Calcula el promedio ponderado del estudiante en UNA sección específica
    // y retorna el estadoAcademico con uno de estos tres valores:
    //
    //   "APROBADO"                     → promedio >= 4.0
    //   "PENDIENTE_EXAMEN_RECUPERACION" → promedio 3.5 – 3.9
    //   "REPROBADO"                    → promedio < 3.5
    //
    // Ejemplo de respuesta JSON:
    // {
    //   "estudianteId": "...",
    //   "seccionId": "...",
    //   "promedioPonderado": 3.7,
    //   "promedioSimple": 3.7,
    //   "totalNotas": 3,
    //   "aprobado": false,
    //   "estadoAcademico": "PENDIENTE_EXAMEN_RECUPERACION",
    //   "mensajeR3": "⚠️ PENDIENTE DE EXAMEN DE RECUPERACIÓN (R3): promedio ponderado 3.7 ..."
    // }
    @GetMapping("/estudiante/{estudianteId}/promedio/seccion/{seccionId}")
    public ResponseEntity<PromedioResponseDTO> getPromedioSeccion(
            @PathVariable UUID estudianteId,
            @PathVariable UUID seccionId) {
        log.info("GET /api/v1/notas/estudiante/{}/promedio/seccion/{} [R3 por sección]",
                estudianteId, seccionId);
        return ResponseEntity.ok(service.calcularPromedioSeccion(estudianteId, seccionId));
    }

    // GET /api/v1/notas/estudiante/{id}/avance
    // R5: verifica el avance del 80%
    @GetMapping("/estudiante/{estudianteId}/avance")
    public ResponseEntity<AvanceResponseDTO> getAvance(@PathVariable UUID estudianteId) {
        log.info("GET /api/v1/notas/estudiante/{}/avance [R5]", estudianteId);
        return ResponseEntity.ok(service.calcularAvance(estudianteId));
    }
}
