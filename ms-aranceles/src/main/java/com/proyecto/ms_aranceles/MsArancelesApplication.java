package com.proyecto.ms_aranceles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:.env")
public class MsArancelesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsArancelesApplication.class, args);
	}

}
