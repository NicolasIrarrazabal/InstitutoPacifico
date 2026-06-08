package com.proyecto.ms_notas;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ms-notas")
                        .description("Microservicio de gestión de notas - Instituto Pacífico. " +
                                "Gestiona el registro de notas, promedios ponderados y evaluación de las reglas R3 y R5.")
                        .version("1.0.0"))
                .servers(List.of(new Server().url("http://localhost:8086").description("Local")));
    }
}
