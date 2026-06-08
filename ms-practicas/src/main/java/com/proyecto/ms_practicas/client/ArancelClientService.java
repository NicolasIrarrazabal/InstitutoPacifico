package com.proyecto.ms_practicas.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Tag(name = "Arancel Client Service", description = "Cliente HTTP para comunicación con ms-aranceles (R5)")
@Service
@Slf4j
@RequiredArgsConstructor
public class ArancelClientService {

    private final RestTemplate restTemplate;

    @Value("${ms-aranceles.url}")
    private String msArancelesUrl;

    @Operation(summary = "Verificar arancel al día (R5)", description = "Verifica si el estudiante está al día con sus aranceles para la regla R5")
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

    public record PuedeContinuarResponse(Boolean puedeContinuar) {}
}
