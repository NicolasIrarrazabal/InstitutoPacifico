package com.proyecto.ms_asignaturas.service;

import com.proyecto.ms_asignaturas.repository.PrerequisitoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PrerequisitoService {

    private PrerequisitoRepository prerequisitoRepository;
}
