package com.proyecto.ms_matriculas.client;

import com.proyecto.ms_matriculas.dto.EstudianteResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EstudianteClientService {

    private final RestTemplate restTemplate;

    @Value("${ms-estudiante.url}")
    private String msEstudianteUrl;

    public EstudianteResponseDTO obtenerEstudiante(UUID id) {
        log.info("Llamando a ms-estudiante para obtener estudiante {}", id);
        String url = msEstudianteUrl + "/api/v1/estudiantes/" + id;
        return restTemplate.getForObject(url, EstudianteResponseDTO.class);
    }

    public PuedeMatricularResponse puedeMatricular(UUID id) {
        log.info("Llamando a ms-estudiante para validar puede matricular {}", id);
        String url = msEstudianteUrl + "/api/v1/estudiantes/" + id + "/puede-matricular";
        return restTemplate.getForObject(url, PuedeMatricularResponse.class);
    }
}