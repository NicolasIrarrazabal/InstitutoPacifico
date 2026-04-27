package com.proyecto.ms_asignaturas.controller;

import com.proyecto.ms_asignaturas.model.Credito;
import com.proyecto.ms_asignaturas.service.CreditoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/creditos")
@RequiredArgsConstructor
public class CreditoController {

    private final CreditoService creditoService;

    @GetMapping()
    public ResponseEntity<List<Credito>> listar() {
        return ResponseEntity.ok(creditoService.listarTodos());
    }

    @PostMapping()
    public ResponseEntity<Credito> crearCredito(@Valid @RequestBody Credito credito) {
        return new ResponseEntity<>(creditoService.guardar(credito), HttpStatus.CREATED);
    }
}
