package com.proyecto.ms_asignaturas.service;

import com.proyecto.ms_asignaturas.repository.CreditoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CreditoService {

    private CreditoRepository  creditoRepository;
}
