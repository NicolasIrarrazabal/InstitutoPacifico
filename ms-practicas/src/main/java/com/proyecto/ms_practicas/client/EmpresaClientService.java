package com.proyecto.ms_practicas.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Tag(name = "Empresa Client Service", description = "Cliente HTTP para comunicación con ms-empresas (R5)")
@Service
@Slf4j
@RequiredArgsConstructor
public class EmpresaClientService {

    private final RestTemplate restTemplate;

    @Value("${ms-empresas.url}")
    private String msEmpresasUrl;

    @Operation(summary = "Verificar convenio vigente", description = "Verifica si la empresa tiene convenio vigente para la regla R5")
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

    public record ConvenioVigenteResponse(Boolean tieneConvenioVigente) {}
}
