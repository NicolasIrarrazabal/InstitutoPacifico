package com.proyecto.ms_asignaturas.controller;

import com.proyecto.ms_asignaturas.repository.PrerequisitoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PrerequisitoController {

    private PrerequisitoRepository prerequisitoRepository;
}
