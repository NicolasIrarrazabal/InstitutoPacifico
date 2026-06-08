package com.proyecto.ms_aranceles.repository;

import com.proyecto.ms_aranceles.model.Arancel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArancelRepository extends JpaRepository<Arancel, UUID> {

    List<Arancel> findByEstudianteId(UUID estudianteId);

    List<Arancel> findByEstudianteIdAndEstadoNot(UUID estudianteId, String estado);
}
