package com.proyecto.ms_aranceles.service;

import com.proyecto.ms_aranceles.repository.ArancelRepository;
import com.proyecto.ms_aranceles.dto.ArancelDTO;
import com.proyecto.ms_aranceles.model.Arancel;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArancelService {

    private final ArancelRepository repository;


    public List<Arancel> findAll() {
        log.info("Listando todos los aranceles");
        return repository.findAll();
    }

    public Arancel findById(UUID id) {
        log.info("Buscando arancel por ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Arancel no encontrado con ID: " + id));
    }

    public List<Arancel> findByEstudiante(UUID estudianteId) {
        log.info("Listando aranceles del estudiante {}", estudianteId);
        return repository.findByEstudianteId(estudianteId);
    }

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

    @Transactional
    public Arancel update(UUID id, ArancelDTO dto) {
        log.info("Actualizando arancel ID: {}", id);
        Arancel a = findById(id);

        // no edito si ya está pagado
        if ("PAGADO".equals(a.getEstado())) {
            throw new IllegalStateException("No se puede modificar un arancel que ya fue pagado");
        }

        a.setConcepto(dto.concepto());
        a.setMonto(dto.monto());
        a.setFechaVencimiento(dto.fechaVencimiento());
        return repository.save(a);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Eliminando (lógica) arancel ID: {}", id);
        Arancel a = findById(id);
        a.setEstado("ANULADO");
        repository.save(a);
        log.info("Arancel marcado como ANULADO, ID: {}", id);
    }


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


    // R4: tiene deuda si lleva más de 45 días sin pagar
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

    // R5: puede hacer práctica solo si no tiene ningún arancel pendiente
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
