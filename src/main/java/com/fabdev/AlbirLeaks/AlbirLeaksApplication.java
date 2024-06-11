package com.fabdev.AlbirLeaks;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AlbirLeaksApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		System.setProperty("GOOGLE_CLIENT_ID", dotenv.get("GOOGLE_CLIENT_ID"));
		System.setProperty("GOOGLE_CLIENT_SECRET", dotenv.get("GOOGLE_CLIENT_SECRET"));
		System.out.println(dotenv.get("GOOGLE_CLIENT_ID"));
		System.out.println(dotenv.get("GOOGLE_CLIENT_SECRET"));
		SpringApplication.run(AlbirLeaksApplication.class, args);
	}

}
