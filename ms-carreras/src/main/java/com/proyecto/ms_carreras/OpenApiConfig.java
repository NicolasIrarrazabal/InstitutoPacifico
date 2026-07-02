package com.proyecto.ms_carreras;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ms-carreras")
                        .description("Microservicio de gestión de carreras - Instituto Pacífico. " +
                                "Gestiona el catálogo de carreras disponibles en el instituto.")
                        .version("1.0.0"));
    }
}
