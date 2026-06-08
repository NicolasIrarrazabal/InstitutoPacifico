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

@Tag(name = "Empresa Service", description = "Lógica de negocio para gestión de empresas y convenios")
@Service
@RequiredArgsConstructor
public class EmpresaService {

    private static final Logger log = LoggerFactory.getLogger(EmpresaService.class);

    private final EmpresaRepository repository;

    @Operation(summary = "Listar todas las empresas", description = "Retorna todas las empresas registradas")
    public List<Empresa> findAll() {
        return repository.findAll();
    }

    @Operation(summary = "Buscar empresa por ID", description = "Retorna una empresa por su ID")
    public Empresa findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada con ID: " + id));
    }

    @Operation(summary = "Guardar empresa", description = "Registra una nueva empresa con convenio validando RUT único")
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

    @Operation(summary = "Actualizar empresa", description = "Actualiza los datos de una empresa existente")
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

    @Operation(summary = "Desactivar empresa", description = "Desactiva lógicamente una empresa")
    public void delete(UUID id) {
        Empresa empresa = findById(id);

        if ("INACTIVO".equals(empresa.getEstado())) {
            throw new IllegalStateException("La empresa ya se encuentra en estado INACTIVO");
        }

        empresa.setEstado("INACTIVO");
        repository.save(empresa);
        log.info("Empresa marcada como INACTIVO, ID {}", id);
    }

    @Operation(summary = "Verificar convenio vigente", description = "Verifica si la empresa tiene un convenio vigente según las fechas registradas (R5)")
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
