package com.fastnfit.app.config;

import com.fastnfit.app.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String jwt = authHeader.substring(7);
            Claims claims = jwtService.extractAllClaims(jwt);
            
            if (claims != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Long userId = claims.get("userId", Long.class);
                
                if (userId != null) {
                    // Create a User object with the userId as the username
                    User userDetails = new User(
                            userId.toString(), 
                            "", 
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                    
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, 
                            null, 
                            userDetails.getAuthorities()
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (JwtException e) {
            // Invalid token - do not set authentication
            logger.error("JWT token validation failed: " + e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
}