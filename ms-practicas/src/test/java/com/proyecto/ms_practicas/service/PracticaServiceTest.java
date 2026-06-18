package com.proyecto.ms_practicas.service;

import com.proyecto.ms_practicas.client.ArancelClientService;
import com.proyecto.ms_practicas.client.CreditoClientService;
import com.proyecto.ms_practicas.client.EmpresaClientService;
import com.proyecto.ms_practicas.dto.FinalizarPracticaDTO;
import com.proyecto.ms_practicas.dto.PracticaDTO;
import com.proyecto.ms_practicas.dto.ValidacionR5Response;
import com.proyecto.ms_practicas.model.Practica;
import com.proyecto.ms_practicas.repository.PracticaRepository;
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
class PracticaServiceTest {

    @Mock
    private PracticaRepository repository;

    @Mock
    private ArancelClientService arancelClient;

    @Mock
    private CreditoClientService creditoClient;

    @Mock
    private EmpresaClientService empresaClient;

    @InjectMocks
    private PracticaService service;

    // ── findAll ──────────────────────────────────────────────────────

    @Test
    void findAll_retornaListaPracticas() {
        when(repository.findAll()).thenReturn(List.of(practica(UUID.randomUUID(), "EN_CURSO")));

        List<Practica> resultado = service.findAll();

        assertThat(resultado).hasSize(1);
        verify(repository).findAll();
    }

    // ── findById ─────────────────────────────────────────────────────

    @Test
    void findById_cuandoExiste_retornaPractica() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(practica(id, "EN_CURSO")));

        Practica resultado = service.findById(id);

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
    void findByEstudiante_delegaAlRepositorio() {
        UUID estudianteId = UUID.randomUUID();
        when(repository.findByEstudianteId(estudianteId)).thenReturn(List.of());

        service.findByEstudiante(estudianteId);

        verify(repository).findByEstudianteId(estudianteId);
    }

    // ── verificarRequisitosR5 ────────────────────────────────────────

    @Test
    void verificarRequisitosR5_cuandoTodosCumplidos_retornaPuedeInscribir() {
        UUID estudianteId = UUID.randomUUID();
        UUID empresaId = UUID.randomUUID();

        when(creditoClient.estudianteTieneCreditosSuficientes(estudianteId)).thenReturn(true);
        when(arancelClient.estudianteEstaAlDia(estudianteId)).thenReturn(true);
        when(empresaClient.empresaTieneConvenioVigente(empresaId)).thenReturn(true);

        ValidacionR5Response resultado = service.verificarRequisitosR5(estudianteId, empresaId);

        assertThat(resultado.puedeInscribir()).isTrue();
        assertThat(resultado.creditosAprobados()).isTrue();
        assertThat(resultado.arancelAlDia()).isTrue();
        assertThat(resultado.empresaConConvenio()).isTrue();
    }

    @Test
    void verificarRequisitosR5_cuandoSinCreditos_noPuedeInscribir() {
        UUID estudianteId = UUID.randomUUID();
        UUID empresaId = UUID.randomUUID();

        when(creditoClient.estudianteTieneCreditosSuficientes(estudianteId)).thenReturn(false);
        when(arancelClient.estudianteEstaAlDia(estudianteId)).thenReturn(true);
        when(empresaClient.empresaTieneConvenioVigente(empresaId)).thenReturn(true);

        ValidacionR5Response resultado = service.verificarRequisitosR5(estudianteId, empresaId);

        assertThat(resultado.puedeInscribir()).isFalse();
        assertThat(resultado.creditosAprobados()).isFalse();
        assertThat(resultado.mensaje()).contains("créditos");
    }

    @Test
    void verificarRequisitosR5_cuandoClienteFalla_trataComo_false() {
        UUID estudianteId = UUID.randomUUID();
        UUID empresaId = UUID.randomUUID();

        when(creditoClient.estudianteTieneCreditosSuficientes(estudianteId))
                .thenThrow(new RuntimeException("ms-notas caído"));
        when(arancelClient.estudianteEstaAlDia(estudianteId)).thenReturn(true);
        when(empresaClient.empresaTieneConvenioVigente(empresaId)).thenReturn(true);

        ValidacionR5Response resultado = service.verificarRequisitosR5(estudianteId, empresaId);

        // creditosOk = false porque el cliente falló (catch -> stays false)
        assertThat(resultado.puedeInscribir()).isFalse();
        assertThat(resultado.creditosAprobados()).isFalse();
    }

    // ── create ───────────────────────────────────────────────────────

    @Test
    void create_caminoFeliz_todosRequisitos_guardaPractica() {
        UUID estudianteId = UUID.randomUUID();
        UUID empresaId = UUID.randomUUID();
        PracticaDTO dto = new PracticaDTO(estudianteId, empresaId, "Supervisor", LocalDate.now());
        Practica guardada = practica(UUID.randomUUID(), "EN_CURSO");

        when(repository.existsByEstudianteIdAndEstadoIn(estudianteId, List.of("PENDIENTE", "EN_CURSO")))
                .thenReturn(false);
        when(creditoClient.estudianteTieneCreditosSuficientes(estudianteId)).thenReturn(true);
        when(arancelClient.estudianteEstaAlDia(estudianteId)).thenReturn(true);
        when(empresaClient.empresaTieneConvenioVigente(empresaId)).thenReturn(true);
        when(repository.save(any(Practica.class))).thenReturn(guardada);

        Practica resultado = service.create(dto);

        assertThat(resultado.getEstado()).isEqualTo("EN_CURSO");
        verify(repository).save(any(Practica.class));
    }

    @Test
    void create_cuandoYaTienePracticaActiva_lanzaIllegalStateException() {
        UUID estudianteId = UUID.randomUUID();
        UUID empresaId = UUID.randomUUID();
        PracticaDTO dto = new PracticaDTO(estudianteId, empresaId, "Supervisor", LocalDate.now());

        when(repository.existsByEstudianteIdAndEstadoIn(estudianteId, List.of("PENDIENTE", "EN_CURSO")))
                .thenReturn(true);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("activa");

        verify(repository, never()).save(any());
    }

    @Test
    void create_cuandoNoPassaR5_lanzaIllegalStateException() {
        UUID estudianteId = UUID.randomUUID();
        UUID empresaId = UUID.randomUUID();
        PracticaDTO dto = new PracticaDTO(estudianteId, empresaId, "Supervisor", LocalDate.now());

        when(repository.existsByEstudianteIdAndEstadoIn(estudianteId, List.of("PENDIENTE", "EN_CURSO")))
                .thenReturn(false);
        when(creditoClient.estudianteTieneCreditosSuficientes(estudianteId)).thenReturn(false);
        when(arancelClient.estudianteEstaAlDia(estudianteId)).thenReturn(true);
        when(empresaClient.empresaTieneConvenioVigente(empresaId)).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(IllegalStateException.class);

        verify(repository, never()).save(any());
    }

    // ── finalizar ────────────────────────────────────────────────────

    @Test
    void finalizar_cuandoEnCurso_conEstadoCompletada_finalizaCorrectamente() {
        UUID id = UUID.randomUUID();
        Practica existente = practica(id, "EN_CURSO");
        FinalizarPracticaDTO dto = new FinalizarPracticaDTO(LocalDate.now(), "COMPLETADA", "Buen desempeño");

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Practica.class))).thenReturn(existente);

        Practica resultado = service.finalizar(id, dto);

        assertThat(resultado).isNotNull();
        verify(repository).save(existente);
    }

    @Test
    void finalizar_cuandoEnCurso_conEstadoReprobada_finalizaCorrectamente() {
        UUID id = UUID.randomUUID();
        Practica existente = practica(id, "EN_CURSO");
        FinalizarPracticaDTO dto = new FinalizarPracticaDTO(LocalDate.now(), "REPROBADA", "Abandono");

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Practica.class))).thenReturn(existente);

        Practica resultado = service.finalizar(id, dto);

        assertThat(resultado).isNotNull();
    }

    @Test
    void finalizar_cuandoNoEstaEnCurso_lanzaIllegalStateException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(practica(id, "COMPLETADA")));

        FinalizarPracticaDTO dto = new FinalizarPracticaDTO(LocalDate.now(), "COMPLETADA", null);

        assertThatThrownBy(() -> service.finalizar(id, dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("EN_CURSO");
    }

    @Test
    void finalizar_cuandoEstadoInvalido_lanzaIllegalArgumentException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(practica(id, "EN_CURSO")));

        FinalizarPracticaDTO dto = new FinalizarPracticaDTO(LocalDate.now(), "EN_PROGRESO", null);

        assertThatThrownBy(() -> service.finalizar(id, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("COMPLETADA");
    }

    // ── delete ───────────────────────────────────────────────────────

    @Test
    void delete_cuandoEnCurso_marcaComoAnulada() {
        UUID id = UUID.randomUUID();
        Practica existente = practica(id, "EN_CURSO");

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Practica.class))).thenReturn(existente);

        service.delete(id);

        assertThat(existente.getEstado()).isEqualTo("ANULADA");
        verify(repository).save(existente);
    }

    @Test
    void delete_cuandoCompletada_lanzaIllegalStateException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(practica(id, "COMPLETADA")));

        assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("completada");

        verify(repository, never()).save(any());
    }

    // ── helper ───────────────────────────────────────────────────────

    private Practica practica(UUID id, String estado) {
        Practica p = new Practica();
        p.setId(id);
        p.setEstudianteId(UUID.randomUUID());
        p.setEmpresaId(UUID.randomUUID());
        p.setSupervisorNombre("Supervisor Test");
        p.setFechaInicio(LocalDate.now());
        p.setEstado(estado);
        return p;
    }
}
