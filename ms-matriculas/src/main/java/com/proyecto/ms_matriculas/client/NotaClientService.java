package com.proyecto.ms_matriculas.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotaClientService {

    private final RestTemplate restTemplate;

    @Value("${ms-notas.url:http://localhost:8083}")
    private String msNotasUrl;

    public boolean estudianteAproboAsignatura(UUID estudianteId, UUID asignaturaId) {
        log.info("Verificando si estudiante {} aprobó asignatura {}", estudianteId, asignaturaId);
        String url = msNotasUrl + "/api/v1/notas/estudiante/" + estudianteId
                + "/aprobo-asignatura/" + asignaturaId;
        Boolean resultado = restTemplate.getForObject(url, Boolean.class);
        return Boolean.TRUE.equals(resultado);
    }
}
