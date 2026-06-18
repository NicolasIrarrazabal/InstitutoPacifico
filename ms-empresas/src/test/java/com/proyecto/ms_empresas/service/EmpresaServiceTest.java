package com.proyecto.ms_empresas.service;

import com.proyecto.ms_empresas.dto.EmpresaDTO;
import com.proyecto.ms_empresas.model.Empresa;
import com.proyecto.ms_empresas.repository.EmpresaRepository;
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
class EmpresaServiceTest {

    @Mock
    private EmpresaRepository repository;

    @InjectMocks
    private EmpresaService service;

    // ── findAll ──────────────────────────────────────────────────────

    @Test
    void findAll_retornaListaEmpresas() {
        when(repository.findAll()).thenReturn(List.of(empresa(UUID.randomUUID(), "12345678-9", "ACTIVO")));

        List<Empresa> resultado = service.findAll();

        assertThat(resultado).hasSize(1);
        verify(repository).findAll();
    }

    // ── findById ─────────────────────────────────────────────────────

    @Test
    void findById_cuandoExiste_retornaEmpresa() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(empresa(id, "12345678-9", "ACTIVO")));

        Empresa resultado = service.findById(id);

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

    // ── save ─────────────────────────────────────────────────────────

    @Test
    void save_caminoFeliz_guardaEmpresaConEstadoActivo() {
        EmpresaDTO dto = empresaDTO("12345678-9");
        Empresa guardada = empresa(UUID.randomUUID(), "12345678-9", "ACTIVO");

        when(repository.existsByRut("12345678-9")).thenReturn(false);
        when(repository.save(any(Empresa.class))).thenReturn(guardada);

        Empresa resultado = service.save(dto);

        assertThat(resultado.getEstado()).isEqualTo("ACTIVO");
        verify(repository).save(any(Empresa.class));
    }

    @Test
    void save_cuandoRutDuplicado_lanzaIllegalArgumentException() {
        EmpresaDTO dto = empresaDTO("12345678-9");
        when(repository.existsByRut("12345678-9")).thenReturn(true);

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("RUT");

        verify(repository, never()).save(any());
    }

    // ── update ───────────────────────────────────────────────────────

    @Test
    void update_cuandoActivo_actualizaTodosLosCampos() {
        UUID id = UUID.randomUUID();
        Empresa existente = empresa(id, "12345678-9", "ACTIVO");
        EmpresaDTO dto = empresaDTO("12345678-9");

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Empresa.class))).thenReturn(existente);

        Empresa resultado = service.update(id, dto);

        assertThat(resultado).isNotNull();
        verify(repository).save(existente);
    }

    @Test
    void update_cuandoInactivo_lanzaIllegalStateException() {
        UUID id = UUID.randomUUID();
        Empresa inactiva = empresa(id, "12345678-9", "INACTIVO");

        when(repository.findById(id)).thenReturn(Optional.of(inactiva));

        assertThatThrownBy(() -> service.update(id, empresaDTO("12345678-9")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("INACTIVO");

        verify(repository, never()).save(any());
    }

    @Test
    void update_cuandoCambiaRutYNuevoRutEnUso_lanzaIllegalArgumentException() {
        UUID id = UUID.randomUUID();
        Empresa existente = empresa(id, "11111111-1", "ACTIVO");
        EmpresaDTO dto = empresaDTO("22222222-2");

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.existsByRut("22222222-2")).thenReturn(true);

        assertThatThrownBy(() -> service.update(id, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("RUT");

        verify(repository, never()).save(any());
    }

    // ── delete ───────────────────────────────────────────────────────

    @Test
    void delete_cuandoActivo_marcaComoInactivo() {
        UUID id = UUID.randomUUID();
        Empresa existente = empresa(id, "12345678-9", "ACTIVO");

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Empresa.class))).thenReturn(existente);

        service.delete(id);

        assertThat(existente.getEstado()).isEqualTo("INACTIVO");
        verify(repository).save(existente);
    }

    @Test
    void delete_cuandoYaInactivo_lanzaIllegalStateException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(empresa(id, "12345678-9", "INACTIVO")));

        assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("INACTIVO");

        verify(repository, never()).save(any());
    }

    // ── tieneConvenioVigente ─────────────────────────────────────────

    @Test
    void tieneConvenioVigente_cuandoActivoYFechaVigente_retornaTrue() {
        UUID id = UUID.randomUUID();
        Empresa e = empresa(id, "12345678-9", "ACTIVO");
        e.setFechaInicioConvenio(LocalDate.now().minusMonths(1));
        e.setFechaFinConvenio(LocalDate.now().plusMonths(1));

        when(repository.findById(id)).thenReturn(Optional.of(e));

        boolean resultado = service.tieneConvenioVigente(id);

        assertThat(resultado).isTrue();
    }

    @Test
    void tieneConvenioVigente_cuandoInactivo_retornaFalse() {
        UUID id = UUID.randomUUID();
        Empresa e = empresa(id, "12345678-9", "INACTIVO");
        e.setFechaInicioConvenio(LocalDate.now().minusDays(1));
        e.setFechaFinConvenio(LocalDate.now().plusDays(1));

        when(repository.findById(id)).thenReturn(Optional.of(e));

        boolean resultado = service.tieneConvenioVigente(id);

        assertThat(resultado).isFalse();
    }

    @Test
    void tieneConvenioVigente_cuandoFechasNulas_retornaFalse() {
        UUID id = UUID.randomUUID();
        Empresa e = empresa(id, "12345678-9", "ACTIVO");
        // fechas nulas

        when(repository.findById(id)).thenReturn(Optional.of(e));

        boolean resultado = service.tieneConvenioVigente(id);

        assertThat(resultado).isFalse();
    }

    @Test
    void tieneConvenioVigente_cuandoConvenioVencido_retornaFalse() {
        UUID id = UUID.randomUUID();
        Empresa e = empresa(id, "12345678-9", "ACTIVO");
        e.setFechaInicioConvenio(LocalDate.now().minusYears(2));
        e.setFechaFinConvenio(LocalDate.now().minusDays(1));

        when(repository.findById(id)).thenReturn(Optional.of(e));

        boolean resultado = service.tieneConvenioVigente(id);

        assertThat(resultado).isFalse();
    }

    @Test
    void tieneConvenioVigente_cuandoConvenioFuturo_retornaFalse() {
        UUID id = UUID.randomUUID();
        Empresa e = empresa(id, "12345678-9", "ACTIVO");
        e.setFechaInicioConvenio(LocalDate.now().plusDays(5));
        e.setFechaFinConvenio(LocalDate.now().plusMonths(6));

        when(repository.findById(id)).thenReturn(Optional.of(e));

        boolean resultado = service.tieneConvenioVigente(id);

        assertThat(resultado).isFalse();
    }

    // ── helpers ──────────────────────────────────────────────────────

    private Empresa empresa(UUID id, String rut, String estado) {
        Empresa e = new Empresa();
        e.setId(id);
        e.setNombre("Empresa Test");
        e.setRut(rut);
        e.setRubro("Tecnología");
        e.setDireccion("Av. Test 123");
        e.setTelefono("+56912345678");
        e.setEmailContacto("contacto@empresa.cl");
        e.setNombreContacto("Contacto Test");
        e.setEstado(estado);
        return e;
    }

    private EmpresaDTO empresaDTO(String rut) {
        return new EmpresaDTO(
                "Empresa Test", rut, "Tecnología",
                "Av. Test 123", "+56912345678",
                "contacto@empresa.cl", "Contacto Test",
                LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(11)
        );
    }
}
