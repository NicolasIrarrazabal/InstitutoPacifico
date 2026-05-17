package com.proyecto.ms_notas.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

// cliente para consultar si el estudiante tiene deuda (R4)
@Service
@Slf4j
@RequiredArgsConstructor
public class ArancelClientService {

    private final RestTemplate restTemplate;

    @Value("${ms-aranceles.url}")
    private String msArancelesUrl;

    // R4: pregunta a ms-aranceles si tiene deuda vencida
    @SuppressWarnings("unchecked")
    public boolean tieneDeudaVencida(UUID estudianteId) {
        String url = msArancelesUrl + "/api/v1/aranceles/estudiante/" + estudianteId + "/tiene-deuda-vencida";
        log.info("[R4] Consultando ms-aranceles: ¿estudiante {} tiene deuda vencida >45 días? → {}",
                estudianteId, url);
        try {
            Map<String, Boolean> respuesta = restTemplate.getForObject(url, Map.class);

            if (respuesta == null || !respuesta.containsKey("tieneDeudaVencida")) {
                log.warn("[R4] ms-aranceles devolvió respuesta inesperada para estudiante {}", estudianteId);
                return false; // ante la duda, no bloqueamos
            }

            boolean resultado = Boolean.TRUE.equals(respuesta.get("tieneDeudaVencida"));
            log.info("[R4] Estudiante {} — tieneDeudaVencida: {}", estudianteId, resultado);
            return resultado;

        } catch (HttpClientErrorException.NotFound e) {
            // El estudiante no tiene aranceles registrados → no tiene deuda
            log.info("[R4] Estudiante {} sin aranceles en ms-aranceles (404) → sin deuda", estudianteId);
            return false;
        } catch (Exception e) {
            log.error("[R4] Error al consultar ms-aranceles para estudiante {}: {}", estudianteId, e.getMessage());
            // Si ms-aranceles no responde no bloqueamos (fail-open), solo logueamos
            return false;
        }
    }
}
