package com.proyecto.ms_practicas;

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
                        .title("ms-practicas")
                        .description("Microservicio de gestión de prácticas profesionales - Instituto Pacífico. " +
                                "Gestiona la inscripción y seguimiento de prácticas validando la regla R5.")
                        .version("1.0.0"))
                .servers(List.of(new Server().url("http://localhost:8087").description("Local")));
    }
}
