package com.proyecto.ms_notas.service;

import com.proyecto.ms_notas.client.ArancelClientService;
import com.proyecto.ms_notas.client.MatriculaClientService;
import com.proyecto.ms_notas.dto.AvanceResponseDTO;
import com.proyecto.ms_notas.dto.NotaDTO;
import com.proyecto.ms_notas.dto.PromedioResponseDTO;
import com.proyecto.ms_notas.model.Nota;
import com.proyecto.ms_notas.repository.NotaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotaService {

    private final NotaRepository repository;
    private final MatriculaClientService matriculaClient;
    private final ArancelClientService arancelClient;

    private static final BigDecimal NOTA_APROBACION         = new BigDecimal("4.0");
    private static final BigDecimal NOTA_LIMITE_RECUPERACION = new BigDecimal("3.5");

    private static final String ESTADO_APROBADO              = "APROBADO";
    private static final String ESTADO_PENDIENTE_RECUPERACION = "PENDIENTE_EXAMEN_RECUPERACION";
    private static final String ESTADO_REPROBADO             = "REPROBADO";

    private static final double PORCENTAJE_AVANCE_MINIMO = 80.0;

    public List<Nota> findAll() {
        log.info("Listando todas las notas activas");
        return repository.findAll()
                .stream()
                .filter(n -> "ACTIVA".equals(n.getEstado()))
                .toList();
    }

    public Nota findById(UUID id) {
        log.info("Buscando nota por ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nota no encontrada con ID: " + id));
    }

    public List<Nota> findByEstudiante(UUID estudianteId) {
        log.info("Buscando notas activas del estudiante: {}", estudianteId);

        validarSinDeudaVencidaR4(estudianteId);

        return repository.findByEstudianteIdAndEstado(estudianteId, "ACTIVA");
    }

    @Transactional
    public Nota create(NotaDTO dto) {
        log.info("Creando nota para estudiante {} en sección {}", dto.estudianteId(), dto.seccionId());

        validarMatriculaActiva(dto.estudianteId(), dto.seccionId());

        if (repository.existsByEstudianteIdAndSeccionIdAndTipoAndEstado(
                dto.estudianteId(), dto.seccionId(), dto.tipo(), "ACTIVA")) {
            log.warn("Ya existe una nota de tipo {} para estudiante {} en sección {}",
                    dto.tipo(), dto.estudianteId(), dto.seccionId());
            throw new IllegalStateException(
                    "Ya existe una nota de tipo '" + dto.tipo() +
                    "' para este estudiante en esta sección. Use PUT para actualizar.");
        }

        Nota nota = new Nota();
        nota.setEstudianteId(dto.estudianteId());
        nota.setSeccionId(dto.seccionId());
        nota.setNota(dto.nota());
        nota.setTipo(dto.tipo());
        nota.setPonderacion(dto.ponderacion());
        nota.setFecha(dto.fecha());
        nota.setEstado("ACTIVA");

        Nota guardada = repository.save(nota);
        log.info("Nota creada con ID: {} | Estudiante: {} | Nota: {} | Aprobado: {}",
                guardada.getId(), guardada.getEstudianteId(), guardada.getNota(), guardada.isAprobado());
        return guardada;
    }

    @Transactional
    public Nota update(UUID id, NotaDTO dto) {
        log.info("Actualizando nota ID: {}", id);
        Nota nota = findById(id);

        if ("ANULADA".equals(nota.getEstado())) {
            throw new IllegalStateException("No se puede modificar una nota anulada");
        }

        nota.setNota(dto.nota());
        nota.setTipo(dto.tipo());
        nota.setPonderacion(dto.ponderacion());
        nota.setFecha(dto.fecha());

        Nota actualizada = repository.save(nota);
        log.info("Nota actualizada ID: {} | Nueva nota: {}", id, actualizada.getNota());
        return actualizada;
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Anulando (eliminación lógica) nota ID: {}", id);
        Nota nota = findById(id);
        nota.setEstado("ANULADA");
        repository.save(nota);
        log.info("Nota marcada como ANULADA, ID: {}", id);
    }

    public PromedioResponseDTO calcularPromedio(UUID estudianteId) {
        log.info("[R3] Calculando promedio global para estudiante: {}", estudianteId);

        validarSinDeudaVencidaR4(estudianteId);

        List<Nota> notas = repository.findByEstudianteIdAndEstado(estudianteId, "ACTIVA");

        if (notas.isEmpty()) {
            log.warn("[R3] Sin notas activas para estudiante: {}", estudianteId);
            throw new EntityNotFoundException("No hay notas registradas para el estudiante: " + estudianteId);
        }

        BigDecimal promedioPonderado = calcularPromedioPonderado(notas);
        BigDecimal promedioSimple    = calcularPromedioSimple(notas);

        String estadoAcademico = determinarEstadoAcademicoR3(promedioPonderado);
        String mensajeR3       = construirMensajeR3(estadoAcademico, promedioPonderado);
        boolean aprobado       = ESTADO_APROBADO.equals(estadoAcademico);

        log.info("[R3] Estudiante: {} | Promedio: {} | Estado: {}",
                estudianteId, promedioPonderado, estadoAcademico);

        return new PromedioResponseDTO(
                estudianteId,
                null,
                promedioPonderado,
                promedioSimple,
                notas.size(),
                aprobado,
                estadoAcademico,
                mensajeR3
        );
    }

    public PromedioResponseDTO calcularPromedioSeccion(UUID estudianteId, UUID seccionId) {
        log.info("[R3] Calculando promedio por sección — estudiante: {} | sección: {}",
                estudianteId, seccionId);

        validarSinDeudaVencidaR4(estudianteId);

        List<Nota> notas = repository.findByEstudianteIdAndSeccionIdAndEstado(
                estudianteId, seccionId, "ACTIVA");

        if (notas.isEmpty()) {
            log.warn("[R3] Sin notas activas para estudiante {} en sección {}", estudianteId, seccionId);
            throw new EntityNotFoundException(
                    "No hay notas registradas para el estudiante " + estudianteId +
                    " en la sección " + seccionId);
        }

        BigDecimal promedioPonderado = calcularPromedioPonderado(notas);
        BigDecimal promedioSimple    = calcularPromedioSimple(notas);

        String estadoAcademico = determinarEstadoAcademicoR3(promedioPonderado);
        String mensajeR3       = construirMensajeR3(estadoAcademico, promedioPonderado);
        boolean aprobado       = ESTADO_APROBADO.equals(estadoAcademico);

        log.info("[R3] Estudiante: {} | Sección: {} | Promedio: {} | Estado: {}",
                estudianteId, seccionId, promedioPonderado, estadoAcademico);

        return new PromedioResponseDTO(
                estudianteId,
                seccionId,
                promedioPonderado,
                promedioSimple,
                notas.size(),
                aprobado,
                estadoAcademico,
                mensajeR3
        );
    }

    public AvanceResponseDTO calcularAvance(UUID estudianteId) {
        log.info("Calculando avance académico del 80% para estudiante: {}", estudianteId);

        List<UUID> secciones = repository.findSeccionesActivasByEstudianteId(estudianteId);

        if (secciones.isEmpty()) {
            log.warn("No se encontraron secciones con notas para estudiante: {}", estudianteId);
            throw new EntityNotFoundException("No hay notas registradas para el estudiante: " + estudianteId);
        }

        int totalSecciones = secciones.size();
        int seccionesAprobadas = 0;

        for (UUID seccionId : secciones) {
            List<Nota> notasSeccion = repository.findByEstudianteIdAndSeccionIdAndEstado(
                    estudianteId, seccionId, "ACTIVA");

            if (!notasSeccion.isEmpty()) {
                BigDecimal promedio = calcularPromedioPonderado(notasSeccion);
                if (promedio.compareTo(NOTA_APROBACION) >= 0) {
                    seccionesAprobadas++;
                }
            }
        }

        double porcentaje = totalSecciones > 0
                ? ((double) seccionesAprobadas / totalSecciones) * 100.0
                : 0.0;

        porcentaje = Math.round(porcentaje * 10.0) / 10.0;

        boolean cumple80 = porcentaje >= PORCENTAJE_AVANCE_MINIMO;

        log.info("Avance estudiante {}: {}/{} secciones aprobadas ({}%) | Cumple 80%: {}",
                estudianteId, seccionesAprobadas, totalSecciones, porcentaje, cumple80);

        return new AvanceResponseDTO(
                estudianteId,
                totalSecciones,
                seccionesAprobadas,
                porcentaje,
                cumple80
        );
    }

    private String determinarEstadoAcademicoR3(BigDecimal promedio) {
        if (promedio.compareTo(NOTA_APROBACION) >= 0) {
            return ESTADO_APROBADO;
        } else if (promedio.compareTo(NOTA_LIMITE_RECUPERACION) >= 0) {
            return ESTADO_PENDIENTE_RECUPERACION;
        } else {
            return ESTADO_REPROBADO;
        }
    }

    private String construirMensajeR3(String estadoAcademico, BigDecimal promedio) {
        return switch (estadoAcademico) {
            case ESTADO_APROBADO ->
                String.format(
                    "✅ APROBADO (R3): promedio ponderado %.1f — superior al mínimo de %.1f. " +
                    "El estudiante ha aprobado la asignatura.",
                    promedio, NOTA_APROBACION);

            case ESTADO_PENDIENTE_RECUPERACION ->
                String.format(
                    "⚠️ PENDIENTE DE EXAMEN DE RECUPERACIÓN (R3): promedio ponderado %.1f — " +
                    "dentro del rango de recuperación (%.1f – %.1f). " +
                    "El estudiante puede rendir el examen de recuperación para aprobar. " +
                    "Si no lo rinde o lo reprueba, quedará reprobado.",
                    promedio, NOTA_LIMITE_RECUPERACION,
                    NOTA_APROBACION.subtract(new BigDecimal("0.1")));

            case ESTADO_REPROBADO ->
                String.format(
                    "❌ REPROBADO (R3): promedio ponderado %.1f — inferior al límite de recuperación (%.1f). " +
                    "El estudiante queda REPROBADO directamente, sin derecho a examen de recuperación.",
                    promedio, NOTA_LIMITE_RECUPERACION);

            default -> "Estado académico no determinado.";
        };
    }

    private BigDecimal calcularPromedioPonderado(List<Nota> notas) {
        BigDecimal sumaPonderada    = BigDecimal.ZERO;
        BigDecimal sumaPonderaciones = BigDecimal.ZERO;

        for (Nota n : notas) {
            sumaPonderada     = sumaPonderada.add(n.getNota().multiply(n.getPonderacion()));
            sumaPonderaciones = sumaPonderaciones.add(n.getPonderacion());
        }

        return sumaPonderaciones.compareTo(BigDecimal.ZERO) > 0
                ? sumaPonderada.divide(sumaPonderaciones, 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
    }

    private BigDecimal calcularPromedioSimple(List<Nota> notas) {
        BigDecimal suma = notas.stream()
                .map(Nota::getNota)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return suma.divide(new BigDecimal(notas.size()), 1, RoundingMode.HALF_UP);
    }

    private void validarSinDeudaVencidaR4(UUID estudianteId) {
        log.info("[R4] Verificando deuda vencida del estudiante: {}", estudianteId);
        boolean tieneDeuda = arancelClient.tieneDeudaVencida(estudianteId);
        if (tieneDeuda) {
            log.warn("[R4] BLOQUEADO — estudiante {} tiene aranceles vencidos >45 días", estudianteId);
            throw new IllegalStateException(
                "R4: El estudiante tiene aranceles vencidos por más de 45 días. " +
                "El acceso al historial de notas está bloqueado hasta regularizar la situación."
            );
        }
        log.info("[R4] OK — estudiante {} sin deuda vencida, acceso permitido", estudianteId);
    }

    private void validarMatriculaActiva(UUID estudianteId, UUID seccionId) {
        log.info("R1 validando matrícula - estudiante {} sección {}", estudianteId, seccionId);
        try {
            boolean tieneMatricula = matriculaClient.tieneMatriculaActiva(estudianteId, seccionId);
            if (!tieneMatricula) {
                log.warn("R1 RECHAZADO: estudiante {} no tiene matrícula activa en sección {}", estudianteId, seccionId);
                throw new IllegalStateException(
                        "R1: El estudiante no tiene una matrícula ACTIVA en la sección indicada. " +
                        "No se puede registrar la nota.");
            }
            log.info("R1 APROBADO: estudiante {} tiene matrícula activa en sección {}", estudianteId, seccionId);
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("R1: Error al validar matrícula: {}", e.getMessage());
            throw new IllegalStateException("Error al validar prerrequisito R1: " + e.getMessage());
        }
    }
}
