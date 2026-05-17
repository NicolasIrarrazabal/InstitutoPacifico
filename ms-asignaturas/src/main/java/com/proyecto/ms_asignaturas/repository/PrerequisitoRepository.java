package com.proyecto.ms_asignaturas.repository;

import com.proyecto.ms_asignaturas.model.Prerequisito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrerequisitoRepository extends JpaRepository<Prerequisito, UUID> {

    // Busca todos los prerrequisitos de una asignatura principal
    List<Prerequisito> findByAsignaturaPrincipalId(UUID asignaturaPrincipalId);
}
