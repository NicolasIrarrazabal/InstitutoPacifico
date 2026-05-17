package com.proyecto.ms_empresas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:.env")
public class MsEmpresasApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsEmpresasApplication.class, args);
    }
}
