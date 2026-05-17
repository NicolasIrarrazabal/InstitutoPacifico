package com.proyecto.ms_practicas.repository;

import com.proyecto.ms_practicas.model.Practica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PracticaRepository extends JpaRepository<Practica, UUID> {

    // Todas las prácticas de un estudiante
    List<Practica> findByEstudianteId(UUID estudianteId);

    // Para saber si el estudiante ya tiene una práctica activa (no puede tener dos a la vez)
    boolean existsByEstudianteIdAndEstadoIn(UUID estudianteId, List<String> estados);
}
