package com.proyecto.ms_notas.service;

import com.proyecto.ms_notas.client.ArancelClientService;
import com.proyecto.ms_notas.client.MatriculaClientService;
import com.proyecto.ms_notas.dto.AvanceResponseDTO;
import com.proyecto.ms_notas.dto.NotaDTO;
import com.proyecto.ms_notas.dto.PromedioResponseDTO;
import com.proyecto.ms_notas.model.Nota;
import com.proyecto.ms_notas.repository.NotaRepository;
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
class NotaServiceTest {

    @Mock
    private NotaRepository repository;

    @Mock
    private MatriculaClientService matriculaClient;

    @Mock
    private ArancelClientService arancelClient;

    @InjectMocks
    private NotaService service;

    // ── findAll ──────────────────────────────────────────────────────

    @Test
    void findAll_retornaSoloNotasActivas() {
        Nota activa = nota(UUID.randomUUID(), new BigDecimal("5.0"), new BigDecimal("0.5"), "ACTIVA");
        Nota anulada = nota(UUID.randomUUID(), new BigDecimal("3.0"), new BigDecimal("0.5"), "ANULADA");
        when(repository.findAll()).thenReturn(List.of(activa, anulada));

        List<Nota> resultado = service.findAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo("ACTIVA");
    }

    // ── findById ─────────────────────────────────────────────────────

    @Test
    void findById_cuandoExiste_retornaNota() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(nota(id, new BigDecimal("6.0"), new BigDecimal("0.3"), "ACTIVA")));

        Nota resultado = service.findById(id);

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

    // ── findByEstudiante (R4) ────────────────────────────────────────

    @Test
    void findByEstudiante_cuandoSinDeuda_retornaNotas() {
        UUID estudianteId = UUID.randomUUID();
        when(arancelClient.tieneDeudaVencida(estudianteId)).thenReturn(false);
        when(repository.findByEstudianteIdAndEstado(estudianteId, "ACTIVA"))
                .thenReturn(List.of(nota(UUID.randomUUID(), new BigDecimal("5.0"), new BigDecimal("1.0"), "ACTIVA")));

        List<Nota> resultado = service.findByEstudiante(estudianteId);

        assertThat(resultado).hasSize(1);
    }

    @Test
    void findByEstudiante_cuandoConDeudaVencida_lanzaIllegalStateException() {
        UUID estudianteId = UUID.randomUUID();
        when(arancelClient.tieneDeudaVencida(estudianteId)).thenReturn(true);

        assertThatThrownBy(() -> service.findByEstudiante(estudianteId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("R4");

        verify(repository, never()).findByEstudianteIdAndEstado(any(), any());
    }

    // ── create ───────────────────────────────────────────────────────

    @Test
    void create_caminoFeliz_guardaNota() {
        UUID estudianteId = UUID.randomUUID();
        UUID seccionId = UUID.randomUUID();
        NotaDTO dto = notaDTO(estudianteId, seccionId, new BigDecimal("5.5"), "PARCIAL1", new BigDecimal("0.3"));
        Nota guardada = nota(UUID.randomUUID(), new BigDecimal("5.5"), new BigDecimal("0.3"), "ACTIVA");

        when(matriculaClient.tieneMatriculaActiva(estudianteId, seccionId)).thenReturn(true);
        when(repository.existsByEstudianteIdAndSeccionIdAndTipoAndEstado(
                estudianteId, seccionId, "PARCIAL1", "ACTIVA")).thenReturn(false);
        when(repository.save(any(Nota.class))).thenReturn(guardada);

        Nota resultado = service.create(dto);

        assertThat(resultado.getEstado()).isEqualTo("ACTIVA");
        verify(repository).save(any(Nota.class));
    }

    @Test
    void create_cuandoSinMatricula_lanzaIllegalStateException() {
        UUID estudianteId = UUID.randomUUID();
        UUID seccionId = UUID.randomUUID();
        NotaDTO dto = notaDTO(estudianteId, seccionId, new BigDecimal("5.0"), "PARCIAL1", new BigDecimal("0.5"));

        when(matriculaClient.tieneMatriculaActiva(estudianteId, seccionId)).thenReturn(false);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("R1");

        verify(repository, never()).save(any());
    }

    @Test
    void create_cuandoNotaDuplicada_lanzaIllegalStateException() {
        UUID estudianteId = UUID.randomUUID();
        UUID seccionId = UUID.randomUUID();
        NotaDTO dto = notaDTO(estudianteId, seccionId, new BigDecimal("4.0"), "PARCIAL1", new BigDecimal("0.5"));

        when(matriculaClient.tieneMatriculaActiva(estudianteId, seccionId)).thenReturn(true);
        when(repository.existsByEstudianteIdAndSeccionIdAndTipoAndEstado(
                estudianteId, seccionId, "PARCIAL1", "ACTIVA")).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("PARCIAL1");

        verify(repository, never()).save(any());
    }

    // ── update ───────────────────────────────────────────────────────

    @Test
    void update_cuandoActiva_actualizaNota() {
        UUID id = UUID.randomUUID();
        Nota existente = nota(id, new BigDecimal("3.0"), new BigDecimal("0.5"), "ACTIVA");
        NotaDTO dto = notaDTO(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("5.5"), "PARCIAL1", new BigDecimal("0.5"));

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Nota.class))).thenReturn(existente);

        Nota resultado = service.update(id, dto);

        assertThat(resultado).isNotNull();
        verify(repository).save(existente);
    }

    @Test
    void update_cuandoAnulada_lanzaIllegalStateException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(
                Optional.of(nota(id, new BigDecimal("3.0"), new BigDecimal("0.5"), "ANULADA")));

        NotaDTO dto = notaDTO(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("5.0"), "PARCIAL1", new BigDecimal("0.5"));

        assertThatThrownBy(() -> service.update(id, dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("anulada");

        verify(repository, never()).save(any());
    }

    // ── delete ───────────────────────────────────────────────────────

    @Test
    void delete_marcaNotaComoAnulada() {
        UUID id = UUID.randomUUID();
        Nota existente = nota(id, new BigDecimal("4.0"), new BigDecimal("0.5"), "ACTIVA");
        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Nota.class))).thenReturn(existente);

        service.delete(id);

        assertThat(existente.getEstado()).isEqualTo("ANULADA");
        verify(repository).save(existente);
    }

    // ── calcularPromedio (R3) ────────────────────────────────────────

    @Test
    void calcularPromedio_cuandoNotasAprobatorias_retornaEstadoAprobado() {
        UUID estudianteId = UUID.randomUUID();
        Nota n1 = nota(UUID.randomUUID(), new BigDecimal("5.0"), new BigDecimal("0.5"), "ACTIVA");
        n1.setEstudianteId(estudianteId);
        Nota n2 = nota(UUID.randomUUID(), new BigDecimal("6.0"), new BigDecimal("0.5"), "ACTIVA");
        n2.setEstudianteId(estudianteId);

        when(arancelClient.tieneDeudaVencida(estudianteId)).thenReturn(false);
        when(repository.findByEstudianteIdAndEstado(estudianteId, "ACTIVA"))
                .thenReturn(List.of(n1, n2));

        PromedioResponseDTO resultado = service.calcularPromedio(estudianteId);

        assertThat(resultado.estadoAcademico()).isEqualTo("APROBADO");
        assertThat(resultado.aprobado()).isTrue();
    }

    @Test
    void calcularPromedio_cuandoPromedioEnRangoRecuperacion_retornaPendiente() {
        UUID estudianteId = UUID.randomUUID();
        Nota n = nota(UUID.randomUUID(), new BigDecimal("3.7"), new BigDecimal("1.0"), "ACTIVA");
        n.setEstudianteId(estudianteId);

        when(arancelClient.tieneDeudaVencida(estudianteId)).thenReturn(false);
        when(repository.findByEstudianteIdAndEstado(estudianteId, "ACTIVA"))
                .thenReturn(List.of(n));

        PromedioResponseDTO resultado = service.calcularPromedio(estudianteId);

        assertThat(resultado.estadoAcademico()).isEqualTo("PENDIENTE_EXAMEN_RECUPERACION");
    }

    @Test
    void calcularPromedio_cuandoPromedioReprobatorio_retornaReprobado() {
        UUID estudianteId = UUID.randomUUID();
        Nota n = nota(UUID.randomUUID(), new BigDecimal("2.0"), new BigDecimal("1.0"), "ACTIVA");
        n.setEstudianteId(estudianteId);

        when(arancelClient.tieneDeudaVencida(estudianteId)).thenReturn(false);
        when(repository.findByEstudianteIdAndEstado(estudianteId, "ACTIVA"))
                .thenReturn(List.of(n));

        PromedioResponseDTO resultado = service.calcularPromedio(estudianteId);

        assertThat(resultado.estadoAcademico()).isEqualTo("REPROBADO");
        assertThat(resultado.aprobado()).isFalse();
    }

    @Test
    void calcularPromedio_cuandoSinNotas_lanzaEntityNotFoundException() {
        UUID estudianteId = UUID.randomUUID();
        when(arancelClient.tieneDeudaVencida(estudianteId)).thenReturn(false);
        when(repository.findByEstudianteIdAndEstado(estudianteId, "ACTIVA")).thenReturn(List.of());

        assertThatThrownBy(() -> service.calcularPromedio(estudianteId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void calcularPromedio_cuandoDeudaVencida_lanzaIllegalStateException() {
        UUID estudianteId = UUID.randomUUID();
        when(arancelClient.tieneDeudaVencida(estudianteId)).thenReturn(true);

        assertThatThrownBy(() -> service.calcularPromedio(estudianteId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("R4");
    }

    // ── calcularPromedioSeccion ──────────────────────────────────────

    @Test
    void calcularPromedioSeccion_cuandoHayNotas_retornaPromedio() {
        UUID estudianteId = UUID.randomUUID();
        UUID seccionId = UUID.randomUUID();
        Nota n = nota(UUID.randomUUID(), new BigDecimal("5.5"), new BigDecimal("1.0"), "ACTIVA");

        when(arancelClient.tieneDeudaVencida(estudianteId)).thenReturn(false);
        when(repository.findByEstudianteIdAndSeccionIdAndEstado(estudianteId, seccionId, "ACTIVA"))
                .thenReturn(List.of(n));

        PromedioResponseDTO resultado = service.calcularPromedioSeccion(estudianteId, seccionId);

        assertThat(resultado).isNotNull();
        assertThat(resultado.seccionId()).isEqualTo(seccionId);
    }

    @Test
    void calcularPromedioSeccion_cuandoSinNotas_lanzaEntityNotFoundException() {
        UUID estudianteId = UUID.randomUUID();
        UUID seccionId = UUID.randomUUID();

        when(arancelClient.tieneDeudaVencida(estudianteId)).thenReturn(false);
        when(repository.findByEstudianteIdAndSeccionIdAndEstado(estudianteId, seccionId, "ACTIVA"))
                .thenReturn(List.of());

        assertThatThrownBy(() -> service.calcularPromedioSeccion(estudianteId, seccionId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // ── calcularAvance (R5) ──────────────────────────────────────────

    @Test
    void calcularAvance_cuandoTodasAprobadas_cumplePorcentaje() {
        UUID estudianteId = UUID.randomUUID();
        UUID seccion1 = UUID.randomUUID();
        UUID seccion2 = UUID.randomUUID();

        Nota n1 = nota(UUID.randomUUID(), new BigDecimal("5.0"), new BigDecimal("1.0"), "ACTIVA");
        Nota n2 = nota(UUID.randomUUID(), new BigDecimal("6.0"), new BigDecimal("1.0"), "ACTIVA");

        when(repository.findSeccionesActivasByEstudianteId(estudianteId))
                .thenReturn(List.of(seccion1, seccion2));
        when(repository.findByEstudianteIdAndSeccionIdAndEstado(estudianteId, seccion1, "ACTIVA"))
                .thenReturn(List.of(n1));
        when(repository.findByEstudianteIdAndSeccionIdAndEstado(estudianteId, seccion2, "ACTIVA"))
                .thenReturn(List.of(n2));

        AvanceResponseDTO resultado = service.calcularAvance(estudianteId);

        assertThat(resultado.cumpleAvance80()).isTrue();
        assertThat(resultado.porcentajeAvance()).isEqualTo(100.0);
    }

    @Test
    void calcularAvance_cuandoMenosDel80Aprobadas_noCumple() {
        UUID estudianteId = UUID.randomUUID();
        UUID seccion1 = UUID.randomUUID();
        UUID seccion2 = UUID.randomUUID();
        UUID seccion3 = UUID.randomUUID();

        Nota reprobada = nota(UUID.randomUUID(), new BigDecimal("2.5"), new BigDecimal("1.0"), "ACTIVA");
        Nota reprobada2 = nota(UUID.randomUUID(), new BigDecimal("3.0"), new BigDecimal("1.0"), "ACTIVA");
        Nota aprobada = nota(UUID.randomUUID(), new BigDecimal("5.0"), new BigDecimal("1.0"), "ACTIVA");

        when(repository.findSeccionesActivasByEstudianteId(estudianteId))
                .thenReturn(List.of(seccion1, seccion2, seccion3));
        when(repository.findByEstudianteIdAndSeccionIdAndEstado(estudianteId, seccion1, "ACTIVA"))
                .thenReturn(List.of(reprobada));
        when(repository.findByEstudianteIdAndSeccionIdAndEstado(estudianteId, seccion2, "ACTIVA"))
                .thenReturn(List.of(reprobada2));
        when(repository.findByEstudianteIdAndSeccionIdAndEstado(estudianteId, seccion3, "ACTIVA"))
                .thenReturn(List.of(aprobada));

        AvanceResponseDTO resultado = service.calcularAvance(estudianteId);

        // 1/3 = 33.3% < 80%
        assertThat(resultado.cumpleAvance80()).isFalse();
    }

    @Test
    void calcularAvance_cuandoSinSecciones_lanzaEntityNotFoundException() {
        UUID estudianteId = UUID.randomUUID();
        when(repository.findSeccionesActivasByEstudianteId(estudianteId)).thenReturn(List.of());

        assertThatThrownBy(() -> service.calcularAvance(estudianteId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // ── helpers ──────────────────────────────────────────────────────

    private Nota nota(UUID id, BigDecimal valorNota, BigDecimal ponderacion, String estado) {
        Nota n = new Nota();
        n.setId(id);
        n.setEstudianteId(UUID.randomUUID());
        n.setSeccionId(UUID.randomUUID());
        n.setNota(valorNota);
        n.setTipo("PARCIAL1");
        n.setPonderacion(ponderacion);
        n.setFecha(LocalDate.now());
        n.setEstado(estado);
        return n;
    }

    private NotaDTO notaDTO(UUID estudianteId, UUID seccionId, BigDecimal nota, String tipo, BigDecimal ponderacion) {
        return new NotaDTO(estudianteId, seccionId, nota, tipo, ponderacion, LocalDate.now());
    }
}
