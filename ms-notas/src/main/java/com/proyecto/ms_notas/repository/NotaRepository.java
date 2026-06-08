package com.proyecto.ms_notas.repository;

import com.proyecto.ms_notas.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotaRepository extends JpaRepository<Nota, UUID> {

    List<Nota> findByEstudianteIdAndEstado(UUID estudianteId, String estado);

    List<Nota> findByEstudianteIdAndSeccionIdAndEstado(UUID estudianteId, UUID seccionId, String estado);

    List<Nota> findBySeccionIdAndEstado(UUID seccionId, String estado);

    @Query("SELECT DISTINCT n.seccionId FROM Nota n WHERE n.estudianteId = :estudianteId AND n.estado = 'ACTIVA'")
    List<UUID> findSeccionesActivasByEstudianteId(@Param("estudianteId") UUID estudianteId);

    boolean existsByEstudianteIdAndSeccionIdAndTipoAndEstado(
            UUID estudianteId, UUID seccionId, String tipo, String estado);
}
