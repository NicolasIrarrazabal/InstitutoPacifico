package com.proyecto.ms_asignaturas.repository;

import com.proyecto.ms_asignaturas.model.Credito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CreditoRepository extends JpaRepository<Credito, UUID> {
}
