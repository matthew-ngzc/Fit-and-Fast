package com.fastnfit.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class JwtConfig {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    //Getters
    public String getSecret(){
        return secret;
    }

    public long getExpiration(){
        return expiration;
    }
}
