package com.proyecto.ms_carreras.repository;

import com.proyecto.ms_carreras.model.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, UUID> {
}