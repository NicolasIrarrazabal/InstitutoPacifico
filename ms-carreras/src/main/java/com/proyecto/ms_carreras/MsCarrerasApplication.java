package com.proyecto.ms_carreras;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:.env")
public class MsCarrerasApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsCarrerasApplication.class, args);
    }
}