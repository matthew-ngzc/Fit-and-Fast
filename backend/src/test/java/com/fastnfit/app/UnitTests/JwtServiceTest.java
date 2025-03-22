package com.fastnfit.app.UnitTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fastnfit.app.config.JwtConfig;
import com.fastnfit.app.service.JwtService;

import io.jsonwebtoken.Claims;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtConfig jwtConfig;

    private JwtService jwtService;
    private static final String SECRET_KEY = "thisIsATestSecretKeyThatIsLongEnoughForHS256Algorithm";
    private static final long EXPIRATION_TIME = 3600000; // 1 hour in milliseconds
    private static final Long USER_ID = 123L;

    @BeforeEach
    void setUp() {
        // Set up common mocks before each test
        lenient().when(jwtConfig.getSecret()).thenReturn(SECRET_KEY);
        lenient().when(jwtConfig.getExpiration()).thenReturn(EXPIRATION_TIME);
        jwtService = new JwtService(jwtConfig);
    }

    @Test
    void shouldGenerateTokenWithUserId() {
        // When
        String token = jwtService.generateToken(USER_ID);
        
        // Then
        assertNotNull(token);
        assertEquals(USER_ID, jwtService.extractUserId(token));
    }

    @Test
    void shouldExtractAllClaims() {
        // Given
        String token = jwtService.generateToken(USER_ID);
        
        // When
        Claims claims = jwtService.extractAllClaims(token);

        // Then
        assertNotNull(claims);
        assertEquals(USER_ID, claims.get("userId", Long.class));
    }

    @Test
    void shouldExtractUserId() {
        // Given
        String token = jwtService.generateToken(USER_ID);
        
        // When
        Long extractedUserId = jwtService.extractUserId(token);

        // Then
        assertEquals(USER_ID, extractedUserId);
    }

    @Test
    void shouldExtractExpiration() {
        // Given
        String token = jwtService.generateToken(USER_ID);
        long currentTimeMillis = System.currentTimeMillis();

        // When
        Date expiration = jwtService.extractExpiration(token);

        // Then
        assertNotNull(expiration);
        assertTrue(expiration.getTime() > currentTimeMillis);
        assertTrue(expiration.getTime() <= currentTimeMillis + EXPIRATION_TIME + 1000); // Adding 1 second tolerance
    }

    @Test
    void shouldValidateValidToken() {
        // Given
        String token = jwtService.generateToken(USER_ID);
        
        // When
        boolean isValid = jwtService.validateToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void shouldRejectExpiredToken() throws Exception {
        // Create a new mock configuration just for this test
        JwtConfig expiredConfig = Mockito.mock(JwtConfig.class);
        when(expiredConfig.getSecret()).thenReturn(SECRET_KEY);
        when(expiredConfig.getExpiration()).thenReturn(1L);

        // Create a new service with the expired config
        JwtService expiredJwtService = new JwtService(expiredConfig);

        // Generate token with very short expiration
        String token = expiredJwtService.generateToken(USER_ID);

        // Wait to ensure token expiration
        TimeUnit.MILLISECONDS.sleep(10);

        // When
        boolean isValid = expiredJwtService.validateToken(token);

        // Then
        assertFalse(isValid);
    }

    @Test
    void shouldRejectInvalidToken() {
        // Given
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        
        // When
        boolean isValid = jwtService.validateToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void shouldHandleTokenWithInvalidSignature() {
        // Given
        String token = jwtService.generateToken(USER_ID);
        String tamperedToken = token.substring(0, token.lastIndexOf('.') + 1) + "tamperedSignature";
        
        // When
        boolean isValid = jwtService.validateToken(tamperedToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void shouldHandleNullToken() {
        // When
        boolean isValid = jwtService.validateToken(null);

        // Then
        assertFalse(isValid);
    }

    @Test
    void shouldHandleEmptyToken() {
        // When
        boolean isValid = jwtService.validateToken("");

        // Then
        assertFalse(isValid);
    }
}