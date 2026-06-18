package com.proyecto.ms_asignaturas.service;

import com.proyecto.ms_asignaturas.dto.AsignaturaDTO;
import com.proyecto.ms_asignaturas.model.Asignatura;
import com.proyecto.ms_asignaturas.model.Credito;
import com.proyecto.ms_asignaturas.repository.AsignaturasRepository;
import com.proyecto.ms_asignaturas.repository.CreditoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsignaturasServiceTest {

    @Mock
    private AsignaturasRepository asignaturasRepository;

    @Mock
    private CreditoRepository creditoRepository;

    @InjectMocks
    private AsignaturasService service;

    // ── listarTodas ──────────────────────────────────────────────────

    @Test
    void listarTodas_retornaListaCompleta() {
        Asignatura a = asignatura(UUID.randomUUID(), "Matemáticas", 4);
        when(asignaturasRepository.findAll()).thenReturn(List.of(a));

        List<Asignatura> resultado = service.listarTodas();

        assertThat(resultado).hasSize(1);
        verify(asignaturasRepository).findAll();
    }

    // ── buscarPorId ──────────────────────────────────────────────────

    @Test
    void buscarPorId_cuandoExiste_retornaAsignatura() {
        UUID id = UUID.randomUUID();
        Asignatura a = asignatura(id, "Física", 3);
        when(asignaturasRepository.findById(id)).thenReturn(Optional.of(a));

        Asignatura resultado = service.buscarPorId(id);

        assertThat(resultado.getNombre()).isEqualTo("Física");
    }

    @Test
    void buscarPorId_cuandoNoExiste_lanzaEntityNotFoundException() {
        UUID id = UUID.randomUUID();
        when(asignaturasRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    // ── crear ────────────────────────────────────────────────────────

    @Test
    void crear_cuandoCreditoExiste_reutilizaCredito() {
        // Arrange
        Credito creditoExistente = credito(UUID.randomUUID(), 4);
        AsignaturaDTO dto = new AsignaturaDTO("Cálculo I", 4);
        Asignatura guardada = asignatura(UUID.randomUUID(), "Cálculo I", 4);

        when(creditoRepository.findAll()).thenReturn(List.of(creditoExistente));
        when(asignaturasRepository.save(any(Asignatura.class))).thenReturn(guardada);

        // Act
        Asignatura resultado = service.crear(dto);

        // Assert
        assertThat(resultado.getNombre()).isEqualTo("Cálculo I");
        verify(creditoRepository, never()).save(any()); // no se creó nuevo crédito
        verify(asignaturasRepository).save(any(Asignatura.class));
    }

    @Test
    void crear_cuandoCreditoNoExiste_creaYGuardaCredito() {
        // Arrange
        AsignaturaDTO dto = new AsignaturaDTO("Química", 5);
        Credito nuevoCred = credito(UUID.randomUUID(), 5);
        Asignatura guardada = asignatura(UUID.randomUUID(), "Química", 5);

        when(creditoRepository.findAll()).thenReturn(List.of()); // no hay créditos
        when(creditoRepository.save(any(Credito.class))).thenReturn(nuevoCred);
        when(asignaturasRepository.save(any(Asignatura.class))).thenReturn(guardada);

        // Act
        Asignatura resultado = service.crear(dto);

        // Assert
        assertThat(resultado).isNotNull();
        verify(creditoRepository).save(any(Credito.class)); // se creó el crédito
    }

    // ── actualizar ───────────────────────────────────────────────────

    @Test
    void actualizar_cuandoExiste_actualizaYGuarda() {
        UUID id = UUID.randomUUID();
        Asignatura existente = asignatura(id, "Álgebra", 3);
        AsignaturaDTO dto = new AsignaturaDTO("Álgebra Lineal", 4);
        Credito cred = credito(UUID.randomUUID(), 4);

        when(asignaturasRepository.findById(id)).thenReturn(Optional.of(existente));
        when(creditoRepository.findAll()).thenReturn(List.of(cred));
        when(asignaturasRepository.save(any(Asignatura.class))).thenReturn(existente);

        Asignatura resultado = service.actualizar(id, dto);

        assertThat(resultado).isNotNull();
        verify(asignaturasRepository).save(existente);
    }

    @Test
    void actualizar_cuandoNoExiste_lanzaEntityNotFoundException() {
        UUID id = UUID.randomUUID();
        when(asignaturasRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(id, new AsignaturaDTO("x", 3)))
                .isInstanceOf(EntityNotFoundException.class);

        verify(asignaturasRepository, never()).save(any());
    }

    // ── eliminar ─────────────────────────────────────────────────────

    @Test
    void eliminar_cuandoExiste_eliminaAsignatura() {
        UUID id = UUID.randomUUID();
        Asignatura existente = asignatura(id, "Historia", 2);
        when(asignaturasRepository.findById(id)).thenReturn(Optional.of(existente));

        service.eliminar(id);

        verify(asignaturasRepository).delete(existente);
    }

    @Test
    void eliminar_cuandoNoExiste_lanzaEntityNotFoundException() {
        UUID id = UUID.randomUUID();
        when(asignaturasRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminar(id))
                .isInstanceOf(EntityNotFoundException.class);

        verify(asignaturasRepository, never()).delete(any());
    }

    // ── helpers ──────────────────────────────────────────────────────

    private Asignatura asignatura(UUID id, String nombre, int cantCreditos) {
        Asignatura a = new Asignatura();
        a.setId(id);
        a.setNombre(nombre);
        a.setCredito(credito(UUID.randomUUID(), cantCreditos));
        return a;
    }

    private Credito credito(UUID id, int cantidad) {
        Credito c = new Credito();
        c.setId(id);
        c.setCantidad(cantidad);
        return c;
    }
}
