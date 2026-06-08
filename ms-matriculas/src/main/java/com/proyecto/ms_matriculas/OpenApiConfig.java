package com.proyecto.ms_matriculas;

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
                        .title("ms-matriculas")
                        .description("Microservicio de gestión de matrículas - Instituto Pacífico. " +
                                "Gestiona la matriculación de estudiantes validando la regla R1 (prerrequisitos).")
                        .version("1.0.0"))
                .servers(List.of(new Server().url("http://localhost:8083").description("Local")));
    }
}
