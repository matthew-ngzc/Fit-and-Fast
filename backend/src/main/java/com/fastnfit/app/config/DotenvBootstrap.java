package com.fastnfit.app.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvBootstrap {

    @PostConstruct
    public void init() {
        try {
            // Use the proper methods for this library
            Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()  // Don't fail if file is missing
                .load();
                
            // Set system properties manually
            dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
            );
            //logger.info("Successfully loaded environment variables from .env file");
            // Or: System.out.println("Successfully loaded environment variables from .env file");
            System.out.println("Successfully loaded environment variables from .env file");
        } catch (Exception e) {
            // Log the error but don't crash
            //logger.warn("Failed to load .env file: {}", e.getMessage());
            // Or: System.out.println("Warning: Could not load .env file: " + e.getMessage());
            System.out.println("Warning: Could not load .env file: " + e.getMessage());
        }
    }
}
