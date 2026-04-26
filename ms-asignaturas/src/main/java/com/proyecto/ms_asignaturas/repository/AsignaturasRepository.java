package com.proyecto.ms_asignaturas.repository;

import com.proyecto.ms_asignaturas.model.Asignatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AsignaturasRepository extends JpaRepository<Asignatura, UUID> {
}
