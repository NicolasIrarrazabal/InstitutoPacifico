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

@Tag(name = "Practica Service", description = "Lógica de negocio para prácticas profesionales y validación de regla R5")
@Service
@Slf4j
@RequiredArgsConstructor
public class PracticaService {

    private final PracticaRepository repository;

    private final ArancelClientService arancelClient;
    private final CreditoClientService creditoClient;
    private final EmpresaClientService empresaClient;

    @Operation(summary = "Listar todas las prácticas", description = "Retorna todas las prácticas registradas")
    public List<Practica> findAll() {
        log.info("Listando todas las prácticas");
        return repository.findAll();
    }

    @Operation(summary = "Buscar práctica por ID", description = "Retorna una práctica por su ID")
    public Practica findById(UUID id) {
        log.info("Buscando práctica por ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Práctica no encontrada con ID: " + id));
    }

    @Operation(summary = "Listar prácticas por estudiante", description = "Retorna todas las prácticas de un estudiante")
    public List<Practica> findByEstudiante(UUID estudianteId) {
        log.info("Listando prácticas del estudiante {}", estudianteId);
        return repository.findByEstudianteId(estudianteId);
    }

    @Operation(summary = "Verificar requisitos R5", description = "Verifica si el estudiante cumple los 3 requisitos para inscribir práctica: créditos, arancel y convenio")
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

    @Operation(summary = "Inscribir práctica", description = "Inscribe una nueva práctica profesional validando la regla R5")
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

    @Operation(summary = "Finalizar práctica", description = "Finaliza una práctica con el estado y observaciones correspondientes")
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

    @Operation(summary = "Anular práctica", description = "Anula lógicamente una práctica")
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
