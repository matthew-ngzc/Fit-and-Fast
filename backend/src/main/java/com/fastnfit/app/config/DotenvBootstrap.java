package com.fastnfit.app.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DotenvBootstrap {

    private final ConfigurableEnvironment environment;

    public DotenvBootstrap(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void init() {
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            Map<String, Object> envMap = new HashMap<>();

            dotenv.entries().forEach(entry -> envMap.put(entry.getKey(), entry.getValue()));

            // Add to Spring Boot environment
            environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", envMap));

            System.out.println("✅ Successfully loaded environment variables from .env file");
        } catch (Exception e) {
            System.out.println("⚠️ Warning: Could not load .env file: " + e.getMessage());
        }
    }
}
