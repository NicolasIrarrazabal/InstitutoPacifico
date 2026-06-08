package com.proyecto.ms_practicas.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreditoClientService {

    private final RestTemplate restTemplate;

    @Value("${ms-notas.url}")
    private String msNotasUrl;

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
