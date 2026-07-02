package com.proyecto.ms_estudiante.service;

import com.proyecto.ms_estudiante.client.MatriculaClientService;
import com.proyecto.ms_estudiante.client.MatriculaClientService.MatriculaResponse;
import com.proyecto.ms_estudiante.client.NotaClientService;
import com.proyecto.ms_estudiante.client.NotaClientService.NotaResponse;
import com.proyecto.ms_estudiante.client.NotaClientService.PromedioResponse;
import com.proyecto.ms_estudiante.dto.DetalleEstudianteResponse;
import com.proyecto.ms_estudiante.dto.EstudianteDTO;
import com.proyecto.ms_estudiante.model.Estudiante;
import com.proyecto.ms_estudiante.model.enums.EstadoEstudiante;
import com.proyecto.ms_estudiante.repository.EstudianteRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Servicio que gestiona la lógica de negocio para estudiantes.
 * Administra el CRUD de estudiantes con validación de RUT y email únicos,
 * y se comunica con ms-notas y ms-matriculas para obtener el detalle enriquecido.
 */
@Service
@RequiredArgsConstructor
public class EstudianteService {

    private static final Logger log = LoggerFactory.getLogger(EstudianteService.class);

    private final EstudianteRepository repository;
    private final NotaClientService notaClient;
    private final MatriculaClientService matriculaClient;

    /**
     * Obtiene todos los estudiantes registrados.
     *
     * @return lista de estudiantes, vacía si no hay registros
     */
    public List<Estudiante> findAll() {
        return repository.findAll();
    }

    /**
     * Busca un estudiante por su ID.
     *
     * @param id identificador único del estudiante
     * @return el estudiante encontrado
     * @throws jakarta.persistence.EntityNotFoundException si no existe con ese ID
     */
    public Estudiante findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado con ID: " + id));
    }

    /**
     * Busca un estudiante por su RUT.
     *
     * @param rut RUT del estudiante (formato: 12345678-9)
     * @return el estudiante encontrado
     * @throws jakarta.persistence.EntityNotFoundException si no existe con ese RUT
     */
    public Estudiante findByRut(String rut) {
        return repository.findByRut(rut)
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado con RUT: " + rut));
    }

    /**
     * Crea un nuevo estudiante validando que el RUT y el email sean únicos.
     *
     * @param dto datos del estudiante (nombre, RUT, email, teléfono, dirección)
     * @return el estudiante creado con su ID asignado
     * @throws IllegalArgumentException si el RUT o email ya están registrados
     */
    public Estudiante save(EstudianteDTO dto) {
        if (repository.existsByRut(dto.rut())) {
            throw new IllegalArgumentException("Ya existe un estudiante registrado con el RUT: " + dto.rut());
        }

        if (repository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Ya existe un estudiante registrado con el email: " + dto.email());
        }

        Estudiante est = new Estudiante();
        est.setNombre(dto.nombre());
        est.setRut(dto.rut());
        est.setEmail(dto.email());
        est.setTelefono(dto.telefono());
        est.setDireccion(dto.direccion());
        est.setEstado(EstadoEstudiante.ACTIVO);

        Estudiante guardado = repository.save(est);
        log.info("Estudiante creado correctamente con ID {} y RUT {}", guardado.getId(), guardado.getRut());
        return guardado;
    }

    /**
     * Actualiza los datos de un estudiante existente.
     * No permite modificar estudiantes inactivos.
     *
     * @param id  identificador del estudiante a actualizar
     * @param dto datos actualizados del estudiante
     * @return el estudiante actualizado
     * @throws IllegalStateException si el estudiante está inactivo
     */
    public Estudiante update(UUID id, EstudianteDTO dto) {
        Estudiante est = findById(id);

        if (EstadoEstudiante.INACTIVO.equals(est.getEstado())) {
            throw new IllegalStateException("No se puede modificar un estudiante en estado INACTIVO");
        }

        if (!est.getEmail().equals(dto.email()) && repository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("El email ya esta en uso por otro estudiante");
        }

        est.setNombre(dto.nombre());
        est.setEmail(dto.email());
        est.setTelefono(dto.telefono());
        est.setDireccion(dto.direccion());

        Estudiante actualizado = repository.save(est);
        log.info("Estudiante actualizado correctamente, ID {}", id);
        return actualizado;
    }

    /**
     * Desactiva lógicamente un estudiante cambiando su estado a INACTIVO.
     *
     * @param id identificador del estudiante a desactivar
     * @throws IllegalStateException si el estudiante ya está inactivo
     */
    public void delete(UUID id) {
        Estudiante est = findById(id);

        if (EstadoEstudiante.INACTIVO.equals(est.getEstado())) {
            throw new IllegalStateException("El estudiante ya se encuentra en estado INACTIVO");
        }

        est.setEstado(EstadoEstudiante.INACTIVO);
        repository.save(est);
        log.info("Estudiante marcado como INACTIVO, ID {}", id);
    }

    /**
     * Verifica si el estudiante puede matricularse según su estado actual.
     *
     * @param estudianteId identificador del estudiante
     * @return true si el estudiante está en estado ACTIVO
     */
    public boolean puedeMatricular(UUID estudianteId) {
        Estudiante est = findById(estudianteId);
        return EstadoEstudiante.ACTIVO.equals(est.getEstado());
    }

    /**
     * Obtiene el detalle enriquecido de un estudiante incluyendo
     * sus notas (desde ms-notas) y matrículas activas (desde ms-matriculas).
     *
     * @param id identificador del estudiante
     * @return detalle completo con datos personales, promedios, notas y matrículas
     */
    public DetalleEstudianteResponse obtenerDetalle(UUID id) {
        log.info("Construyendo detalle enriquecido para estudiante {}", id);

        Estudiante est = findById(id);

        List<NotaResponse> notas = notaClient.obtenerNotasEstudiante(id);
        log.info("ms-notas respondió con {} notas para estudiante {}", notas.size(), id);

        PromedioResponse promedio = calcularPromedioDesdeNotas(notas, id);

        List<MatriculaResponse> matriculas = matriculaClient.obtenerMatriculasActivas(id);
        log.info("ms-matriculas respondió con {} matrículas activas para estudiante {}", matriculas.size(), id);

        return new DetalleEstudianteResponse(
                est.getId(),
                est.getNombre(),
                est.getRut(),
                est.getEmail(),
                est.getTelefono(),
                est.getDireccion(),
                est.getEstado() != null ? est.getEstado().name() : null,
                promedio.promedioPonderado(),
                promedio.promedioSimple(),
                promedio.totalNotas(),
                promedio.aprobado(),
                notas,
                matriculas.size(),
                matriculas
        );
    }

    private PromedioResponse calcularPromedioDesdeNotas(List<NotaResponse> notas, UUID estudianteId) {
        if (notas.isEmpty()) {
            return new PromedioResponse(estudianteId, BigDecimal.ZERO, BigDecimal.ZERO, 0, false);
        }

        BigDecimal sumaPonderada = BigDecimal.ZERO;
        BigDecimal sumaSimple = BigDecimal.ZERO;
        BigDecimal sumaPonderaciones = BigDecimal.ZERO;

        for (NotaResponse n : notas) {
            if (n.ponderacion() != null && n.nota() != null) {
                sumaPonderada = sumaPonderada.add(n.nota().multiply(n.ponderacion()));
                sumaPonderaciones = sumaPonderaciones.add(n.ponderacion());
            }
            if (n.nota() != null) {
                sumaSimple = sumaSimple.add(n.nota());
            }
        }

        BigDecimal promedioPonderado = sumaPonderaciones.compareTo(BigDecimal.ZERO) > 0
                ? sumaPonderada.divide(sumaPonderaciones, 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal promedioSimple = sumaSimple.divide(
                BigDecimal.valueOf(notas.size()), 2, java.math.RoundingMode.HALF_UP);

        boolean aprobado = promedioPonderado.compareTo(new BigDecimal("4.0")) >= 0;

        return new PromedioResponse(estudianteId, promedioPonderado, promedioSimple, notas.size(), aprobado);
    }
}
