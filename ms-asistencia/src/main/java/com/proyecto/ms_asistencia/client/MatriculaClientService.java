package com.proyecto.ms_asistencia.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Tag(name = "Matricula Client Service", description = "Cliente HTTP para comunicación con ms-matriculas")
@Service
@Slf4j
@RequiredArgsConstructor
public class MatriculaClientService {

    private final RestTemplate restTemplate;

    @Value("${ms-matriculas.url}")
    private String msMatriculasUrl;

    @Operation(summary = "Verificar matrícula activa", description = "Consulta a ms-matriculas si el estudiante tiene matrícula activa en la sección")
    public boolean tieneMatriculaActiva(UUID estudianteId, UUID seccionId) {
        log.info("Consultando ms-matriculas: ¿estudiante {} matriculado en sección {}?",
                estudianteId, seccionId);
        try {
            String url = msMatriculasUrl + "/api/v1/matriculas";

            List<MatriculaResponse> matriculas = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<MatriculaResponse>>() {}
            ).getBody();

            if (matriculas == null || matriculas.isEmpty()) {
                log.warn("ms-matriculas devolvió lista vacía");
                return false;
            }

            boolean resultado = matriculas.stream()
                    .anyMatch(m ->
                            m.estudianteId().equals(estudianteId) &&
                            m.seccionId().equals(seccionId) &&
                            "ACTIVA".equals(m.estado())
                    );

            log.info("Resultado: estudiante {} en sección {} → matrícula activa: {}",
                    estudianteId, seccionId, resultado);
            return resultado;

        } catch (HttpClientErrorException e) {
            log.error("Error HTTP al consultar ms-matriculas: {}", e.getMessage());
            throw new IllegalStateException("Error al consultar ms-matriculas: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error de conexión con ms-matriculas: {}", e.getMessage());
            throw new IllegalStateException("No se pudo conectar con ms-matriculas: " + e.getMessage());
        }
    }
}
