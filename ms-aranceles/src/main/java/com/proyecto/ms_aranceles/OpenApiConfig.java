package com.proyecto.ms_aranceles;

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
                        .title("ms-aranceles")
                        .description("Microservicio de gestión de aranceles - Instituto Pacífico. " +
                                "Gestiona pagos, deudas y validación de la regla R4 (deuda vencida).")
                        .version("1.0.0"))
                .servers(List.of(new Server().url("http://localhost:8081").description("Local")));
    }
}
