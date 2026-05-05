package com.proyecto.ms_matriculas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:.env")
public class MsMatriculasApplication {

	public static void main(String[] args) {//
		SpringApplication.run(MsMatriculasApplication.class, args);
	}

}
