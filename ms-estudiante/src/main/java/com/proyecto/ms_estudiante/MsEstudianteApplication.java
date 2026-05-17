package com.proyecto.ms_estudiante;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:.env")
public class MsEstudianteApplication {
	public static void main(String[] args) {
		SpringApplication.run(MsEstudianteApplication.class, args);
	}

}
