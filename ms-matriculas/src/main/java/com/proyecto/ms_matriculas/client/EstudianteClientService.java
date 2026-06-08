package com.proyecto.ms_matriculas.client;

import com.proyecto.ms_matriculas.dto.EstudianteResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Tag(name = "Estudiante Client Service", description = "Cliente HTTP para comunicación con ms-estudiante")
@Service
@Slf4j
@RequiredArgsConstructor
public class EstudianteClientService {

    private final RestTemplate restTemplate;

    @Value("${ms-estudiante.url}")
    private String msEstudianteUrl;

    @Operation(summary = "Obtener estudiante", description = "Obtiene los datos de un estudiante desde ms-estudiante")
    public EstudianteResponseDTO obtenerEstudiante(UUID id) {
        log.info("Llamando a ms-estudiante para obtener estudiante {}", id);
        String url = msEstudianteUrl + "/api/v1/estudiantes/" + id;
        return restTemplate.getForObject(url, EstudianteResponseDTO.class);
    }

    @Operation(summary = "Verificar puede matricular", description = "Verifica si un estudiante puede matricularse desde ms-estudiante")
    public PuedeMatricularResponse puedeMatricular(UUID id) {
        log.info("Llamando a ms-estudiante para validar puede matricular {}", id);
        String url = msEstudianteUrl + "/api/v1/estudiantes/" + id + "/puede-matricular";
        return restTemplate.getForObject(url, PuedeMatricularResponse.class);
    }
}