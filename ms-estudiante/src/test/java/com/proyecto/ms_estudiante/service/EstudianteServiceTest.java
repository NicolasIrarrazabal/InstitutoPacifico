package com.proyecto.ms_estudiante.service;

import com.proyecto.ms_estudiante.client.MatriculaClientService;
import com.proyecto.ms_estudiante.client.MatriculaClientService.MatriculaResponse;
import com.proyecto.ms_estudiante.client.NotaClientService;
import com.proyecto.ms_estudiante.client.NotaClientService.NotaResponse;
import com.proyecto.ms_estudiante.dto.DetalleEstudianteResponse;
import com.proyecto.ms_estudiante.dto.EstudianteDTO;
import com.proyecto.ms_estudiante.model.Estudiante;
import com.proyecto.ms_estudiante.model.enums.EstadoEstudiante;
import com.proyecto.ms_estudiante.repository.EstudianteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstudianteServiceTest {

    @Mock
    private EstudianteRepository repository;

    @Mock
    private NotaClientService notaClient;

    @Mock
    private MatriculaClientService matriculaClient;

    @InjectMocks
    private EstudianteService service;

    // ── findAll ──────────────────────────────────────────────────────

    @Test
    void findAll_retornaListaEstudiantes() {
        when(repository.findAll()).thenReturn(List.of(estudiante(UUID.randomUUID(), "12345678-9")));

        List<Estudiante> resultado = service.findAll();

        assertThat(resultado).hasSize(1);
        verify(repository).findAll();
    }

    // ── findById ─────────────────────────────────────────────────────

    @Test
    void findById_cuandoExiste_retornaEstudiante() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(estudiante(id, "12345678-9")));

        Estudiante resultado = service.findById(id);

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

    // ── findByRut ────────────────────────────────────────────────────

    @Test
    void findByRut_cuandoExiste_retornaEstudiante() {
        String rut = "12345678-9";
        when(repository.findByRut(rut)).thenReturn(Optional.of(estudiante(UUID.randomUUID(), rut)));

        Estudiante resultado = service.findByRut(rut);

        assertThat(resultado.getRut()).isEqualTo(rut);
    }

    @Test
    void findByRut_cuandoNoExiste_lanzaEntityNotFoundException() {
        String rut = "99999999-9";
        when(repository.findByRut(rut)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByRut(rut))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(rut);
    }

    // ── save ─────────────────────────────────────────────────────────

    @Test
    void save_caminoFeliz_guardaEstudianteConEstadoActivo() {
        EstudianteDTO dto = new EstudianteDTO("Juan Pérez", "12345678-9",
                "juan@mail.com", "+56912345678", "Av. Test 123");
        Estudiante guardado = estudiante(UUID.randomUUID(), "12345678-9");

        when(repository.existsByRut("12345678-9")).thenReturn(false);
        when(repository.existsByEmail("juan@mail.com")).thenReturn(false);
        when(repository.save(any(Estudiante.class))).thenReturn(guardado);

        Estudiante resultado = service.save(dto);

        assertThat(resultado.getEstado()).isEqualTo(EstadoEstudiante.ACTIVO);
        verify(repository).save(any(Estudiante.class));
    }

    @Test
    void save_cuandoRutDuplicado_lanzaIllegalArgumentException() {
        EstudianteDTO dto = new EstudianteDTO("Ana López", "12345678-9",
                "ana@mail.com", null, null);
        when(repository.existsByRut("12345678-9")).thenReturn(true);

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("RUT");

        verify(repository, never()).save(any());
    }

    @Test
    void save_cuandoEmailDuplicado_lanzaIllegalArgumentException() {
        EstudianteDTO dto = new EstudianteDTO("Pedro García", "98765432-1",
                "repetido@mail.com", null, null);
        when(repository.existsByRut("98765432-1")).thenReturn(false);
        when(repository.existsByEmail("repetido@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");

        verify(repository, never()).save(any());
    }

    // ── update ───────────────────────────────────────────────────────

    @Test
    void update_cuandoActivo_actualizaCampos() {
        UUID id = UUID.randomUUID();
        Estudiante existente = estudiante(id, "12345678-9");
        existente.setEstado(EstadoEstudiante.ACTIVO);
        existente.setEmail("original@mail.com");

        EstudianteDTO dto = new EstudianteDTO("Nuevo Nombre", "12345678-9",
                "original@mail.com", "+56912345678", "Nueva dirección");

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Estudiante.class))).thenReturn(existente);

        Estudiante resultado = service.update(id, dto);

        assertThat(resultado).isNotNull();
        verify(repository).save(existente);
    }

    @Test
    void update_cuandoInactivo_lanzaIllegalStateException() {
        UUID id = UUID.randomUUID();
        Estudiante existente = estudiante(id, "12345678-9");
        existente.setEstado(EstadoEstudiante.INACTIVO);

        when(repository.findById(id)).thenReturn(Optional.of(existente));

        EstudianteDTO dto = new EstudianteDTO("Nombre", "12345678-9",
                "email@mail.com", null, null);

        assertThatThrownBy(() -> service.update(id, dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("INACTIVO");

        verify(repository, never()).save(any());
    }

    @Test
    void update_cuandoNuevoEmailEnUso_lanzaIllegalArgumentException() {
        UUID id = UUID.randomUUID();
        Estudiante existente = estudiante(id, "12345678-9");
        existente.setEmail("original@mail.com");
        existente.setEstado(EstadoEstudiante.ACTIVO);

        EstudianteDTO dto = new EstudianteDTO("Nombre", "12345678-9",
                "ocupado@mail.com", null, null);

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.existsByEmail("ocupado@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> service.update(id, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");

        verify(repository, never()).save(any());
    }

    // ── delete ───────────────────────────────────────────────────────

    @Test
    void delete_cuandoActivo_marcaComoInactivo() {
        UUID id = UUID.randomUUID();
        Estudiante existente = estudiante(id, "12345678-9");
        existente.setEstado(EstadoEstudiante.ACTIVO);

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Estudiante.class))).thenReturn(existente);

        service.delete(id);

        assertThat(existente.getEstado()).isEqualTo(EstadoEstudiante.INACTIVO);
        verify(repository).save(existente);
    }

    @Test
    void delete_cuandoYaInactivo_lanzaIllegalStateException() {
        UUID id = UUID.randomUUID();
        Estudiante existente = estudiante(id, "12345678-9");
        existente.setEstado(EstadoEstudiante.INACTIVO);

        when(repository.findById(id)).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("INACTIVO");

        verify(repository, never()).save(any());
    }

    // ── puedeMatricular ──────────────────────────────────────────────

    @Test
    void puedeMatricular_cuandoActivo_retornaTrue() {
        UUID id = UUID.randomUUID();
        Estudiante e = estudiante(id, "12345678-9");
        e.setEstado(EstadoEstudiante.ACTIVO);
        when(repository.findById(id)).thenReturn(Optional.of(e));

        boolean resultado = service.puedeMatricular(id);

        assertThat(resultado).isTrue();
    }

    @Test
    void puedeMatricular_cuandoInactivo_retornaFalse() {
        UUID id = UUID.randomUUID();
        Estudiante e = estudiante(id, "12345678-9");
        e.setEstado(EstadoEstudiante.INACTIVO);
        when(repository.findById(id)).thenReturn(Optional.of(e));

        boolean resultado = service.puedeMatricular(id);

        assertThat(resultado).isFalse();
    }

    // ── obtenerDetalle ───────────────────────────────────────────────

    @Test
    void obtenerDetalle_sinNotas_retornaPromediosCero() {
        UUID id = UUID.randomUUID();
        Estudiante e = estudiante(id, "12345678-9");
        e.setEstado(EstadoEstudiante.ACTIVO);

        when(repository.findById(id)).thenReturn(Optional.of(e));
        when(notaClient.obtenerNotasEstudiante(id)).thenReturn(List.of());
        when(matriculaClient.obtenerMatriculasActivas(id)).thenReturn(List.of());

        DetalleEstudianteResponse resultado = service.obtenerDetalle(id);

        assertThat(resultado.totalNotas()).isEqualTo(0);
        assertThat(resultado.promedioPonderado()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(resultado.aprobado()).isFalse();
    }

    @Test
    void obtenerDetalle_conNotasAprobatorias_retornaAprobado() {
        UUID id = UUID.randomUUID();
        Estudiante e = estudiante(id, "12345678-9");
        e.setEstado(EstadoEstudiante.ACTIVO);

        NotaResponse nota1 = new NotaResponse(UUID.randomUUID(), id, UUID.randomUUID(),
                new BigDecimal("5.5"), "PARCIAL1", new BigDecimal("0.5"));
        NotaResponse nota2 = new NotaResponse(UUID.randomUUID(), id, UUID.randomUUID(),
                new BigDecimal("6.0"), "PARCIAL2", new BigDecimal("0.5"));

        when(repository.findById(id)).thenReturn(Optional.of(e));
        when(notaClient.obtenerNotasEstudiante(id)).thenReturn(List.of(nota1, nota2));
        when(matriculaClient.obtenerMatriculasActivas(id)).thenReturn(List.of());

        DetalleEstudianteResponse resultado = service.obtenerDetalle(id);

        assertThat(resultado.aprobado()).isTrue();
        assertThat(resultado.totalNotas()).isEqualTo(2);
    }

    // ── helper ───────────────────────────────────────────────────────

    private Estudiante estudiante(UUID id, String rut) {
        Estudiante e = new Estudiante();
        e.setId(id);
        e.setNombre("Estudiante Test");
        e.setRut(rut);
        e.setEmail("test@mail.com");
        e.setEstado(EstadoEstudiante.ACTIVO);
        return e;
    }
}
