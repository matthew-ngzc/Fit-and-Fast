// WorkoutApplication.java
package com.fastnfit.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fastnfit.app.config.AwsConfig;
import com.fastnfit.app.config.DotenvBootstrap;
import com.fastnfit.app.config.JwtConfig;

@SpringBootApplication
@EnableConfigurationProperties({JwtConfig.class, AwsConfig.class})
public class WorkoutApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkoutApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*");
            }
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
