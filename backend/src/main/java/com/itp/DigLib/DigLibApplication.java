package com.itp.DigLib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.itp.DigLib.api.controller.GetController;

/**
 * The main entry point for the DigLib application.
 */
@SpringBootApplication
public class DigLibApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(GetController.class);

	/**
	 * The main method that starts the Spring Boot application.
	 *
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		LOGGER.info("Application start!!!!!!");
		SpringApplication.run(DigLibApplication.class, args);
	}
}
