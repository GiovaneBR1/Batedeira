package com.batedeira.projeto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BatedeiraApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatedeiraApplication.class, args);
	}

}
