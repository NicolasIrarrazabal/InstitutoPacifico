package com.proyecto.ms_carreras;

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
                        .title("ms-carreras")
                        .description("Microservicio de gestión de carreras - Instituto Pacífico. " +
                                "Gestiona el catálogo de carreras disponibles en el instituto.")
                        .version("1.0.0"))
                .servers(List.of(
                        new Server().url("https://institutopacifico-ms-carreras.onrender.com").description("Render (producción)"),
                        new Server().url("http://localhost:8084").description("Local")
                ));
    }
}
