package com.proyecto.ms_notas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

// @SpringBootApplication: marca esta clase como el punto de entrada de Spring Boot
// @PropertySource: le dice a Spring que cargue el archivo .env con las variables de entorno
@SpringBootApplication
@PropertySource("classpath:.env")
public class MsNotasApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsNotasApplication.class, args);
    }
}
