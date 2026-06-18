package com.proyecto.ms_estudiante.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatriculaClientService {

    private final RestTemplate restTemplate;

    @Value("${ms-matriculas.url:http://localhost:8084}")
    private String msMatriculasUrl;

    public List<MatriculaResponse> obtenerMatriculasActivas(UUID estudianteId) {
        log.info("Llamando a ms-matriculas para obtener matrículas activas del estudiante {}", estudianteId);
        String url = msMatriculasUrl + "/api/v1/matriculas/estudiante/" + estudianteId;
        List<MatriculaResponse> matriculas = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<MatriculaResponse>>() {}).getBody();
        return matriculas != null ? matriculas : List.of();
    }

    public record MatriculaResponse(UUID id, UUID estudianteId, UUID seccionId, String estado) {}
}
