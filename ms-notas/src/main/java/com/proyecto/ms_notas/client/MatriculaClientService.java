package com.proyecto.ms_notas.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

// Este servicio se encarga de llamar al microservicio ms-matriculas
// Es el puente de comunicación entre ms-notas y ms-matriculas
@Service
@Slf4j                    // Lombok genera el logger: log.info(), log.error(), etc.
@RequiredArgsConstructor  // Lombok genera el constructor con los campos final (inyección de dependencias)
public class MatriculaClientService {

    // RestTemplate es el cliente HTTP que usamos para llamar a otros microservicios
    private final RestTemplate restTemplate;

    // @Value: inyecta el valor de la propiedad desde application.properties
    // Lee ms-matriculas.url=${MS_MATRICULAS_URL} y lo inyecta aquí
    @Value("${ms-matriculas.url}")
    private String msMatriculasUrl;

    // R1: verifica matrícula activa antes de registrar nota
    public boolean tieneMatriculaActiva(UUID estudianteId, UUID seccionId) {
        log.info("Consultando ms-matriculas: ¿estudiante {} tiene matrícula activa en sección {}?",
                estudianteId, seccionId);
        try {
            // Construimos la URL del endpoint de ms-matriculas
            String url = msMatriculasUrl + "/api/v1/matriculas";

            // Llamamos al endpoint y pedimos la lista de matrículas
            // ParameterizedTypeReference nos permite deserializar List<MatriculaResponse>
            List<MatriculaResponse> matriculas = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<MatriculaResponse>>() {}
            ).getBody();

            if (matriculas == null || matriculas.isEmpty()) {
                log.warn("ms-matriculas devolvió lista vacía para estudiante {}", estudianteId);
                return false;
            }

            // Filtramos: buscamos una matrícula que coincida con el estudiante, la sección y esté ACTIVA
            boolean tieneMatricula = matriculas.stream()
                    .anyMatch(m ->
                            m.estudianteId().equals(estudianteId) &&
                            m.seccionId().equals(seccionId) &&
                            "ACTIVA".equals(m.estado())
                    );

            log.info("Resultado validación R1 - estudiante {} en sección {}: {}",
                    estudianteId, seccionId, tieneMatricula ? "TIENE matrícula activa" : "NO tiene matrícula activa");

            return tieneMatricula;

        } catch (HttpClientErrorException e) {
            // Error HTTP del otro microservicio (ej: 404, 400)
            log.error("Error HTTP al consultar ms-matriculas: {} - {}", e.getStatusCode(), e.getMessage());
            throw new IllegalStateException("Error al consultar ms-matriculas: " + e.getMessage());
        } catch (Exception e) {
            // Error de conexión u otro error inesperado
            log.error("Error de conexión con ms-matriculas: {}", e.getMessage());
            throw new IllegalStateException("No se pudo conectar con ms-matriculas: " + e.getMessage());
        }
    }
}
