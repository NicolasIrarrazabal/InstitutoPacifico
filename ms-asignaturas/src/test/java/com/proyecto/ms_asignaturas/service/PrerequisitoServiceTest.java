package com.proyecto.ms_asignaturas.service;

import com.proyecto.ms_asignaturas.model.Asignatura;
import com.proyecto.ms_asignaturas.model.Prerequisito;
import com.proyecto.ms_asignaturas.repository.PrerequisitoRepository;
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
class PrerequisitoServiceTest {

    @Mock
    private PrerequisitoRepository prerequisitoRepository;

    @InjectMocks
    private PrerequisitoService service;

    @Test
    void listarPorAsignatura_delegaAlRepositorio() {
        UUID asignaturaId = UUID.randomUUID();
        when(prerequisitoRepository.findByAsignaturaPrincipalId(asignaturaId))
                .thenReturn(List.of());

        List<Prerequisito> resultado = service.listarPorAsignatura(asignaturaId);

        assertThat(resultado).isEmpty();
        verify(prerequisitoRepository).findByAsignaturaPrincipalId(asignaturaId);
    }

    @Test
    void listarPorAsignatura_retornaListaConPrerequisitos() {
        UUID asignaturaId = UUID.randomUUID();
        Prerequisito p = prerequisito(asignaturaId);
        when(prerequisitoRepository.findByAsignaturaPrincipalId(asignaturaId))
                .thenReturn(List.of(p));

        List<Prerequisito> resultado = service.listarPorAsignatura(asignaturaId);

        assertThat(resultado).hasSize(1);
    }

    @Test
    void asignarPrerequisito_guardaYRetornaPrerequisito() {
        Prerequisito p = prerequisito(UUID.randomUUID());
        when(prerequisitoRepository.save(any(Prerequisito.class))).thenReturn(p);

        Prerequisito resultado = service.asignarPrerequisito(p);

        assertThat(resultado).isNotNull();
        verify(prerequisitoRepository).save(p);
    }

    // ── helpers ──────────────────────────────────────────────────────

    private Prerequisito prerequisito(UUID asignaturaId) {
        Asignatura principal = new Asignatura();
        principal.setId(asignaturaId);
        principal.setNombre("Cálculo II");

        Asignatura requisito = new Asignatura();
        requisito.setId(UUID.randomUUID());
        requisito.setNombre("Cálculo I");

        Prerequisito p = new Prerequisito();
        p.setId(UUID.randomUUID());
        p.setAsignaturaPrincipal(principal);
        p.setAsignaturaRequisito(requisito);
        return p;
    }
}
