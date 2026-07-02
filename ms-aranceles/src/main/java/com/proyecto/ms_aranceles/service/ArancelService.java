package com.proyecto.ms_aranceles.service;

import com.proyecto.ms_aranceles.repository.ArancelRepository;
import com.proyecto.ms_aranceles.dto.ArancelDTO;
import com.proyecto.ms_aranceles.model.Arancel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Servicio que gestiona la lógica de negocio para aranceles, pagos y deudas.
 * Implementa la validación de la regla R4 (deuda vencida > 45 días)
 * y controla el ciclo de vida de los aranceles (pendiente, pagado, anulado).
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ArancelService {

    private final ArancelRepository repository;

    /**
     * Obtiene todos los aranceles registrados en el sistema.
     *
     * @return lista de aranceles, vacía si no hay registros
     */
    public List<Arancel> findAll() {
        log.info("Listando todos los aranceles");
        return repository.findAll();
    }

    /**
     * Busca un arancel por su ID.
     *
     * @param id identificador único del arancel
     * @return el arancel encontrado
     * @throws jakarta.persistence.EntityNotFoundException si no existe un arancel con ese ID
     */
    public Arancel findById(UUID id) {
        log.info("Buscando arancel por ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Arancel no encontrado con ID: " + id));
    }

    /**
     * Obtiene todos los aranceles asociados a un estudiante.
     *
     * @param estudianteId identificador del estudiante
     * @return lista de aranceles del estudiante, vacía si no tiene
     */
    public List<Arancel> findByEstudiante(UUID estudianteId) {
        log.info("Listando aranceles del estudiante {}", estudianteId);
        return repository.findByEstudianteId(estudianteId);
    }

    /**
     * Crea un nuevo arancel para un estudiante con estado PENDIENTE.
     *
     * @param dto datos del arancel a crear (estudiante, concepto, monto, fechas)
     * @return el arancel creado con su ID asignado
     */
    @Transactional
    public Arancel create(ArancelDTO dto) {
        log.info("Creando arancel para estudiante {} — concepto: {}", dto.estudianteId(), dto.concepto());

        Arancel a = new Arancel();
        a.setEstudianteId(dto.estudianteId());
        a.setConcepto(dto.concepto());
        a.setMonto(dto.monto());
        a.setFechaEmision(dto.fechaEmision());
        a.setFechaVencimiento(dto.fechaVencimiento());
        a.setEstado("PENDIENTE");

        Arancel guardado = repository.save(a);
        log.info("Arancel creado con ID: {}", guardado.getId());
        return guardado;
    }

    /**
     * Actualiza los datos de un arancel existente.
     * No permite modificar aranceles que ya fueron pagados.
     *
     * @param id  identificador del arancel a actualizar
     * @param dto datos actualizados del arancel
     * @return el arancel actualizado
     * @throws IllegalStateException si el arancel ya está pagado
     */
    @Transactional
    public Arancel update(UUID id, ArancelDTO dto) {
        log.info("Actualizando arancel ID: {}", id);
        Arancel a = findById(id);

        if ("PAGADO".equals(a.getEstado())) {
            throw new IllegalStateException("No se puede modificar un arancel que ya fue pagado");
        }

        a.setConcepto(dto.concepto());
        a.setMonto(dto.monto());
        a.setFechaVencimiento(dto.fechaVencimiento());
        return repository.save(a);
    }

    /**
     * Anula lógicamente un arancel cambiando su estado a ANULADO.
     *
     * @param id identificador del arancel a anular
     */
    @Transactional
    public void delete(UUID id) {
        log.info("Eliminando (lógica) arancel ID: {}", id);
        Arancel a = findById(id);
        a.setEstado("ANULADO");
        repository.save(a);
        log.info("Arancel marcado como ANULADO, ID: {}", id);
    }

    /**
     * Registra el pago de un arancel cambiando su estado a PAGADO.
     * Valida que el arancel no esté ya pagado o anulado.
     *
     * @param id identificador del arancel a pagar
     * @return el arancel con estado PAGADO y fecha de pago asignada
     * @throws IllegalStateException si el arancel ya fue pagado o está anulado
     */
    @Transactional
    public Arancel registrarPago(UUID id) {
        log.info("Registrando pago del arancel ID: {}", id);
        Arancel a = findById(id);

        if ("PAGADO".equals(a.getEstado())) {
            throw new IllegalStateException("El arancel ya fue pagado anteriormente");
        }
        if ("ANULADO".equals(a.getEstado())) {
            throw new IllegalStateException("No se puede pagar un arancel anulado");
        }

        a.setEstado("PAGADO");
        a.setFechaPago(LocalDate.now());
        Arancel pagado = repository.save(a);
        log.info("Pago registrado para arancel ID: {} — fecha de pago: {}", id, pagado.getFechaPago());
        return pagado;
    }

    /**
     * Verifica si el estudiante tiene deuda vencida por más de 45 días (regla R4).
     * Revisa todos los aranceles no pagados cuya fecha de vencimiento
     * supere los 45 días desde la fecha actual.
     *
     * @param estudianteId identificador del estudiante a verificar
     * @return true si el estudiante tiene al menos una deuda vencida > 45 días
     */
    public boolean tieneDeudaVencida(UUID estudianteId) {
        log.info("Verificando deuda vencida (>45 días) del estudiante {}", estudianteId);

        LocalDate fechaLimite = LocalDate.now().minusDays(45);

        List<Arancel> pendientes = repository.findByEstudianteIdAndEstadoNot(estudianteId, "PAGADO");

        boolean tieneDeuda = pendientes.stream()
                .filter(a -> !"ANULADO".equals(a.getEstado()))
                .anyMatch(a -> a.getFechaVencimiento().isBefore(fechaLimite));

        log.info("Estudiante {} tiene deuda vencida >45 días: {}", estudianteId, tieneDeuda);
        return tieneDeuda;
    }

    /**
     * Verifica si el estudiante puede continuar según su situación de arancel.
     * No debe tener aranceles pendientes (no pagados ni anulados).
     * Utilizado por la regla R5 para permitir prácticas profesionales.
     *
     * @param estudianteId identificador del estudiante a verificar
     * @return true si el estudiante no tiene aranceles pendientes
     */
    public boolean puedeContinuar(UUID estudianteId) {
        log.info("Verificando si estudiante {} puede continuar con R5", estudianteId);

        List<Arancel> pendientes = repository.findByEstudianteIdAndEstadoNot(estudianteId, "PAGADO");

        boolean tienePendientes = pendientes.stream()
                .anyMatch(a -> !"ANULADO".equals(a.getEstado()));

        boolean puede = !tienePendientes;
        log.info("Estudiante {} puede continuar con R5: {}", estudianteId, puede);
        return puede;
    }
}
