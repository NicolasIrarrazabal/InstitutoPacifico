package com.proyecto.ms_gateway;

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
                        .title("ms-gateway")
                        .description("API Gateway - Instituto Pacífico. " +
                                "Centraliza y administra el enrutamiento hacia los 10 microservicios del sistema.")
                        .version("1.0.0"))
                .servers(List.of(new Server().url("http://localhost:9000").description("Local")));
    }
}
