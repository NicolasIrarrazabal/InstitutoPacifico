package com.proyecto.ms_matriculas.service;

import com.proyecto.ms_matriculas.client.AsignaturaClientService;
import com.proyecto.ms_matriculas.client.NotaClientService;
import com.proyecto.ms_matriculas.client.PrerequisitosResponse;
import com.proyecto.ms_matriculas.client.PrerequisitosResponse.AsignaturaRef;
import com.proyecto.ms_matriculas.dto.MatriculaDTO;
import com.proyecto.ms_matriculas.model.Matricula;
import com.proyecto.ms_matriculas.repository.MatriculaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatriculaServiceTest {

    @Mock
    private MatriculaRepository repository;

    @Mock
    private AsignaturaClientService asignaturaClient;

    @Mock
    private NotaClientService notaClient;

    @InjectMocks
    private MatriculaService service;

    // ── findAll ──────────────────────────────────────────────────────

    @Test
    void findAll_retornaListaMatriculas() {
        when(repository.findAll()).thenReturn(List.of(matricula(UUID.randomUUID(), "ACTIVA")));

        List<Matricula> resultado = service.findAll();

        assertThat(resultado).hasSize(1);
        verify(repository).findAll();
    }

    // ── findById ─────────────────────────────────────────────────────

    @Test
    void findById_cuandoExiste_retornaMatricula() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(matricula(id, "ACTIVA")));

        Matricula resultado = service.findById(id);

        assertThat(resultado.getId()).isEqualTo(id);
    }

    @Test
    void findById_cuandoNoExiste_lanzaEntityNotFoundException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    // ── findByEstudiante ─────────────────────────────────────────────

    @Test
    void findByEstudiante_retornaMatriculasActivas() {
        UUID estudianteId = UUID.randomUUID();
        when(repository.findByEstudianteIdAndEstado(estudianteId, "ACTIVA"))
                .thenReturn(List.of(matricula(UUID.randomUUID(), "ACTIVA")));

        List<Matricula> resultado = service.findByEstudiante(estudianteId);

        assertThat(resultado).hasSize(1);
        verify(repository).findByEstudianteIdAndEstado(estudianteId, "ACTIVA");
    }

    // ── create ───────────────────────────────────────────────────────

    @Test
    void create_caminoFeliz_sinPrerequisitos_guardaMatricula() {
        UUID estudianteId = UUID.randomUUID();
        UUID seccionId = UUID.randomUUID();
        MatriculaDTO dto = new MatriculaDTO(estudianteId, seccionId, LocalDate.now(), "ACTIVA");
        Matricula guardada = matricula(UUID.randomUUID(), "ACTIVA");

        when(repository.existsByEstudianteIdAndSeccionIdAndEstado(estudianteId, seccionId, "ACTIVA"))
                .thenReturn(false);
        when(asignaturaClient.obtenerPrerequisitos(seccionId)).thenReturn(List.of());
        when(repository.save(any(Matricula.class))).thenReturn(guardada);

        Matricula resultado = service.create(dto);

        assertThat(resultado.getEstado()).isEqualTo("ACTIVA");
        verify(repository).save(any(Matricula.class));
    }

    @Test
    void create_cuandoYaMatriculado_lanzaIllegalStateException() {
        UUID estudianteId = UUID.randomUUID();
        UUID seccionId = UUID.randomUUID();
        MatriculaDTO dto = new MatriculaDTO(estudianteId, seccionId, LocalDate.now(), "ACTIVA");

        when(repository.existsByEstudianteIdAndSeccionIdAndEstado(estudianteId, seccionId, "ACTIVA"))
                .thenReturn(true);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("matriculado");

        verify(repository, never()).save(any());
    }

    @Test
    void create_cuandoPrerequisitoCumplido_guardaMatricula() {
        UUID estudianteId = UUID.randomUUID();
        UUID seccionId = UUID.randomUUID();
        UUID asigPrereq = UUID.randomUUID();
        MatriculaDTO dto = new MatriculaDTO(estudianteId, seccionId, LocalDate.now(), "ACTIVA");

        PrerequisitosResponse prereq = new PrerequisitosResponse(
                UUID.randomUUID(), new AsignaturaRef(asigPrereq, "Cálculo I"));

        when(repository.existsByEstudianteIdAndSeccionIdAndEstado(estudianteId, seccionId, "ACTIVA"))
                .thenReturn(false);
        when(asignaturaClient.obtenerPrerequisitos(seccionId)).thenReturn(List.of(prereq));
        when(notaClient.estudianteAproboAsignatura(estudianteId, asigPrereq)).thenReturn(true);
        when(repository.save(any(Matricula.class))).thenReturn(matricula(UUID.randomUUID(), "ACTIVA"));

        Matricula resultado = service.create(dto);

        assertThat(resultado).isNotNull();
        verify(repository).save(any(Matricula.class));
    }

    @Test
    void create_cuandoPrerequisitoPendiente_lanzaIllegalStateException() {
        UUID estudianteId = UUID.randomUUID();
        UUID seccionId = UUID.randomUUID();
        UUID asigPrereq = UUID.randomUUID();
        MatriculaDTO dto = new MatriculaDTO(estudianteId, seccionId, LocalDate.now(), "ACTIVA");

        PrerequisitosResponse prereq = new PrerequisitosResponse(
                UUID.randomUUID(), new AsignaturaRef(asigPrereq, "Cálculo I"));

        when(repository.existsByEstudianteIdAndSeccionIdAndEstado(estudianteId, seccionId, "ACTIVA"))
                .thenReturn(false);
        when(asignaturaClient.obtenerPrerequisitos(seccionId)).thenReturn(List.of(prereq));
        when(notaClient.estudianteAproboAsignatura(estudianteId, asigPrereq)).thenReturn(false);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("prerrequisitos");

        verify(repository, never()).save(any());
    }

    @Test
    void create_cuandoClienteAsignaturaFalla_lanzaIllegalStateException() {
        UUID estudianteId = UUID.randomUUID();
        UUID seccionId = UUID.randomUUID();
        MatriculaDTO dto = new MatriculaDTO(estudianteId, seccionId, LocalDate.now(), "ACTIVA");

        when(repository.existsByEstudianteIdAndSeccionIdAndEstado(estudianteId, seccionId, "ACTIVA"))
                .thenReturn(false);
        when(asignaturaClient.obtenerPrerequisitos(seccionId))
                .thenThrow(new RuntimeException("ms-asignaturas caído"));

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(IllegalStateException.class);
    }

    // ── update ───────────────────────────────────────────────────────

    @Test
    void update_cuandoActiva_actualizaEstado() {
        UUID id = UUID.randomUUID();
        Matricula existente = matricula(id, "ACTIVA");
        MatriculaDTO dto = new MatriculaDTO(UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), "INACTIVA");

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Matricula.class))).thenReturn(existente);

        Matricula resultado = service.update(id, dto);

        assertThat(resultado).isNotNull();
        verify(repository).save(existente);
    }

    @Test
    void update_cuandoInactiva_lanzaIllegalStateException() {
        UUID id = UUID.randomUUID();
        Matricula existente = matricula(id, "INACTIVA");
        MatriculaDTO dto = new MatriculaDTO(UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), "ACTIVA");

        when(repository.findById(id)).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> service.update(id, dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("inactiva");

        verify(repository, never()).save(any());
    }

    // ── delete ───────────────────────────────────────────────────────

    @Test
    void delete_marcaMatriculaComoInactiva() {
        UUID id = UUID.randomUUID();
        Matricula existente = matricula(id, "ACTIVA");

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Matricula.class))).thenReturn(existente);

        service.delete(id);

        assertThat(existente.getEstado()).isEqualTo("INACTIVA");
        verify(repository).save(existente);
    }

    // ── helper ───────────────────────────────────────────────────────

    private Matricula matricula(UUID id, String estado) {
        Matricula m = new Matricula();
        m.setId(id);
        m.setEstudianteId(UUID.randomUUID());
        m.setSeccionId(UUID.randomUUID());
        m.setFechaMatricula(LocalDate.now());
        m.setEstado(estado);
        return m;
    }
}
