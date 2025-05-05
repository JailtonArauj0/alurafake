package br.com.alura.AluraFake;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;

@SpringBootApplication
public class AluraFakeApplication {

	public static void main(String[] args) {
		// DEFINI O LOCALE PARA INGLÊS PARA PADRONIZAR AS MENSAGENS DE TRATAMENTO PADRÃO DO SPRING
		Locale.setDefault(Locale.ENGLISH);
		SpringApplication.run(AluraFakeApplication.class, args);
	}

}
