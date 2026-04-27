package com.proyecto.ms_matriculas.controller;

import com.proyecto.ms_matriculas.dto.MatriculaDTO;
import com.proyecto.ms_matriculas.model.Matricula;
import com.proyecto.ms_matriculas.service.MatriculaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/matriculas")
public class MatriculaController {

    @Autowired
    private MatriculaService matriculaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Matricula crearMatricula(@RequestBody MatriculaDTO matriculaDTO) {
        return matriculaService.crearMatricula(matriculaDTO);
    }
}