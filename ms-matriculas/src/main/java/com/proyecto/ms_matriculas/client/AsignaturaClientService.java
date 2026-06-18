package com.proyecto.ms_matriculas.client;

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
public class AsignaturaClientService {

    private final RestTemplate restTemplate;

    @Value("${ms-asignaturas.url:http://localhost:8082}")
    private String msAsignaturasUrl;

    public List<PrerequisitosResponse> obtenerPrerequisitos(UUID asignaturaId) {
        log.info("Llamando a ms-asignaturas para obtener prerequisitos de asignatura {}", asignaturaId);
        String url = msAsignaturasUrl + "/api/v1/prerequisitos/asignatura/" + asignaturaId;
        List<PrerequisitosResponse> prereqs = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<PrerequisitosResponse>>() {}).getBody();
        return prereqs != null ? prereqs : List.of();
    }
}
