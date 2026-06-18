package com.proyecto.ms_docente.service;

import com.proyecto.ms_docente.model.Especialidad;
import com.proyecto.ms_docente.repository.EspecialidadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EspecialidadServiceTest {

    @Mock
    private EspecialidadRepository repository;

    @InjectMocks
    private EspecialidadService service;

    @Test
    void listarTodas_retornaListaCompleta() {
        Especialidad e = especialidad(UUID.randomUUID(), "Matemáticas");
        when(repository.findAll()).thenReturn(List.of(e));

        List<Especialidad> resultado = service.listarTodas();

        assertThat(resultado).hasSize(1);
        verify(repository).findAll();
    }

    @Test
    void guardar_delegaAlRepositorioYRetornaEspecialidad() {
        Especialidad e = especialidad(UUID.randomUUID(), "Física");
        when(repository.save(any(Especialidad.class))).thenReturn(e);

        Especialidad resultado = service.guardar(e);

        assertThat(resultado.getNombre()).isEqualTo("Física");
        verify(repository).save(e);
    }

    // ── helper ───────────────────────────────────────────────────────

    private Especialidad especialidad(UUID id, String nombre) {
        Especialidad e = new Especialidad();
        e.setId(id);
        e.setNombre(nombre);
        e.setDescripcion("Descripción de " + nombre);
        return e;
    }
}
