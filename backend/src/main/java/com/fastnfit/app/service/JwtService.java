package com.fastnfit.app.service;

import com.fastnfit.app.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final JwtConfig jwtConfig;
    
    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }

    public String generateToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}