package com.proyecto.ms_carreras.service;

import com.proyecto.ms_carreras.dto.CarreraDTO;
import com.proyecto.ms_carreras.model.Carrera;
import com.proyecto.ms_carreras.repository.CarreraRepository;
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
class CarreraServiceTest {

    @Mock
    private CarreraRepository repository;

    @InjectMocks
    private CarreraService service;

    // ── findAll ──────────────────────────────────────────────────────

    @Test
    void findAll_retornaListaDeCarreras() {
        Carrera c = carrera(UUID.randomUUID(), "Ingeniería Informática");
        when(repository.findAll()).thenReturn(List.of(c));

        List<Carrera> resultado = service.findAll();

        assertThat(resultado).hasSize(1);
        verify(repository).findAll();
    }

    // ── findById ─────────────────────────────────────────────────────

    @Test
    void findById_cuandoExiste_retornaCarrera() {
        UUID id = UUID.randomUUID();
        Carrera c = carrera(id, "Administración");
        when(repository.findById(id)).thenReturn(Optional.of(c));

        Carrera resultado = service.findById(id);

        assertThat(resultado.getNombre()).isEqualTo("Administración");
    }

    @Test
    void findById_cuandoNoExiste_lanzaEntityNotFoundException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    // ── create ───────────────────────────────────────────────────────

    @Test
    void create_mapeaCorrectamenteTodosCamposYGuarda() {
        CarreraDTO dto = new CarreraDTO("Derecho", "Carrera de derecho", 10, "Santiago");
        Carrera guardada = carrera(UUID.randomUUID(), "Derecho");
        guardada.setDescripcion("Carrera de derecho");
        guardada.setDuracionSemestres(10);
        guardada.setSede("Santiago");

        when(repository.save(any(Carrera.class))).thenReturn(guardada);

        Carrera resultado = service.create(dto);

        assertThat(resultado.getNombre()).isEqualTo("Derecho");
        verify(repository).save(any(Carrera.class));
    }

    // ── update ───────────────────────────────────────────────────────

    @Test
    void update_cuandoExiste_actualizaTodosCampos() {
        UUID id = UUID.randomUUID();
        Carrera existente = carrera(id, "Enfermería");
        CarreraDTO dto = new CarreraDTO("Enfermería Clínica", "Descripción actualizada", 9, "Valparaíso");

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Carrera.class))).thenReturn(existente);

        Carrera resultado = service.update(id, dto);

        assertThat(resultado).isNotNull();
        verify(repository).save(existente);
    }

    @Test
    void update_cuandoNoExiste_lanzaEntityNotFoundException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(id, new CarreraDTO("x", null, 1, "y")))
                .isInstanceOf(EntityNotFoundException.class);

        verify(repository, never()).save(any());
    }

    // ── delete ───────────────────────────────────────────────────────

    @Test
    void delete_cuandoExiste_eliminaCarrera() {
        UUID id = UUID.randomUUID();
        Carrera existente = carrera(id, "Psicología");
        when(repository.findById(id)).thenReturn(Optional.of(existente));

        service.delete(id);

        verify(repository).delete(existente);
    }

    @Test
    void delete_cuandoNoExiste_lanzaEntityNotFoundException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(EntityNotFoundException.class);

        verify(repository, never()).delete(any());
    }

    // ── helper ───────────────────────────────────────────────────────

    private Carrera carrera(UUID id, String nombre) {
        Carrera c = new Carrera();
        c.setId(id);
        c.setNombre(nombre);
        c.setDescripcion("Descripción de prueba");
        c.setDuracionSemestres(8);
        c.setSede("Santiago");
        c.setDisponible(true);
        return c;
    }

    // ── estaDisponible (R1) ─────────────────────────────────────────

    @Test
    void estaDisponible_cuandoCarreraDisponible_retornaTrue() {
        UUID id = UUID.randomUUID();
        Carrera c = carrera(id, "Ingeniería Civil");
        c.setDisponible(true);
        when(repository.findById(id)).thenReturn(Optional.of(c));

        boolean resultado = service.estaDisponible(id);

        assertThat(resultado).isTrue();
    }

    @Test
    void estaDisponible_cuandoCarreraNoDisponible_retornaFalse() {
        UUID id = UUID.randomUUID();
        Carrera c = carrera(id, "Ingeniería Civil");
        c.setDisponible(false);
        when(repository.findById(id)).thenReturn(Optional.of(c));

        boolean resultado = service.estaDisponible(id);

        assertThat(resultado).isFalse();
    }

    @Test
    void estaDisponible_cuandoNoExiste_lanzaEntityNotFoundException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.estaDisponible(id))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // ── cambiarDisponibilidad ────────────────────────────────────────

    @Test
    void cambiarDisponibilidad_actualizaFlagYPersiste() {
        UUID id = UUID.randomUUID();
        Carrera c = carrera(id, "Ingeniería Civil");
        c.setDisponible(true);
        when(repository.findById(id)).thenReturn(Optional.of(c));
        when(repository.save(any(Carrera.class))).thenAnswer(inv -> inv.getArgument(0));

        Carrera resultado = service.cambiarDisponibilidad(id, false);

        assertThat(resultado.getDisponible()).isFalse();
        verify(repository).save(c);
    }

    @Test
    void create_carreraNueva_quedaDisponiblePorDefecto() {
        CarreraDTO dto = new CarreraDTO("Ingeniería Civil", "desc", 8, "Santiago");
        when(repository.save(any(Carrera.class))).thenAnswer(inv -> inv.getArgument(0));

        Carrera resultado = service.create(dto);

        assertThat(resultado.getDisponible()).isTrue();
    }
}
