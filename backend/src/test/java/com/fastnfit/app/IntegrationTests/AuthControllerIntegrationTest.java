package com.fastnfit.app.IntegrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastnfit.app.dto.AuthResponseDTO;
import com.fastnfit.app.dto.LoginRequestDTO;
import com.fastnfit.app.dto.UserRegistrationDTO;
import com.fastnfit.app.model.User;
import com.fastnfit.app.repository.UserRepository;
import com.fastnfit.app.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Use test profile
@Transactional // Rollback transactions after each test
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_USERNAME = "testuser";

    @BeforeEach
    public void setup() {
        // Clear existing test user if exists
        userRepository.findByEmail(TEST_EMAIL).ifPresent(user -> userRepository.delete(user));
    }

    @Test
    public void testSignup_Success() throws Exception {
        // Create registration DTO
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
        registrationDTO.setEmail(TEST_EMAIL);
        registrationDTO.setPassword(TEST_PASSWORD);
        registrationDTO.setUsername(TEST_USERNAME);

        // Perform signup request
        MvcResult result = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        // Parse response
        AuthResponseDTO response = objectMapper.readValue(
                result.getResponse().getContentAsString(), 
                AuthResponseDTO.class);

        // Verify user exists in database
        User savedUser = userRepository.findByEmail(TEST_EMAIL).orElse(null);
        assertNotNull(savedUser, "User should be saved in database");
        assertEquals(TEST_EMAIL, savedUser.getEmail(), "Email should match");
        
        // Verify token is valid
        assertTrue(jwtService.validateToken(response.getToken()), "Token should be valid");
        assertEquals(savedUser.getUserId(), jwtService.extractUserId(response.getToken()), 
                "Token should contain correct user ID");
    }

    @Test
    public void testSignup_DuplicateEmail() throws Exception {
        // Create a user with the test email
        User existingUser = new User();
        existingUser.setEmail(TEST_EMAIL);
        existingUser.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        userRepository.save(existingUser);

        // Create registration DTO with the same email
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
        registrationDTO.setEmail(TEST_EMAIL);
        registrationDTO.setPassword("differentPassword");
        registrationDTO.setUsername("differentUsername");

        // Perform signup request - should fail
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testLogin_Success() throws Exception {
        // Create a user for login test
        User user = new User();
        user.setEmail(TEST_EMAIL);
        user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        userRepository.save(user);

        // Create login request
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword(TEST_PASSWORD);

        // Perform login request
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        // Parse response
        AuthResponseDTO response = objectMapper.readValue(
                result.getResponse().getContentAsString(), 
                AuthResponseDTO.class);

        // Verify token is valid
        assertTrue(jwtService.validateToken(response.getToken()), "Token should be valid");
        assertEquals(user.getUserId(), jwtService.extractUserId(response.getToken()), 
                "Token should contain correct user ID");
    }

    //@Test
    public void testLogin_WrongEmail() throws Exception {
        // Create login request with non-existent email
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword(TEST_PASSWORD);

        // Perform login request - should fail
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLogin_WrongPassword() throws Exception {
        // Create a user for login test
        User user = new User();
        user.setEmail(TEST_EMAIL);
        user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        userRepository.save(user);

        // Create login request with wrong password
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword("wrongPassword");

        // Perform login request - should fail
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLogin_EmptyCredentials() throws Exception {
        // Create login request with empty credentials
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("");
        loginRequest.setPassword("");

        // Perform login request - should fail
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}