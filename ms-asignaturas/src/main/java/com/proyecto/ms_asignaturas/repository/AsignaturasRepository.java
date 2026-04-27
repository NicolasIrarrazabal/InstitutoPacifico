package com.proyecto.ms_asignaturas.repository;

import com.proyecto.ms_asignaturas.model.Asignatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AsignaturasRepository extends JpaRepository<Asignatura, UUID> {
}
