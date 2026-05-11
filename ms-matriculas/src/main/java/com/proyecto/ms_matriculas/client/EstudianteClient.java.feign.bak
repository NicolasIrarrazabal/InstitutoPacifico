package com.proyecto.ms_matriculas.client;

import com.proyecto.ms_matriculas.dto.EstudianteResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "ms-estudiante", url = "${ms-estudiante.url:http://localhost:8081}")
public interface EstudianteClient {

    @GetMapping("/api/v1/estudiantes/{id}")
    EstudianteResponseDTO obtenerEstudiante(@PathVariable("id") UUID id);

    @GetMapping("/api/v1/estudiantes/{id}/puede-matricular")
    PuedeMatricularResponse puedeMatricular(@PathVariable("id") UUID id);
}