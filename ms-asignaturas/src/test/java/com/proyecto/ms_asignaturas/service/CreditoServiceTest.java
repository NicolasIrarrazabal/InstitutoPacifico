package com.proyecto.ms_asignaturas.service;

import com.proyecto.ms_asignaturas.model.Credito;
import com.proyecto.ms_asignaturas.repository.CreditoRepository;
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
class CreditoServiceTest {

    @Mock
    private CreditoRepository creditoRepository;

    @InjectMocks
    private CreditoService service;

    @Test
    void listarTodos_delegaAlRepositorio() {
        Credito c = credito(UUID.randomUUID(), 4);
        when(creditoRepository.findAll()).thenReturn(List.of(c));

        List<Credito> resultado = service.listarTodos();

        assertThat(resultado).hasSize(1);
        verify(creditoRepository).findAll();
    }

    @Test
    void guardar_llamaAlRepositorioYRetornaCredito() {
        Credito c = credito(UUID.randomUUID(), 6);
        when(creditoRepository.save(any(Credito.class))).thenReturn(c);

        Credito resultado = service.guardar(c);

        assertThat(resultado.getCantidad()).isEqualTo(6);
        verify(creditoRepository).save(c);
    }

    // ── helper ───────────────────────────────────────────────────────

    private Credito credito(UUID id, int cantidad) {
        Credito c = new Credito();
        c.setId(id);
        c.setCantidad(cantidad);
        return c;
    }
}
