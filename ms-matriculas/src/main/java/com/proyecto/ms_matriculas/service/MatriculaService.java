package com.proyecto.ms_matriculas.service;

import com.proyecto.ms_matriculas.dto.MatriculaDTO;
import com.proyecto.ms_matriculas.model.Matricula;
import com.proyecto.ms_matriculas.repository.MatriculaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MatriculaService {

    @Autowired
    private MatriculaRepository MatriculaRepository;

    @Transactional
    public Matricula crearMatricula(MatriculaDTO matriculaDTO) {
        // Crear la entidad Matricula usando los datos del DTO
        Matricula matricula = new Matricula();
        matricula.setEstudianteId(matriculaDTO.getEstudianteId());
        matricula.setSeccionId(matriculaDTO.getSeccionId());
        matricula.setFechaMatricula(matriculaDTO.getFechaMatricula());
        matricula.setEstado(matriculaDTO.getEstado());

        // Guardar la matrícula en la base de datos
        return MatriculaRepository.save(matricula);
    }
}