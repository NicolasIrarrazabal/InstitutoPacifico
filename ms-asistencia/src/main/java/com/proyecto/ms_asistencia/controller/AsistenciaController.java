package com.proyecto.ms_asistencia.controller;

import com.proyecto.ms_asistencia.dto.AsistenciaDTO;
import com.proyecto.ms_asistencia.dto.RegistroAsistenciaResponseDTO;
import com.proyecto.ms_asistencia.dto.ResumenAsistenciaDTO;
import com.proyecto.ms_asistencia.model.Asistencia;
import com.proyecto.ms_asistencia.service.AsistenciaService;
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
@RequestMapping("/api/v1/asistencias")
@Slf4j
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaService service;

    // POST /api/v1/asistencias
    // Registra una asistencia y evalúa R2 automáticamente.
    // La respuesta incluye el registro guardado + el resumen de R2.
    //
    // Ejemplo de body JSON:
    // {
    //   "estudianteId": "...",
    //   "seccionId": "...",
    //   "fecha": "2026-05-16",
    //   "tipo": "AUSENTE",
    //   "observacion": "No aviso, ni dio detalles de su ausencia."
    // }
    @PostMapping
    public ResponseEntity<RegistroAsistenciaResponseDTO> registrar(
            @Valid @RequestBody AsistenciaDTO dto) {
        log.info("POST /api/v1/asistencias");
        return new ResponseEntity<>(service.registrar(dto), HttpStatus.CREATED);
    }

    // PUT /api/v1/asistencias/{id}
    // Corrige un registro existente  AUSENTE → JUSTIFICADO
    // También recalcula y devuelve el resumen R2 actualizado.
    @PutMapping("/{id}")
    public ResponseEntity<RegistroAsistenciaResponseDTO> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody AsistenciaDTO dto) {
        log.info("PUT /api/v1/asistencias/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    // DELETE /api/v1/asistencias/{id}
    // Se elimina el registro
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> anular(@PathVariable UUID id) {
        log.info("DELETE /api/v1/asistencias/{}", id);
        service.anular(id);
        return ResponseEntity.ok(Map.of("mensaje", "Registro de asistencia anulado correctamente"));
    }

    // GET /api/v1/asistencias/{id}
    // Buscar un registro específico por ID
    @GetMapping("/{id}")
    public ResponseEntity<Asistencia> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/asistencias/{}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    // GET /api/v1/asistencias/seccion/{seccionId}
    // Lista todas las asistencias de una sección (vista del docente por clase)
    @GetMapping("/seccion/{seccionId}")
    public ResponseEntity<List<Asistencia>> getBySeccion(@PathVariable UUID seccionId) {
        log.info("GET /api/v1/asistencias/seccion/{}", seccionId);
        return ResponseEntity.ok(service.findBySeccion(seccionId));
    }

    // GET /api/v1/asistencias/estudiante/{estudianteId}
    // Lista todas las asistencias de un estudiante (en todas sus secciones)
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<Asistencia>> getByEstudiante(@PathVariable UUID estudianteId) {
        log.info("GET /api/v1/asistencias/estudiante/{}", estudianteId);
        return ResponseEntity.ok(service.findByEstudiante(estudianteId));
    }

    // GET /api/v1/asistencias/estudiante/{estudianteId}/seccion/{seccionId}
    // Lista todos los registros de un estudiante en una sección específica
    @GetMapping("/estudiante/{estudianteId}/seccion/{seccionId}")
    public ResponseEntity<List<Asistencia>> getByEstudianteYSeccion(
            @PathVariable UUID estudianteId,
            @PathVariable UUID seccionId) {
        log.info("GET /api/v1/asistencias/estudiante/{}/seccion/{}", estudianteId, seccionId);
        return ResponseEntity.ok(service.findByEstudianteYSeccion(estudianteId, seccionId));
    }

    // GET /api/v1/asistencias/estudiante/{estudianteId}/seccion/{seccionId}/resumen
    //
    // endpoint que verifica la R2
    // Devuelve el resumen completo:
    //   - total de clases
    //   - presente / ausente / justificado
    //   - % de inasistencia
    //   - reprobadoPorAsistencia: true/false
    //   - mensaje explicativo
    //
    // Este endpoint puede ser consumido por otros microservicios
    // (ej: ms-notas para bloquear el promedio si está reprobado por asistencia)
    @GetMapping("/estudiante/{estudianteId}/seccion/{seccionId}/resumen")
    public ResponseEntity<ResumenAsistenciaDTO> getResumenR2(
            @PathVariable UUID estudianteId,
            @PathVariable UUID seccionId) {
        log.info("GET /api/v1/asistencias/estudiante/{}/seccion/{}/resumen [R2]",
                estudianteId, seccionId);
        return ResponseEntity.ok(service.calcularResumenR2(estudianteId, seccionId));
    }
}
