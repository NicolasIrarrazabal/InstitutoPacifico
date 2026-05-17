package com.proyecto.ms_notas.repository;

import com.proyecto.ms_notas.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

// @Repository: marca esta interfaz como componente de acceso a datos
// JpaRepository<Nota, UUID>: Spring genera automáticamente los métodos CRUD
// (findAll, findById, save, delete, etc.) sin que tengamos que escribirlos
@Repository
public interface NotaRepository extends JpaRepository<Nota, UUID> {

    // Spring JPA genera el SQL automáticamente a partir del nombre del método:
    // "findBy" + "EstudianteId" + "And" + "Estado" = WHERE estudiante_id = ? AND estado = ?
    List<Nota> findByEstudianteIdAndEstado(UUID estudianteId, String estado);

    // Buscar todas las notas activas de un estudiante en una sección específica
    List<Nota> findByEstudianteIdAndSeccionIdAndEstado(UUID estudianteId, UUID seccionId, String estado);

    // Buscar todas las notas activas de una sección
    List<Nota> findBySeccionIdAndEstado(UUID seccionId, String estado);

    // @Query: cuando la consulta es más compleja, se puede escribir en JPQL (similar a SQL)
    // Esta consulta obtiene los IDs únicos de secciones donde el estudiante tiene notas activas
    @Query("SELECT DISTINCT n.seccionId FROM Nota n WHERE n.estudianteId = :estudianteId AND n.estado = 'ACTIVA'")
    List<UUID> findSeccionesActivasByEstudianteId(@Param("estudianteId") UUID estudianteId);

    // Verifica si ya existe una nota con el mismo tipo para un estudiante en la misma sección
    boolean existsByEstudianteIdAndSeccionIdAndTipoAndEstado(
            UUID estudianteId, UUID seccionId, String tipo, String estado);
}
