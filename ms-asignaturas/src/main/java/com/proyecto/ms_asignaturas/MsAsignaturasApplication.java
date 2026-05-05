package com.proyecto.ms_asignaturas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:.env")
public class MsAsignaturasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsAsignaturasApplication.class, args);
	}

}
