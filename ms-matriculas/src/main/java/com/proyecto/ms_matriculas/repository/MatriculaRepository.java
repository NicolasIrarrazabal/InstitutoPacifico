package com.proyecto.ms_matriculas.repository;

import com.proyecto.ms_matriculas.model.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, UUID> {
}