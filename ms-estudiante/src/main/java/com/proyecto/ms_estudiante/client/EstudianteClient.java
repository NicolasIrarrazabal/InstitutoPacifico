package com.proyecto.ms_estudiante.client;


import com.proyecto.ms_estudiante.dto.EstudianteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "servicio-usuario", url = "http://localhost:8081")
public interface EstudianteClient {

    @GetMapping("/usuarios/{id}")
    EstudianteDTO obtenerEstudiante(@PathVariable("id") Long id);
}