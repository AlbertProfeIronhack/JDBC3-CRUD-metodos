package com.ironhack.videojuegos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VideojuegosApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideojuegosApplication.class, args);

		GestorVj consulta1 = new GestorVj();

		consulta1.gestorJuegos();
	
	}

}
