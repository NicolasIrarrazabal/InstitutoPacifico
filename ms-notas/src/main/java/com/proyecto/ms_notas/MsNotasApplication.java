package com.proyecto.ms_notas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:.env")
public class MsNotasApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsNotasApplication.class, args);
    }
}
