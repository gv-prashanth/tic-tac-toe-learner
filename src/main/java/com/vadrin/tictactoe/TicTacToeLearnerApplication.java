package com.vadrin.tictactoe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.vadrin")
public class TicTacToeLearnerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicTacToeLearnerApplication.class, args);
	}

}
