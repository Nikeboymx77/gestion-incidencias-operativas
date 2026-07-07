package com.mx.baz.incidencias;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class GestionIncidenciasOperativasApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionIncidenciasOperativasApplication.class, args);
	}

}
