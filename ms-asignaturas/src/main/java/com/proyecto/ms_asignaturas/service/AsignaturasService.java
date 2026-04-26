package com.proyecto.ms_asignaturas.service;

import com.proyecto.ms_asignaturas.repository.AsignaturasRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AsignaturasService {

    private AsignaturasRepository asignaturasRepository;
}
