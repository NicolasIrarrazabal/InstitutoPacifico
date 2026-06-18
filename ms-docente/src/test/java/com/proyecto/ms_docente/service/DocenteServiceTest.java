package com.proyecto.ms_docente.service;

import com.proyecto.ms_docente.dto.DocenteDTO;
import com.proyecto.ms_docente.model.Docente;
import com.proyecto.ms_docente.model.Especialidad;
import com.proyecto.ms_docente.repository.DocenteRepository;
import com.proyecto.ms_docente.repository.EspecialidadRepository;
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
class DocenteServiceTest {

    @Mock
    private DocenteRepository repository;

    @Mock
    private EspecialidadRepository especialidadRepository;

    @InjectMocks
    private DocenteService service;

    // ── listarTodos ──────────────────────────────────────────────────

    @Test
    void listarTodos_retornaListaDocentes() {
        when(repository.findAll()).thenReturn(List.of(docente(UUID.randomUUID(), "juan@mail.com")));

        List<Docente> resultado = service.listarTodos();

        assertThat(resultado).hasSize(1);
        verify(repository).findAll();
    }

    // ── buscarPorId ──────────────────────────────────────────────────

    @Test
    void buscarPorId_cuandoExiste_retornaDocente() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(docente(id, "ana@mail.com")));

        Docente resultado = service.buscarPorId(id);

        assertThat(resultado.getId()).isEqualTo(id);
    }

    @Test
    void buscarPorId_cuandoNoExiste_lanzaEntityNotFoundException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    // ── guardar ──────────────────────────────────────────────────────

    @Test
    void guardar_caminoFeliz_guardaDocenteConEspecialidad() {
        UUID espId = UUID.randomUUID();
        DocenteDTO dto = new DocenteDTO("Pedro", "González", "pedro@mail.com", espId);
        Especialidad esp = especialidad(espId, "Matemáticas");
        Docente guardado = docente(UUID.randomUUID(), "pedro@mail.com");

        when(repository.existsByEmail(dto.email())).thenReturn(false);
        when(especialidadRepository.findById(espId)).thenReturn(Optional.of(esp));
        when(repository.save(any(Docente.class))).thenReturn(guardado);

        Docente resultado = service.guardar(dto);

        assertThat(resultado).isNotNull();
        verify(repository).save(any(Docente.class));
    }

    @Test
    void guardar_cuandoEmailDuplicado_lanzaIllegalArgumentException() {
        DocenteDTO dto = new DocenteDTO("Pedro", "López", "repetido@mail.com", UUID.randomUUID());
        when(repository.existsByEmail("repetido@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> service.guardar(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");

        verify(repository, never()).save(any());
    }

    @Test
    void guardar_cuandoEspecialidadNoExiste_lanzaEntityNotFoundException() {
        UUID espId = UUID.randomUUID();
        DocenteDTO dto = new DocenteDTO("Ana", "Ruiz", "ana@mail.com", espId);

        when(repository.existsByEmail("ana@mail.com")).thenReturn(false);
        when(especialidadRepository.findById(espId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.guardar(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(espId.toString());

        verify(repository, never()).save(any());
    }

    // ── actualizar ───────────────────────────────────────────────────

    @Test
    void actualizar_mismoEmail_actualizaOtrosCampos() {
        UUID id = UUID.randomUUID();
        UUID espId = UUID.randomUUID();
        Docente existente = docente(id, "mismo@mail.com");
        existente.setEspecialidad(especialidad(espId, "Física"));

        DocenteDTO dto = new DocenteDTO("Nuevo Nombre", "Nuevo Apellido", "mismo@mail.com", espId);
        Especialidad esp = especialidad(espId, "Física");

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(especialidadRepository.findById(espId)).thenReturn(Optional.of(esp));
        when(repository.save(any(Docente.class))).thenReturn(existente);

        Docente resultado = service.actualizar(id, dto);

        assertThat(resultado).isNotNull();
        verify(repository).save(existente);
    }

    @Test
    void actualizar_cuandoNuevoEmailEnUso_lanzaIllegalArgumentException() {
        UUID id = UUID.randomUUID();
        Docente existente = docente(id, "original@mail.com");
        DocenteDTO dto = new DocenteDTO("Ana", "García", "ocupado@mail.com", UUID.randomUUID());

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.existsByEmail("ocupado@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> service.actualizar(id, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");

        verify(repository, never()).save(any());
    }

    @Test
    void actualizar_cuandoEspecialidadIdNulo_noIntentaCambiarEspecialidad() {
        UUID id = UUID.randomUUID();
        Docente existente = docente(id, "doc@mail.com");
        existente.setEspecialidad(especialidad(UUID.randomUUID(), "Historia"));

        DocenteDTO dto = new DocenteDTO("Nombre", "Apellido", "doc@mail.com", null);

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Docente.class))).thenReturn(existente);

        service.actualizar(id, dto);

        verify(especialidadRepository, never()).findById(any());
    }

    // ── eliminar ─────────────────────────────────────────────────────

    @Test
    void eliminar_cuandoExiste_eliminaDocente() {
        UUID id = UUID.randomUUID();
        Docente existente = docente(id, "eliminar@mail.com");
        when(repository.findById(id)).thenReturn(Optional.of(existente));

        service.eliminar(id);

        verify(repository).delete(existente);
    }

    @Test
    void eliminar_cuandoNoExiste_lanzaEntityNotFoundException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminar(id))
                .isInstanceOf(EntityNotFoundException.class);

        verify(repository, never()).delete(any());
    }

    // ── helpers ──────────────────────────────────────────────────────

    private Docente docente(UUID id, String email) {
        Docente d = new Docente();
        d.setId(id);
        d.setNombre("Nombre");
        d.setApellido("Apellido");
        d.setEmail(email);
        d.setEspecialidad(especialidad(UUID.randomUUID(), "General"));
        return d;
    }

    private Especialidad especialidad(UUID id, String nombre) {
        Especialidad e = new Especialidad();
        e.setId(id);
        e.setNombre(nombre);
        e.setDescripcion("Descripción");
        return e;
    }
}
