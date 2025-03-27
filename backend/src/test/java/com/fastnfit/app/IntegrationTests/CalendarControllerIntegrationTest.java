package com.fastnfit.app.IntegrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastnfit.app.dto.CycleUpdateDTO;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.UserDetails;
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

import java.time.LocalDate;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CalendarControllerIntegrationTest {

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

    private String authToken;
    private User testUser;

    @BeforeEach
    public void setup() throws Exception {
        // Clear existing test user if exists
        userRepository.findByEmail(TEST_EMAIL).ifPresent(user -> userRepository.delete(user));

        // Create a test user and get authentication token
        testUser = new User();
        testUser.setEmail(TEST_EMAIL);
        testUser.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        
        // Create UserDetails for the user
        UserDetails userDetails = new UserDetails();
        userDetails.setUser(testUser);
        testUser.setUserDetails(userDetails);
        
        userRepository.save(testUser);

        // Authenticate and get token
        String loginPayload = objectMapper.writeValueAsString(
            Map.of("email", TEST_EMAIL, "password", TEST_PASSWORD)
        );

        MvcResult result = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginPayload))
            .andExpect(status().isOk())
            .andReturn();

        authToken = objectMapper.readValue(
            result.getResponse().getContentAsString(), 
            Map.class
        ).get("token").toString();
    }

    @Test
    public void testGetWorkoutDatesForMonth() throws Exception {
        mockMvc.perform(get("/api/calendar/workout-dates")
            .header("Authorization", "Bearer " + authToken)
            .param("year", "2024")
            .param("month", "3"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testUpdateCycleInfo() throws Exception {
        CycleUpdateDTO cycleUpdateDTO = new CycleUpdateDTO();
        cycleUpdateDTO.setCycleLength(30);
        cycleUpdateDTO.setPeriodLength(5);
        cycleUpdateDTO.setLastPeriodStartDate(LocalDate.now().minusDays(10));

        mockMvc.perform(put("/api/calendar/update-cycle")
            .header("Authorization", "Bearer " + authToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cycleUpdateDTO)))
            .andExpect(status().isOk());

        // Verify the update by fetching cycle info
        MvcResult result = mockMvc.perform(get("/api/calendar/cycle-info")
            .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isOk())
            .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assertTrue(responseContent.contains("\"cycleLength\":30"));
        assertTrue(responseContent.contains("\"periodLength\":5"));
    }

    @Test
    public void testGetCycleInfo() throws Exception {
        mockMvc.perform(get("/api/calendar/cycle-info")
            .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cycleLength").exists())
            .andExpect(jsonPath("$.periodLength").exists())
            .andExpect(jsonPath("$.currentPhase").exists())
            .andExpect(jsonPath("$.daysUntilNextPeriod").exists());
    }

    @Test
    public void testGetWorkoutDates_UnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/calendar/workout-dates")
            .param("year", "2024")
            .param("month", "3"))
            .andExpect(status().isUnauthorized());
    }
}