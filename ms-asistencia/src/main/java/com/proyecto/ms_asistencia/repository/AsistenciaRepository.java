package com.proyecto.ms_asistencia.repository;

import com.proyecto.ms_asistencia.model.Asistencia;
import com.proyecto.ms_asistencia.model.TipoAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, UUID> {

    List<Asistencia> findByEstudianteIdAndSeccionIdAndEstado(
            UUID estudianteId, UUID seccionId, String estado);

    List<Asistencia> findBySeccionIdAndEstado(UUID seccionId, String estado);

    List<Asistencia> findBySeccionIdAndFechaAndEstado(
            UUID seccionId, LocalDate fecha, String estado);

    List<Asistencia> findByEstudianteIdAndEstado(UUID estudianteId, String estado);

    boolean existsByEstudianteIdAndSeccionIdAndFechaAndEstado(
            UUID estudianteId, UUID seccionId, LocalDate fecha, String estado);

    Optional<Asistencia> findByEstudianteIdAndSeccionIdAndFechaAndEstado(
            UUID estudianteId, UUID seccionId, LocalDate fecha, String estado);

    @Query("""
            SELECT COUNT(DISTINCT a.fecha)
            FROM Asistencia a
            WHERE a.seccionId = :seccionId
              AND a.estado = 'ACTIVO'
            """)
    long contarTotalClasesPorSeccion(@Param("seccionId") UUID seccionId);

    @Query("""
            SELECT COUNT(a)
            FROM Asistencia a
            WHERE a.estudianteId = :estudianteId
              AND a.seccionId    = :seccionId
              AND a.tipo         = :tipo
              AND a.estado       = 'ACTIVO'
            """)
    long contarPorTipo(
            @Param("estudianteId") UUID estudianteId,
            @Param("seccionId")    UUID seccionId,
            @Param("tipo")         TipoAsistencia tipo
    );
}
