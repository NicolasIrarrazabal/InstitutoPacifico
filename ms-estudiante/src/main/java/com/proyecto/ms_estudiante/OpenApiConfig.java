package com.proyecto.ms_estudiante;

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
                        .title("ms-estudiante")
                        .description("Microservicio de gestión de estudiantes - Instituto Pacífico. " +
                                "Gestiona el catálogo de estudiantes y su información personal.")
                        .version("1.0.0"))
                .servers(List.of(new Server().url("http://localhost:8085").description("Local")));
    }
}
