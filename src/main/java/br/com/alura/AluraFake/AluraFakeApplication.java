package br.com.alura.AluraFake;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;

@SpringBootApplication
public class AluraFakeApplication {

	public static void main(String[] args) {
		// PADRONIZEI O LOCALE PARA INGLÊS DEVIDO A MENSAGENS DE TRATAMENTO DE ERRO PADRÃO DO SPRING
		Locale.setDefault(Locale.ENGLISH);
		SpringApplication.run(AluraFakeApplication.class, args);
	}

}
