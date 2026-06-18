package com.proyecto.ms_estudiante.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotaClientService {

    private final RestTemplate restTemplate;

    @Value("${ms-notas.url:http://localhost:8083}")
    private String msNotasUrl;

    public List<NotaResponse> obtenerNotasEstudiante(UUID estudianteId) {
        log.info("Llamando a ms-notas para obtener notas del estudiante {}", estudianteId);
        String url = msNotasUrl + "/api/v1/notas/estudiante/" + estudianteId;
        List<NotaResponse> notas = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<NotaResponse>>() {}).getBody();
        return notas != null ? notas : List.of();
    }

    public record NotaResponse(UUID id, UUID estudianteId, UUID seccionId,
                               BigDecimal nota, String tipo, BigDecimal ponderacion) {}

    public record PromedioResponse(UUID estudianteId, BigDecimal promedioPonderado,
                                   BigDecimal promedioSimple, int totalNotas, boolean aprobado) {}
}
