package com.proyecto.ms_asignaturas.repository;

import com.proyecto.ms_asignaturas.model.Credito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CreditoRepository extends JpaRepository<Credito, UUID> {
}
