package com.proyecto.ms_asignaturas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
<<<<<<< HEAD

@SpringBootApplication
=======
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:.env")
>>>>>>> fa0c9f7d3e1e5d3ade7459411904e5793176644e
public class MsAsignaturasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsAsignaturasApplication.class, args);
	}

}
