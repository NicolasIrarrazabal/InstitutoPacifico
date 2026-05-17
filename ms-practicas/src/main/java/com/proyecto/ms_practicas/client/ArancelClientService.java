package com.proyecto.ms_practicas.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

// Llama a ms-aranceles (puerto 8086)
// Endpoint esperado: GET /api/v1/aranceles/estudiante/{id}/puede-continuar
// Respuesta esperada: { "puedeContinuar": true/false }
@Service
@Slf4j
@RequiredArgsConstructor
public class ArancelClientService {

    private final RestTemplate restTemplate;

    @Value("${ms-aranceles.url}")
    private String msArancelesUrl;

    public boolean estudianteEstaAlDia(UUID estudianteId) {
        log.info("Consultando ms-aranceles si estudiante {} está al día", estudianteId);
        String url = msArancelesUrl + "/api/v1/aranceles/estudiante/" + estudianteId + "/puede-continuar";
        try {
            PuedeContinuarResponse response = restTemplate.getForObject(url, PuedeContinuarResponse.class);
            if (response == null || response.puedeContinuar() == null) {
                log.warn("ms-aranceles devolvió respuesta nula para estudiante {}", estudianteId);
                return false;
            }
            return response.puedeContinuar();
        } catch (Exception e) {
            log.error("Error al consultar ms-aranceles: {}", e.getMessage());
            throw new IllegalStateException("No se pudo verificar el estado de arancel del estudiante: " + e.getMessage());
        }
    }

    // Record interno para mapear la respuesta de ms-aranceles
    public record PuedeContinuarResponse(Boolean puedeContinuar) {}
}
