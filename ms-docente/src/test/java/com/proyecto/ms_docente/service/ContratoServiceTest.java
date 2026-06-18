package com.proyecto.ms_docente.service;

import com.proyecto.ms_docente.model.Contrato;
import com.proyecto.ms_docente.model.Docente;
import com.proyecto.ms_docente.repository.ContratoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContratoServiceTest {

    @Mock
    private ContratoRepository repository;

    @InjectMocks
    private ContratoService service;

    @Test
    void listarTodos_retornaListaCompleta() {
        when(repository.findAll()).thenReturn(List.of(contrato()));

        List<Contrato> resultado = service.listarTodos();

        assertThat(resultado).hasSize(1);
        verify(repository).findAll();
    }

    @Test
    void guardar_delegaAlRepositorioYRetornaContrato() {
        Contrato c = contrato();
        when(repository.save(any(Contrato.class))).thenReturn(c);

        Contrato resultado = service.guardar(c);

        assertThat(resultado).isNotNull();
        verify(repository).save(c);
    }

    // ── helper ───────────────────────────────────────────────────────

    private Contrato contrato() {
        Docente d = new Docente();
        d.setId(UUID.randomUUID());
        d.setNombre("Juan");
        d.setApellido("Pérez");
        d.setEmail("juan@mail.com");

        Contrato c = new Contrato();
        c.setId(UUID.randomUUID());
        c.setTipoContrato("INDEFINIDO");
        c.setFechaInicio(LocalDate.now());
        c.setSueldoBase(new BigDecimal("1500000"));
        c.setDocente(d);
        return c;
    }
}
