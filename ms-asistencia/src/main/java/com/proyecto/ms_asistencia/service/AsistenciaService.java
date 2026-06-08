package com.proyecto.ms_asistencia.service;

import com.proyecto.ms_asistencia.client.MatriculaClientService;
import com.proyecto.ms_asistencia.dto.AsistenciaDTO;
import com.proyecto.ms_asistencia.dto.RegistroAsistenciaResponseDTO;
import com.proyecto.ms_asistencia.dto.ResumenAsistenciaDTO;
import com.proyecto.ms_asistencia.model.Asistencia;
import com.proyecto.ms_asistencia.model.TipoAsistencia;
import com.proyecto.ms_asistencia.repository.AsistenciaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsistenciaService {

    private final AsistenciaRepository repository;
    private final MatriculaClientService matriculaClient;

    private static final double LIMITE_INASISTENCIA = 25.0;

    @Transactional
    public RegistroAsistenciaResponseDTO registrar(AsistenciaDTO dto) {
        log.info("Registrando asistencia — estudiante: {} | sección: {} | fecha: {} | tipo: {}",
                dto.estudianteId(), dto.seccionId(), dto.fecha(), dto.tipo());

        validarMatricula(dto.estudianteId(), dto.seccionId());

        if (repository.existsByEstudianteIdAndSeccionIdAndFechaAndEstado(
                dto.estudianteId(), dto.seccionId(), dto.fecha(), "ACTIVO")) {
            log.warn("Ya existe un registro de asistencia para estudiante {} en sección {} el {}",
                    dto.estudianteId(), dto.seccionId(), dto.fecha());
            throw new IllegalStateException(
                    "Ya existe un registro de asistencia para este estudiante en esta sección y fecha. " +
                    "Use PUT para corregirlo.");
        }

        Asistencia asistencia = new Asistencia();
        asistencia.setEstudianteId(dto.estudianteId());
        asistencia.setSeccionId(dto.seccionId());
        asistencia.setFecha(dto.fecha());
        asistencia.setTipo(dto.tipo());
        asistencia.setObservacion(dto.observacion());
        asistencia.setEstado("ACTIVO");

        Asistencia guardada = repository.save(asistencia);
        log.info("Asistencia guardada con ID: {}", guardada.getId());

        ResumenAsistenciaDTO resumen = calcularResumenR2(dto.estudianteId(), dto.seccionId());

        if (resumen.reprobadoPorAsistencia()) {

            log.warn("⚠️  R2 ACTIVADA — Estudiante {} REPROBADO POR ASISTENCIA en sección {} " +
                     "({}% de inasistencia, límite: {}%)",
                    dto.estudianteId(), dto.seccionId(),
                    resumen.porcentajeInasistencia(), LIMITE_INASISTENCIA);
        }

        return new RegistroAsistenciaResponseDTO(guardada, resumen);
    }

    @Transactional
    public RegistroAsistenciaResponseDTO actualizar(UUID id, AsistenciaDTO dto) {
        log.info("Actualizando asistencia ID: {}", id);
        Asistencia asistencia = findById(id);

        if ("ANULADO".equals(asistencia.getEstado())) {
            throw new IllegalStateException("No se puede modificar un registro anulado");
        }

        TipoAsistencia tipoAnterior = asistencia.getTipo();
        asistencia.setTipo(dto.tipo());
        asistencia.setObservacion(dto.observacion());

        Asistencia actualizada = repository.save(asistencia);
        log.info("Asistencia actualizada: {} → {} (ID: {})", tipoAnterior, dto.tipo(), id);

        ResumenAsistenciaDTO resumen = calcularResumenR2(
                actualizada.getEstudianteId(), actualizada.getSeccionId());

        return new RegistroAsistenciaResponseDTO(actualizada, resumen);
    }

    @Transactional
    public void anular(UUID id) {
        log.info("Anulando registro de asistencia ID: {}", id);
        Asistencia asistencia = findById(id);
        asistencia.setEstado("ANULADO");
        repository.save(asistencia);
        log.info("Asistencia anulada ID: {}", id);
    }

    public Asistencia findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Registro de asistencia no encontrado con ID: " + id));
    }

    public List<Asistencia> findBySeccion(UUID seccionId) {
        log.info("Listando asistencias de sección: {}", seccionId);
        return repository.findBySeccionIdAndEstado(seccionId, "ACTIVO");
    }

    public List<Asistencia> findByEstudiante(UUID estudianteId) {
        log.info("Listando asistencias del estudiante: {}", estudianteId);
        return repository.findByEstudianteIdAndEstado(estudianteId, "ACTIVO");
    }

    public List<Asistencia> findByEstudianteYSeccion(UUID estudianteId, UUID seccionId) {
        log.info("Listando asistencias de estudiante {} en sección {}", estudianteId, seccionId);
        return repository.findByEstudianteIdAndSeccionIdAndEstado(estudianteId, seccionId, "ACTIVO");
    }

    public ResumenAsistenciaDTO calcularResumenR2(UUID estudianteId, UUID seccionId) {
        log.info("R2: Calculando resumen de asistencia — estudiante: {} | sección: {}",
                estudianteId, seccionId);

        long totalClases = repository.contarTotalClasesPorSeccion(seccionId);

        if (totalClases == 0) {
            log.warn("R2: No hay clases registradas en la sección {}", seccionId);
            return new ResumenAsistenciaDTO(
                    estudianteId, seccionId,
                    0, 0, 0, 0,
                    0.0, false,
                    "Sin clases registradas en esta sección"
            );
        }

        long presentes    = repository.contarPorTipo(estudianteId, seccionId, TipoAsistencia.PRESENTE);
        long ausentes     = repository.contarPorTipo(estudianteId, seccionId, TipoAsistencia.AUSENTE);
        long justificados = repository.contarPorTipo(estudianteId, seccionId, TipoAsistencia.JUSTIFICADO);

        double porcentaje = Math.round(((double) ausentes / totalClases) * 100.0 * 10.0) / 10.0;

        boolean reprobado = porcentaje > LIMITE_INASISTENCIA;

        String mensaje;
        if (reprobado) {
            mensaje = String.format(
                "⚠️ REPROBADO POR ASISTENCIA (R2): el estudiante acumula %.1f%% de inasistencias " +
                "injustificadas, superando el límite del %.0f%%. " +
                "Está REPROBADO independiente de sus notas.",
                porcentaje, LIMITE_INASISTENCIA
            );
        } else {
            double restante = LIMITE_INASISTENCIA - porcentaje;
            mensaje = String.format(
                "Asistencia dentro del límite. Inasistencia: %.1f%% (máximo %.0f%%). " +
                "Puede faltar %.1f%% más antes de reprobar por asistencia.",
                porcentaje, LIMITE_INASISTENCIA, restante
            );
        }

        log.info("R2 resultado — estudiante: {} | sección: {} | total clases: {} | " +
                 "presentes: {} | ausentes: {} | justificados: {} | porcentaje: {}% | reprobado: {}",
                estudianteId, seccionId, totalClases,
                presentes, ausentes, justificados, porcentaje, reprobado);

        return new ResumenAsistenciaDTO(
                estudianteId,
                seccionId,
                (int) totalClases,
                (int) presentes,
                (int) ausentes,
                (int) justificados,
                porcentaje,
                reprobado,
                mensaje
        );
    }

    private void validarMatricula(UUID estudianteId, UUID seccionId) {
        log.info("Verificando matrícula activa para estudiante {} en sección {}", estudianteId, seccionId);
        try {
            boolean activa = matriculaClient.tieneMatriculaActiva(estudianteId, seccionId);
            if (!activa) {
                log.warn("Estudiante {} no tiene matrícula activa en sección {}", estudianteId, seccionId);
                throw new IllegalStateException(
                        "El estudiante no tiene una matrícula ACTIVA en la sección indicada. " +
                        "No se puede registrar asistencia.");
            }
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al verificar matrícula: {}", e.getMessage());
            throw new IllegalStateException("Error al verificar matrícula en ms-matriculas: " + e.getMessage());
        }
    }
}
