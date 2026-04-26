package com.proyecto.ms_asignaturas.repository;

import com.proyecto.ms_asignaturas.model.Prerequisito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PrerequisitoRepository extends JpaRepository<Prerequisito, UUID> {
}
