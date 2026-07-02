package com.proyecto.ms_empresas.service;

import com.proyecto.ms_empresas.dto.EmpresaDTO;
import com.proyecto.ms_empresas.model.Empresa;
import com.proyecto.ms_empresas.repository.EmpresaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Servicio que gestiona la lógica de negocio para empresas y convenios.
 * Administra el CRUD de empresas con validación de RUT único
 * y verificación de convenios vigentes para la regla R5.
 */
@Service
@RequiredArgsConstructor
public class EmpresaService {

    private static final Logger log = LoggerFactory.getLogger(EmpresaService.class);

    private final EmpresaRepository repository;

    /**
     * Obtiene todas las empresas registradas.
     *
     * @return lista de empresas, vacía si no hay registros
     */
    public List<Empresa> findAll() {
        return repository.findAll();
    }

    /**
     * Busca una empresa por su ID.
     *
     * @param id identificador único de la empresa
     * @return la empresa encontrada
     * @throws jakarta.persistence.EntityNotFoundException si no existe con ese ID
     */
    public Empresa findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada con ID: " + id));
    }

    /**
     * Crea una nueva empresa con convenio validando que el RUT sea único.
     *
     * @param dto datos de la empresa (nombre, RUT, rubro, contacto, fechas de convenio)
     * @return la empresa creada con su ID asignado
     * @throws IllegalArgumentException si ya existe una empresa con ese RUT
     */
    public Empresa save(EmpresaDTO dto) {
        if (repository.existsByRut(dto.rut())) {
            throw new IllegalArgumentException("Ya existe una empresa registrada con el RUT: " + dto.rut());
        }

        Empresa empresa = new Empresa();
        empresa.setNombre(dto.nombre());
        empresa.setRut(dto.rut());
        empresa.setRubro(dto.rubro());
        empresa.setDireccion(dto.direccion());
        empresa.setTelefono(dto.telefono());
        empresa.setEmailContacto(dto.emailContacto());
        empresa.setNombreContacto(dto.nombreContacto());
        empresa.setFechaInicioConvenio(dto.fechaInicioConvenio());
        empresa.setFechaFinConvenio(dto.fechaFinConvenio());
        empresa.setEstado("ACTIVO");

        Empresa guardada = repository.save(empresa);
        log.info("Empresa creada con ID {} y RUT {}", guardada.getId(), guardada.getRut());
        return guardada;
    }

    /**
     * Actualiza los datos de una empresa existente.
     * No permite modificar empresas inactivas.
     *
     * @param id  identificador de la empresa a actualizar
     * @param dto datos actualizados de la empresa
     * @return la empresa actualizada
     * @throws IllegalStateException si la empresa está inactiva
     */
    public Empresa update(UUID id, EmpresaDTO dto) {
        Empresa empresa = findById(id);

        if ("INACTIVO".equals(empresa.getEstado())) {
            throw new IllegalStateException("No se puede modificar una empresa en estado INACTIVO");
        }

        if (!empresa.getRut().equals(dto.rut()) && repository.existsByRut(dto.rut())) {
            throw new IllegalArgumentException("El RUT ya está en uso por otra empresa");
        }

        empresa.setNombre(dto.nombre());
        empresa.setRut(dto.rut());
        empresa.setRubro(dto.rubro());
        empresa.setDireccion(dto.direccion());
        empresa.setTelefono(dto.telefono());
        empresa.setEmailContacto(dto.emailContacto());
        empresa.setNombreContacto(dto.nombreContacto());
        empresa.setFechaInicioConvenio(dto.fechaInicioConvenio());
        empresa.setFechaFinConvenio(dto.fechaFinConvenio());

        Empresa actualizada = repository.save(empresa);
        log.info("Empresa actualizada con ID {}", id);
        return actualizada;
    }

    /**
     * Desactiva lógicamente una empresa cambiando su estado a INACTIVO.
     *
     * @param id identificador de la empresa a desactivar
     * @throws IllegalStateException si la empresa ya está inactiva
     */
    public void delete(UUID id) {
        Empresa empresa = findById(id);

        if ("INACTIVO".equals(empresa.getEstado())) {
            throw new IllegalStateException("La empresa ya se encuentra en estado INACTIVO");
        }

        empresa.setEstado("INACTIVO");
        repository.save(empresa);
        log.info("Empresa marcada como INACTIVO, ID {}", id);
    }

    /**
     * Verifica si la empresa tiene un convenio vigente según sus fechas registradas (regla R5).
     * El convenio es vigente si la empresa está ACTIVA y la fecha actual
     * está dentro del rango de inicio y fin del convenio.
     *
     * @param id identificador de la empresa
     * @return true si la empresa tiene un convenio vigente
     */
    public boolean tieneConvenioVigente(UUID id) {
        Empresa empresa = findById(id);

        if (!"ACTIVO".equals(empresa.getEstado())) {
            log.info("Empresa {} está INACTIVA, convenio no vigente", id);
            return false;
        }

        if (empresa.getFechaInicioConvenio() == null || empresa.getFechaFinConvenio() == null) {
            log.info("Empresa {} no tiene fechas de convenio registradas", id);
            return false;
        }

        LocalDate hoy = LocalDate.now();
        boolean vigente = !hoy.isBefore(empresa.getFechaInicioConvenio())
                && !hoy.isAfter(empresa.getFechaFinConvenio());

        log.info("Convenio de empresa {} — inicio: {} fin: {} hoy: {} vigente: {}",
                id, empresa.getFechaInicioConvenio(), empresa.getFechaFinConvenio(), hoy, vigente);

        return vigente;
    }
}
