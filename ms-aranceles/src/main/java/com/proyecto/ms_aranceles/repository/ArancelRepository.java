package com.proyecto.ms_aranceles.repository;

import com.proyecto.ms_aranceles.model.Arancel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArancelRepository extends JpaRepository<Arancel, UUID> {

    // Busca todos los aranceles de un estudiante
    List<Arancel> findByEstudianteId(UUID estudianteId);

    // Busca los aranceles no pagados de un estudiante
    List<Arancel> findByEstudianteIdAndEstadoNot(UUID estudianteId, String estado);
}
