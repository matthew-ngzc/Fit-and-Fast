package com.fastnfit.app.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

    /**
     * Get the current authenticated user's ID
     * @return the user ID as a Long
     * @throws NumberFormatException if the authentication name cannot be parsed as a Long
     * @throws IllegalStateException if no authentication is present
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("No authentication found in SecurityContext");
        }
        return Long.parseLong(authentication.getName());
    }
}