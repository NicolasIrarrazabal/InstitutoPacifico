package com.proyecto.ms_practicas.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

// Llama a ms-empresas
// Endpoint esperado: GET /api/v1/empresas/{id}/tiene-convenio-vigente
// Respuesta esperada: { "tieneConvenioVigente": true/false }
// La R5 exige que la empresa donde el estudiante hará su práctica
// tenga un convenio activo firmado con el instituto.
@Service
@Slf4j
@RequiredArgsConstructor
public class EmpresaClientService {

    private final RestTemplate restTemplate;

    @Value("${ms-empresas.url}")
    private String msEmpresasUrl;

    public boolean empresaTieneConvenioVigente(UUID empresaId) {
        log.info("Consultando ms-empresas si empresa {} tiene convenio vigente", empresaId);
        String url = msEmpresasUrl + "/api/v1/empresas/" + empresaId + "/tiene-convenio-vigente";
        try {
            ConvenioVigenteResponse response = restTemplate.getForObject(url, ConvenioVigenteResponse.class);
            if (response == null || response.tieneConvenioVigente() == null) {
                log.warn("ms-empresas devolvió respuesta nula para empresa {}", empresaId);
                return false;
            }
            return response.tieneConvenioVigente();
        } catch (Exception e) {
            log.error("Error al consultar ms-empresas: {}", e.getMessage());
            throw new IllegalStateException("No se pudo verificar el convenio de la empresa: " + e.getMessage());
        }
    }

    // Record interno para mapear la respuesta de ms-empresas
    public record ConvenioVigenteResponse(Boolean tieneConvenioVigente) {}
}
