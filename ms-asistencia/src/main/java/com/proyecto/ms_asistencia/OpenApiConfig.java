package com.proyecto.ms_asistencia;

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
                        .title("ms-asistencia")
                        .description("Microservicio de gestión de asistencia - Instituto Pacífico. " +
                                "Gestiona el registro de asistencia de estudiantes y evalúa la regla R2 (límite de inasistencia).")
                        .version("1.0.0"))
                .servers(List.of(new Server().url("http://localhost:8088").description("Local")));
    }
}
