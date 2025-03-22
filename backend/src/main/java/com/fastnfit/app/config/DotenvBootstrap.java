package com.fastnfit.app.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvBootstrap {

    @PostConstruct
    public void init() {
        Dotenv dotenv = Dotenv.load();  // Loads `.env` from project root
        dotenv.entries().forEach(entry ->
            System.setProperty(entry.getKey(), entry.getValue())
        );
    }
}
