package com.adventurebookapp.adventurebook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AdventureBookApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdventureBookApplication.class, args);
	}

}
