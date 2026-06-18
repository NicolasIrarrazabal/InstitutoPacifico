package com.proyecto.ms_aranceles.service;

import com.proyecto.ms_aranceles.dto.ArancelDTO;
import com.proyecto.ms_aranceles.model.Arancel;
import com.proyecto.ms_aranceles.repository.ArancelRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArancelServiceTest {

    @Mock
    private ArancelRepository repository;

    @InjectMocks
    private ArancelService service;

    // ── findAll ──────────────────────────────────────────────────────

    @Test
    void findAll_debeRetornarListaDeAranceles() {
        // Arrange
        Arancel a = arancel(UUID.randomUUID(), "PENDIENTE");
        when(repository.findAll()).thenReturn(List.of(a));

        // Act
        List<Arancel> resultado = service.findAll();

        // Assert
        assertThat(resultado).hasSize(1);
        verify(repository, times(1)).findAll();
    }

    // ── findById ─────────────────────────────────────────────────────

    @Test
    void findById_cuandoExiste_retornaArancel() {
        UUID id = UUID.randomUUID();
        Arancel a = arancel(id, "PENDIENTE");
        when(repository.findById(id)).thenReturn(Optional.of(a));

        Arancel resultado = service.findById(id);

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

    // ── create ───────────────────────────────────────────────────────

    @Test
    void create_debeGuardarArancelConEstadoPendiente() {
        // Arrange
        UUID estudianteId = UUID.randomUUID();
        ArancelDTO dto = new ArancelDTO(
                estudianteId, "Matrícula 2024",
                new BigDecimal("150000"),
                LocalDate.now(), LocalDate.now().plusMonths(1)
        );
        Arancel guardado = arancel(UUID.randomUUID(), "PENDIENTE");
        guardado.setEstudianteId(estudianteId);
        when(repository.save(any(Arancel.class))).thenReturn(guardado);

        // Act
        Arancel resultado = service.create(dto);

        // Assert
        assertThat(resultado.getEstado()).isEqualTo("PENDIENTE");
        verify(repository).save(any(Arancel.class));
    }

    // ── update ───────────────────────────────────────────────────────

    @Test
    void update_cuandoNoPagado_actualizaYGuarda() {
        UUID id = UUID.randomUUID();
        Arancel existente = arancel(id, "PENDIENTE");
        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Arancel.class))).thenReturn(existente);

        ArancelDTO dto = new ArancelDTO(
                UUID.randomUUID(), "Nuevo concepto",
                new BigDecimal("200000"),
                LocalDate.now(), LocalDate.now().plusMonths(2)
        );

        Arancel resultado = service.update(id, dto);

        assertThat(resultado).isNotNull();
        verify(repository).save(existente);
    }

    @Test
    void update_cuandoPagado_lanzaIllegalStateException() {
        UUID id = UUID.randomUUID();
        Arancel existente = arancel(id, "PAGADO");
        when(repository.findById(id)).thenReturn(Optional.of(existente));

        ArancelDTO dto = new ArancelDTO(
                UUID.randomUUID(), "Concepto",
                new BigDecimal("100000"),
                LocalDate.now(), LocalDate.now().plusMonths(1)
        );

        assertThatThrownBy(() -> service.update(id, dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("pagado");

        verify(repository, never()).save(any());
    }

    // ── delete ───────────────────────────────────────────────────────

    @Test
    void delete_marcaArancelComoAnulado() {
        UUID id = UUID.randomUUID();
        Arancel existente = arancel(id, "PENDIENTE");
        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Arancel.class))).thenReturn(existente);

        service.delete(id);

        assertThat(existente.getEstado()).isEqualTo("ANULADO");
        verify(repository).save(existente);
    }

    // ── registrarPago ─────────────────────────────────────────────────

    @Test
    void registrarPago_cuandoPendiente_marcaPagado() {
        UUID id = UUID.randomUUID();
        Arancel existente = arancel(id, "PENDIENTE");
        Arancel pagado = arancel(id, "PAGADO");
        pagado.setFechaPago(LocalDate.now());
        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Arancel.class))).thenReturn(pagado);

        Arancel resultado = service.registrarPago(id);

        assertThat(resultado.getEstado()).isEqualTo("PAGADO");
        verify(repository).save(existente);
    }

    @Test
    void registrarPago_cuandoYaPagado_lanzaIllegalStateException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(arancel(id, "PAGADO")));

        assertThatThrownBy(() -> service.registrarPago(id))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("pagado");

        verify(repository, never()).save(any());
    }

    @Test
    void registrarPago_cuandoAnulado_lanzaIllegalStateException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(arancel(id, "ANULADO")));

        assertThatThrownBy(() -> service.registrarPago(id))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("anulado");

        verify(repository, never()).save(any());
    }

    // ── tieneDeudaVencida (R4) ───────────────────────────────────────

    @Test
    void tieneDeudaVencida_cuandoHayArancelVencidoMas45Dias_retornaTrue() {
        UUID estudianteId = UUID.randomUUID();
        Arancel vencido = arancel(UUID.randomUUID(), "PENDIENTE");
        vencido.setEstudianteId(estudianteId);
        vencido.setFechaVencimiento(LocalDate.now().minusDays(50)); // > 45 días
        when(repository.findByEstudianteIdAndEstadoNot(estudianteId, "PAGADO"))
                .thenReturn(List.of(vencido));

        boolean resultado = service.tieneDeudaVencida(estudianteId);

        assertThat(resultado).isTrue();
    }

    @Test
    void tieneDeudaVencida_cuandoSinDeudaVencida_retornaFalse() {
        UUID estudianteId = UUID.randomUUID();
        Arancel reciente = arancel(UUID.randomUUID(), "PENDIENTE");
        reciente.setFechaVencimiento(LocalDate.now().minusDays(10)); // < 45 días
        when(repository.findByEstudianteIdAndEstadoNot(estudianteId, "PAGADO"))
                .thenReturn(List.of(reciente));

        boolean resultado = service.tieneDeudaVencida(estudianteId);

        assertThat(resultado).isFalse();
    }

    @Test
    void tieneDeudaVencida_cuandoArancelAnulado_seIgnora() {
        UUID estudianteId = UUID.randomUUID();
        Arancel anulado = arancel(UUID.randomUUID(), "ANULADO");
        anulado.setFechaVencimiento(LocalDate.now().minusDays(60));
        when(repository.findByEstudianteIdAndEstadoNot(estudianteId, "PAGADO"))
                .thenReturn(List.of(anulado));

        boolean resultado = service.tieneDeudaVencida(estudianteId);

        assertThat(resultado).isFalse();
    }

    // ── puedeContinuar (R5) ──────────────────────────────────────────

    @Test
    void puedeContinuar_cuandoSinPendientes_retornaTrue() {
        UUID estudianteId = UUID.randomUUID();
        when(repository.findByEstudianteIdAndEstadoNot(estudianteId, "PAGADO"))
                .thenReturn(List.of());

        boolean resultado = service.puedeContinuar(estudianteId);

        assertThat(resultado).isTrue();
    }

    @Test
    void puedeContinuar_cuandoHayPendiente_retornaFalse() {
        UUID estudianteId = UUID.randomUUID();
        Arancel pendiente = arancel(UUID.randomUUID(), "PENDIENTE");
        when(repository.findByEstudianteIdAndEstadoNot(estudianteId, "PAGADO"))
                .thenReturn(List.of(pendiente));

        boolean resultado = service.puedeContinuar(estudianteId);

        assertThat(resultado).isFalse();
    }

    @Test
    void puedeContinuar_cuandoSoloAnulados_retornaTrue() {
        UUID estudianteId = UUID.randomUUID();
        Arancel anulado = arancel(UUID.randomUUID(), "ANULADO");
        when(repository.findByEstudianteIdAndEstadoNot(estudianteId, "PAGADO"))
                .thenReturn(List.of(anulado));

        boolean resultado = service.puedeContinuar(estudianteId);

        assertThat(resultado).isTrue();
    }

    // ── helpers ──────────────────────────────────────────────────────

    private Arancel arancel(UUID id, String estado) {
        Arancel a = new Arancel();
        a.setId(id);
        a.setEstudianteId(UUID.randomUUID());
        a.setConcepto("Arancel de prueba");
        a.setMonto(new BigDecimal("100000"));
        a.setFechaEmision(LocalDate.now().minusDays(30));
        a.setFechaVencimiento(LocalDate.now().plusDays(30));
        a.setEstado(estado);
        return a;
    }
}
