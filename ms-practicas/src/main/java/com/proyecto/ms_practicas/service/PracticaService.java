package com.proyecto.ms_practicas.service;

import com.proyecto.ms_practicas.client.ArancelClientService;
import com.proyecto.ms_practicas.client.CreditoClientService;
import com.proyecto.ms_practicas.client.EmpresaClientService;
import com.proyecto.ms_practicas.repository.PracticaRepository;
import com.proyecto.ms_practicas.dto.FinalizarPracticaDTO;
import com.proyecto.ms_practicas.dto.PracticaDTO;
import com.proyecto.ms_practicas.dto.ValidacionR5Response;
import com.proyecto.ms_practicas.model.Practica;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Servicio que gestiona la lógica de negocio para prácticas profesionales.
 * Implementa la validación de la regla R5 (créditos suficientes, arancel al día,
 * convenio vigente) y se comunica con ms-aranceles, ms-notas y ms-empresas.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PracticaService {

    private final PracticaRepository repository;

    private final ArancelClientService arancelClient;
    private final CreditoClientService creditoClient;
    private final EmpresaClientService empresaClient;

    /**
     * Obtiene todas las prácticas registradas.
     *
     * @return lista de prácticas, vacía si no hay registros
     */
    public List<Practica> findAll() {
        log.info("Listando todas las prácticas");
        return repository.findAll();
    }

    /**
     * Busca una práctica por su ID.
     *
     * @param id identificador único de la práctica
     * @return la práctica encontrada
     * @throws jakarta.persistence.EntityNotFoundException si no existe con ese ID
     */
    public Practica findById(UUID id) {
        log.info("Buscando práctica por ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Práctica no encontrada con ID: " + id));
    }

    /**
     * Obtiene todas las prácticas de un estudiante.
     *
     * @param estudianteId identificador del estudiante
     * @return lista de prácticas del estudiante
     */
    public List<Practica> findByEstudiante(UUID estudianteId) {
        log.info("Listando prácticas del estudiante {}", estudianteId);
        return repository.findByEstudianteId(estudianteId);
    }

    /**
     * Verifica si el estudiante cumple los 3 requisitos de la regla R5
     * para inscribir una práctica profesional: créditos suficientes (R5a),
     * arancel al día (R5b) y convenio de empresa vigente (R5c).
     *
     * @param estudianteId identificador del estudiante
     * @param empresaId    identificador de la empresa
     * @return resultado de la validación con cada requisito por separado
     */
    public ValidacionR5Response verificarRequisitosR5(UUID estudianteId, UUID empresaId) {
        log.info("Verificando requisitos R5 para estudiante {} en empresa {}", estudianteId, empresaId);

        boolean creditosOk = false;
        boolean arancelOk  = false;
        boolean empresaOk  = false;

        try { creditosOk = creditoClient.estudianteTieneCreditosSuficientes(estudianteId); }
        catch (Exception e) { log.warn("No se pudo verificar créditos: {}", e.getMessage()); }

        try { arancelOk = arancelClient.estudianteEstaAlDia(estudianteId); }
        catch (Exception e) { log.warn("No se pudo verificar arancel: {}", e.getMessage()); }

        try { empresaOk = empresaClient.empresaTieneConvenioVigente(empresaId); }
        catch (Exception e) { log.warn("No se pudo verificar convenio empresa: {}", e.getMessage()); }

        boolean puedeInscribir = creditosOk && arancelOk && empresaOk;

        String mensaje = puedeInscribir
                ? "El estudiante cumple todos los requisitos para inscribir la práctica."
                : construirMensajeBloqueo(creditosOk, arancelOk, empresaOk);

        log.info("Resultado R5 — estudiante: {} | créditos: {} | arancel: {} | empresa: {} | resultado: {}",
                estudianteId, creditosOk, arancelOk, empresaOk, puedeInscribir);

        return new ValidacionR5Response(creditosOk, arancelOk, empresaOk, puedeInscribir, mensaje);
    }

    /**
     * Inscribe una nueva práctica profesional tras validar la regla R5.
     * Verifica que el estudiante no tenga otra práctica activa
     * y que cumpla los requisitos de créditos, arancel y convenio.
     *
     * @param dto datos de la práctica a inscribir
     * @return la práctica creada con estado EN_CURSO
     * @throws IllegalStateException si ya tiene práctica activa o no cumple R5
     */
    @Transactional
    public Practica create(PracticaDTO dto) {
        log.info("Intentando inscribir práctica — estudiante: {} empresa: {}", dto.estudianteId(), dto.empresaId());

        boolean yaInscrito = repository.existsByEstudianteIdAndEstadoIn(
                dto.estudianteId(), List.of("PENDIENTE", "EN_CURSO")
        );
        if (yaInscrito) {
            log.warn("Estudiante {} ya tiene una práctica activa", dto.estudianteId());
            throw new IllegalStateException("El estudiante ya tiene una práctica activa o en curso");
        }

        ValidacionR5Response validacion = verificarRequisitosR5(dto.estudianteId(), dto.empresaId());
        if (!validacion.puedeInscribir()) {
            log.warn("Inscripción bloqueada para estudiante {}: {}", dto.estudianteId(), validacion.mensaje());
            throw new IllegalStateException(validacion.mensaje());
        }

        Practica p = new Practica();
        p.setEstudianteId(dto.estudianteId());
        p.setEmpresaId(dto.empresaId());
        p.setSupervisorNombre(dto.supervisorNombre());
        p.setFechaInicio(dto.fechaInicio());
        p.setEstado("EN_CURSO");

        Practica guardada = repository.save(p);
        log.info("Práctica inscrita con ID: {}", guardada.getId());
        return guardada;
    }

    /**
     * Finaliza una práctica en curso con estado COMPLETADA o REPROBADA.
     *
     * @param id  identificador de la práctica a finalizar
     * @param dto datos de finalización (estado, fecha fin, observaciones)
     * @return la práctica finalizada
     * @throws IllegalStateException si la práctica no está EN_CURSO
     */
    @Transactional
    public Practica finalizar(UUID id, FinalizarPracticaDTO dto) {
        log.info("Finalizando práctica ID: {}", id);
        Practica p = findById(id);

        if (!"EN_CURSO".equals(p.getEstado())) {
            throw new IllegalStateException("Solo se puede finalizar una práctica que esté EN_CURSO");
        }
        if (!"COMPLETADA".equals(dto.estado()) && !"REPROBADA".equals(dto.estado())) {
            throw new IllegalArgumentException("El estado final debe ser COMPLETADA o REPROBADA");
        }

        p.setFechaFin(dto.fechaFin());
        p.setEstado(dto.estado());
        p.setObservaciones(dto.observaciones());

        Practica finalizada = repository.save(p);
        log.info("Práctica ID: {} finalizada con estado: {}", id, finalizada.getEstado());
        return finalizada;
    }

    /**
     * Anula lógicamente una práctica. No permite anular prácticas ya completadas.
     *
     * @param id identificador de la práctica a anular
     * @throws IllegalStateException si la práctica ya está completada
     */
    @Transactional
    public void delete(UUID id) {
        log.info("Anulando práctica ID: {}", id);
        Practica p = findById(id);

        if ("COMPLETADA".equals(p.getEstado())) {
            throw new IllegalStateException("No se puede anular una práctica ya completada");
        }

        p.setEstado("ANULADA");
        repository.save(p);
        log.info("Práctica ID: {} marcada como ANULADA", id);
    }

    private String construirMensajeBloqueo(boolean creditosOk, boolean arancelOk, boolean empresaOk) {
        StringBuilder sb = new StringBuilder("No se puede inscribir la práctica. Requisitos pendientes: ");
        if (!creditosOk) sb.append("[El estudiante no tiene el 80% de créditos aprobados] ");
        if (!arancelOk)  sb.append("[El estudiante tiene deuda de arancel pendiente] ");
        if (!empresaOk)  sb.append("[La empresa no tiene convenio vigente con el instituto] ");
        return sb.toString().trim();
    }
}
