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

    // Buscar todos los registros activos de un estudiante en una sección
    // Spring JPA genera el SQL automáticamente desde el nombre del método
    List<Asistencia> findByEstudianteIdAndSeccionIdAndEstado(
            UUID estudianteId, UUID seccionId, String estado);

    // Buscar todos los registros activos de una sección (para listar por clase)
    List<Asistencia> findBySeccionIdAndEstado(UUID seccionId, String estado);

    // Buscar todos los registros activos de una sección en una fecha específica
    List<Asistencia> findBySeccionIdAndFechaAndEstado(
            UUID seccionId, LocalDate fecha, String estado);

    // Buscar todos los registros activos de un estudiante en todas las secciones
    List<Asistencia> findByEstudianteIdAndEstado(UUID estudianteId, String estado);

    // Verificar si ya existe un registro activo para ese estudiante, sección y fecha
    // (para evitar registrar dos veces la misma clase)
    boolean existsByEstudianteIdAndSeccionIdAndFechaAndEstado(
            UUID estudianteId, UUID seccionId, LocalDate fecha, String estado);

    // Buscar el registro específico de un estudiante en una sección y fecha
    Optional<Asistencia> findByEstudianteIdAndSeccionIdAndFechaAndEstado(
            UUID estudianteId, UUID seccionId, LocalDate fecha, String estado);

    // consultas para el cálculo de R2

    // @Query: cuando la consulta es más compleja, la escribimos en JPQL
    // Cuenta cuántas clases ACTIVAS hay en total para esa sección
    // (todas las fechas distintas con al menos un registro)
    @Query("""
            SELECT COUNT(DISTINCT a.fecha)
            FROM Asistencia a
            WHERE a.seccionId = :seccionId
              AND a.estado = 'ACTIVO'
            """)
    long contarTotalClasesPorSeccion(@Param("seccionId") UUID seccionId);

    // Cuenta las ausencias INJUSTIFICADAS del estudiante en la sección
    // Solo AUSENTE cuenta; JUSTIFICADO no
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
