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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EstudianteService {

    private static final Logger log = LoggerFactory.getLogger(EstudianteService.class);

    private final EstudianteRepository repository;
    private final NotaClientService notaClient;
    private final MatriculaClientService matriculaClient;

    public List<Estudiante> findAll() {
        return repository.findAll();
    }

    public Estudiante findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado con ID: " + id));
    }

    public Estudiante findByRut(String rut) {
        return repository.findByRut(rut)
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado con RUT: " + rut));
    }

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

    public void delete(UUID id) {
        Estudiante est = findById(id);

        if (EstadoEstudiante.INACTIVO.equals(est.getEstado())) {
            throw new IllegalStateException("El estudiante ya se encuentra en estado INACTIVO");
        }

        est.setEstado(EstadoEstudiante.INACTIVO);
        repository.save(est);
        log.info("Estudiante marcado como INACTIVO, ID {}", id);
    }

    public boolean puedeMatricular(UUID estudianteId) {
        Estudiante est = findById(estudianteId);
        return EstadoEstudiante.ACTIVO.equals(est.getEstado());
    }

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
