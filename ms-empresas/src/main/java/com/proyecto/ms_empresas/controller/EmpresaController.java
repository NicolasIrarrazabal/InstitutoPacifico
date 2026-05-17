package com.proyecto.ms_empresas.controller;

import com.proyecto.ms_empresas.dto.EmpresaDTO;
import com.proyecto.ms_empresas.model.Empresa;
import com.proyecto.ms_empresas.service.EmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService service;

    @GetMapping
    public ResponseEntity<List<Empresa>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empresa> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Empresa> create(@Valid @RequestBody EmpresaDTO dto) {
        Empresa creada = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empresa> update(@PathVariable UUID id,
                                          @Valid @RequestBody EmpresaDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of("mensaje", "Empresa desactivada correctamente"));
    }

    // R5: ms-practicas llama a esta api para saber si el convenio esta vigente
    @GetMapping("/{id}/tiene-convenio-vigente")
    public ResponseEntity<Map<String, Boolean>> tieneConvenioVigente(@PathVariable UUID id) {
        boolean resultado = service.tieneConvenioVigente(id);
        return ResponseEntity.ok(Map.of("tieneConvenioVigente", resultado));
    }
}
