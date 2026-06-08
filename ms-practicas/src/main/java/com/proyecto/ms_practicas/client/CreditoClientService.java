package com.proyecto.ms_practicas.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Tag(name = "Credito Client Service", description = "Cliente HTTP para comunicación con ms-notas (R5)")
@Service
@Slf4j
@RequiredArgsConstructor
public class CreditoClientService {

    private final RestTemplate restTemplate;

    @Value("${ms-notas.url}")
    private String msNotasUrl;

    @Operation(summary = "Verificar créditos suficientes (R5)", description = "Verifica si el estudiante tiene al menos 80% de avance académico para la regla R5")
    public boolean estudianteTieneCreditosSuficientes(UUID estudianteId) {
        log.info("Consultando ms-notas si estudiante {} tiene >= 80% de avance académico", estudianteId);
        String url = msNotasUrl + "/api/v1/notas/estudiante/" + estudianteId + "/avance";
        try {
            AvanceResponse response = restTemplate.getForObject(url, AvanceResponse.class);
            if (response == null || response.porcentajeAvance() == null) {
                log.warn("ms-notas devolvió respuesta nula para estudiante {}", estudianteId);
                return false;
            }
            log.info("Estudiante {} tiene {}% de avance académico", estudianteId, response.porcentajeAvance());
            return response.cumpleAvance80();
        } catch (Exception e) {
            log.error("Error al consultar ms-notas (avance): {}", e.getMessage());
            throw new IllegalStateException("No se pudo verificar el avance académico del estudiante: " + e.getMessage());
        }
    }

    public record AvanceResponse(
            java.util.UUID estudianteId,
            int totalSecciones,
            int seccionesAprobadas,
            Double porcentajeAvance,
            boolean cumpleAvance80
    ) {}
}
