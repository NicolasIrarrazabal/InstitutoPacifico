package com.proyecto.ms_estudiante.service;

import com.proyecto.ms_estudiante.client.EstudianteClient;
import com.proyecto.ms_estudiante.dto.EstudianteDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EstudianteClientService {

    private final EstudianteClient cliente;

    public EstudianteDTO obtener(Long id) {
        return cliente.obtenerEstudiante(id);
    }

}