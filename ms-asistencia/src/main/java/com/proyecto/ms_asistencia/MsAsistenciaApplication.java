package com.proyecto.ms_asistencia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:.env")
public class MsAsistenciaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsAsistenciaApplication.class, args);
    }
}
