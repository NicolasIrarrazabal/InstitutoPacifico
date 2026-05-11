package com.proyecto.ms_estudiante.repository;

import com.proyecto.ms_estudiante.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    Optional<Estudiante> findByRut(String rut);

    boolean existsByRut(String rut);

    boolean existsByEmail(String email);
}