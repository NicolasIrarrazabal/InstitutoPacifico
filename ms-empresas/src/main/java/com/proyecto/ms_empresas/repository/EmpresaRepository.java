package com.proyecto.ms_empresas.repository;

import com.proyecto.ms_empresas.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {

    Optional<Empresa> findByRut(String rut);
    boolean existsByRut(String rut);
}
