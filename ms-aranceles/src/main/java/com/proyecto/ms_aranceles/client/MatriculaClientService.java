package com.proyecto.ms_aranceles.client;

import com.proyecto.ms_aranceles.dto.MatriculaResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatriculaClientService {

    private final RestTemplate restTemplate;

    @Value("${ms-matricula.url}")
    private String msMatriculaUrl;

    // Consulta ms-matriculas para traer los datos de una matrícula específica
    public MatriculaResponseDTO obtenerMatricula(UUID matriculaId) {
        log.info("Llamando a ms-matriculas para obtener matrícula {}", matriculaId);
        String url = msMatriculaUrl + "/api/v1/matriculas/" + matriculaId;
        return restTemplate.getForObject(url, MatriculaResponseDTO.class);
    }
}
