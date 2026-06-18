package com.proyecto.ms_asistencia.service;

import com.proyecto.ms_asistencia.client.MatriculaClientService;
import com.proyecto.ms_asistencia.dto.AsistenciaDTO;
import com.proyecto.ms_asistencia.dto.RegistroAsistenciaResponseDTO;
import com.proyecto.ms_asistencia.dto.ResumenAsistenciaDTO;
import com.proyecto.ms_asistencia.model.Asistencia;
import com.proyecto.ms_asistencia.model.TipoAsistencia;
import com.proyecto.ms_asistencia.repository.AsistenciaRepository;
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
class AsistenciaServiceTest {

    @Mock
    private AsistenciaRepository repository;

    @Mock
    private MatriculaClientService matriculaClient;

    @InjectMocks
    private AsistenciaService service;

    // ── registrar ────────────────────────────────────────────────────

    @Test
    void registrar_caminoFeliz_guardaAsistenciaYRetornaResumen() {
        // Arrange
        UUID estudianteId = UUID.randomUUID();
        UUID seccionId = UUID.randomUUID();
        LocalDate fecha = LocalDate.now();

        AsistenciaDTO dto = new AsistenciaDTO(estudianteId, seccionId, fecha, TipoAsistencia.PRESENTE, null);

        when(matriculaClient.tieneMatriculaActiva(estudianteId, seccionId)).thenReturn(true);
        when(repository.existsByEstudianteIdAndSeccionIdAndFechaAndEstado(
                estudianteId, seccionId, fecha, "ACTIVO")).thenReturn(false);

        Asistencia guardada = asistencia(UUID.randomUUID(), estudianteId, seccionId, fecha, TipoAsistencia.PRESENTE);
        when(repository.save(any(Asistencia.class))).thenReturn(guardada);

        // calcularResumenR2 internamente:
        when(repository.contarTotalClasesPorSeccion(seccionId)).thenReturn(10L);
        when(repository.contarPorTipo(estudianteId, seccionId, TipoAsistencia.PRESENTE)).thenReturn(9L);
        when(repository.contarPorTipo(estudianteId, seccionId, TipoAsistencia.AUSENTE)).thenReturn(1L);
        when(repository.contarPorTipo(estudianteId, seccionId, TipoAsistencia.JUSTIFICADO)).thenReturn(0L);

        // Act
        RegistroAsistenciaResponseDTO resultado = service.registrar(dto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.asistencia()).isNotNull();
        assertThat(resultado.resumenR2().reprobadoPorAsistencia()).isFalse();
        verify(repository).save(any(Asistencia.class));
    }

    @Test
    void registrar_cuandoSinMatriculaActiva_lanzaIllegalStateException() {
        UUID estudianteId = UUID.randomUUID();
        UUID seccionId = UUID.randomUUID();
        AsistenciaDTO dto = new AsistenciaDTO(estudianteId, seccionId, LocalDate.now(), TipoAsistencia.PRESENTE, null);

        when(matriculaClient.tieneMatriculaActiva(estudianteId, seccionId)).thenReturn(false);

        assertThatThrownBy(() -> service.registrar(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("matrícula");

        verify(repository, never()).save(any());
    }

    @Test
    void registrar_cuandoRegistroDuplicado_lanzaIllegalStateException() {
        UUID estudianteId = UUID.randomUUID();
        UUID seccionId = UUID.randomUUID();
        LocalDate fecha = LocalDate.now();
        AsistenciaDTO dto = new AsistenciaDTO(estudianteId, seccionId, fecha, TipoAsistencia.PRESENTE, null);

        when(matriculaClient.tieneMatriculaActiva(estudianteId, seccionId)).thenReturn(true);
        when(repository.existsByEstudianteIdAndSeccionIdAndFechaAndEstado(
                estudianteId, seccionId, fecha, "ACTIVO")).thenReturn(true);

        assertThatThrownBy(() -> service.registrar(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Ya existe");

        verify(repository, never()).save(any());
    }

    @Test
    void registrar_cuandoClienteLanzaExcepcion_propagaIllegalState() {
        UUID estudianteId = UUID.randomUUID();
        UUID seccionId = UUID.randomUUID();
        AsistenciaDTO dto = new AsistenciaDTO(estudianteId, seccionId, LocalDate.now(), TipoAsistencia.PRESENTE, null);

        when(matriculaClient.tieneMatriculaActiva(estudianteId, seccionId))
                .thenThrow(new RuntimeException("ms-matriculas no disponible"));

        assertThatThrownBy(() -> service.registrar(dto))
                .isInstanceOf(IllegalStateException.class);
    }

    // ── actualizar ───────────────────────────────────────────────────

    @Test
    void actualizar_cuandoActivo_actualizaTipoYRetornaResumen() {
        UUID id = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        UUID seccionId = UUID.randomUUID();

        Asistencia existente = asistencia(id, estudianteId, seccionId, LocalDate.now(), TipoAsistencia.AUSENTE);
        existente.setEstado("ACTIVO");

        AsistenciaDTO dto = new AsistenciaDTO(estudianteId, seccionId, LocalDate.now(), TipoAsistencia.JUSTIFICADO, "Médico");

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Asistencia.class))).thenReturn(existente);
        when(repository.contarTotalClasesPorSeccion(seccionId)).thenReturn(5L);
        when(repository.contarPorTipo(estudianteId, seccionId, TipoAsistencia.PRESENTE)).thenReturn(4L);
        when(repository.contarPorTipo(estudianteId, seccionId, TipoAsistencia.AUSENTE)).thenReturn(0L);
        when(repository.contarPorTipo(estudianteId, seccionId, TipoAsistencia.JUSTIFICADO)).thenReturn(1L);

        RegistroAsistenciaResponseDTO resultado = service.actualizar(id, dto);

        assertThat(resultado).isNotNull();
        verify(repository).save(existente);
    }

    @Test
    void actualizar_cuandoAnulado_lanzaIllegalStateException() {
        UUID id = UUID.randomUUID();
        Asistencia existente = asistencia(id, UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), TipoAsistencia.PRESENTE);
        existente.setEstado("ANULADO");

        when(repository.findById(id)).thenReturn(Optional.of(existente));

        AsistenciaDTO dto = new AsistenciaDTO(UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), TipoAsistencia.PRESENTE, null);

        assertThatThrownBy(() -> service.actualizar(id, dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("anulado");

        verify(repository, never()).save(any());
    }

    // ── anular ───────────────────────────────────────────────────────

    @Test
    void anular_marcaRegistroComoAnulado() {
        UUID id = UUID.randomUUID();
        Asistencia existente = asistencia(id, UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), TipoAsistencia.PRESENTE);
        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Asistencia.class))).thenReturn(existente);

        service.anular(id);

        assertThat(existente.getEstado()).isEqualTo("ANULADO");
        verify(repository).save(existente);
    }

    // ── findById ─────────────────────────────────────────────────────

    @Test
    void findById_cuandoExiste_retornaAsistencia() {
        UUID id = UUID.randomUUID();
        Asistencia a = asistencia(id, UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), TipoAsistencia.PRESENTE);
        when(repository.findById(id)).thenReturn(Optional.of(a));

        Asistencia resultado = service.findById(id);

        assertThat(resultado.getId()).isEqualTo(id);
    }

    @Test
    void findById_cuandoNoExiste_lanzaEntityNotFoundException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // ── findBySeccion / findByEstudiante / findByEstudianteYSeccion ──

    @Test
    void findBySeccion_delegaAlRepositorio() {
        UUID seccionId = UUID.randomUUID();
        when(repository.findBySeccionIdAndEstado(seccionId, "ACTIVO")).thenReturn(List.of());

        service.findBySeccion(seccionId);

        verify(repository).findBySeccionIdAndEstado(seccionId, "ACTIVO");
    }

    @Test
    void findByEstudiante_delegaAlRepositorio() {
        UUID estudianteId = UUID.randomUUID();
        when(repository.findByEstudianteIdAndEstado(estudianteId, "ACTIVO")).thenReturn(List.of());

        service.findByEstudiante(estudianteId);

        verify(repository).findByEstudianteIdAndEstado(estudianteId, "ACTIVO");
    }

    @Test
    void findByEstudianteYSeccion_delegaAlRepositorio() {
        UUID estudianteId = UUID.randomUUID();
        UUID seccionId = UUID.randomUUID();
        when(repository.findByEstudianteIdAndSeccionIdAndEstado(estudianteId, seccionId, "ACTIVO"))
                .thenReturn(List.of());

        service.findByEstudianteYSeccion(estudianteId, seccionId);

        verify(repository).findByEstudianteIdAndSeccionIdAndEstado(estudianteId, seccionId, "ACTIVO");
    }

    // ── calcularResumenR2 ────────────────────────────────────────────

    @Test
    void calcularResumenR2_cuandoSinClases_retornaResumenVacio() {
        UUID estudianteId = UUID.randomUUID();
        UUID seccionId = UUID.randomUUID();
        when(repository.contarTotalClasesPorSeccion(seccionId)).thenReturn(0L);

        ResumenAsistenciaDTO resultado = service.calcularResumenR2(estudianteId, seccionId);

        assertThat(resultado.totalClases()).isEqualTo(0);
        assertThat(resultado.reprobadoPorAsistencia()).isFalse();
        assertThat(resultado.porcentajeInasistencia()).isEqualTo(0.0);
    }

    @Test
    void calcularResumenR2_cuandoPorcentajeSuperior25_reprueba() {
        UUID estudianteId = UUID.randomUUID();
        UUID seccionId = UUID.randomUUID();

        when(repository.contarTotalClasesPorSeccion(seccionId)).thenReturn(4L);
        when(repository.contarPorTipo(estudianteId, seccionId, TipoAsistencia.PRESENTE)).thenReturn(2L);
        when(repository.contarPorTipo(estudianteId, seccionId, TipoAsistencia.AUSENTE)).thenReturn(2L);
        when(repository.contarPorTipo(estudianteId, seccionId, TipoAsistencia.JUSTIFICADO)).thenReturn(0L);

        ResumenAsistenciaDTO resultado = service.calcularResumenR2(estudianteId, seccionId);

        assertThat(resultado.reprobadoPorAsistencia()).isTrue();
        assertThat(resultado.porcentajeInasistencia()).isGreaterThan(25.0);
    }

    @Test
    void calcularResumenR2_cuandoPorcentajeMenorOIgual25_noReprueba() {
        UUID estudianteId = UUID.randomUUID();
        UUID seccionId = UUID.randomUUID();

        when(repository.contarTotalClasesPorSeccion(seccionId)).thenReturn(8L);
        when(repository.contarPorTipo(estudianteId, seccionId, TipoAsistencia.PRESENTE)).thenReturn(6L);
        when(repository.contarPorTipo(estudianteId, seccionId, TipoAsistencia.AUSENTE)).thenReturn(2L);
        when(repository.contarPorTipo(estudianteId, seccionId, TipoAsistencia.JUSTIFICADO)).thenReturn(0L);

        ResumenAsistenciaDTO resultado = service.calcularResumenR2(estudianteId, seccionId);

        // 2/8 = 25% → exactamente en el límite → no reprueba (> 25, no >=)
        assertThat(resultado.reprobadoPorAsistencia()).isFalse();
    }

    // ── helpers ──────────────────────────────────────────────────────

    private Asistencia asistencia(UUID id, UUID estudianteId, UUID seccionId,
                                   LocalDate fecha, TipoAsistencia tipo) {
        Asistencia a = new Asistencia();
        a.setId(id);
        a.setEstudianteId(estudianteId);
        a.setSeccionId(seccionId);
        a.setFecha(fecha);
        a.setTipo(tipo);
        a.setEstado("ACTIVO");
        return a;
    }
}
