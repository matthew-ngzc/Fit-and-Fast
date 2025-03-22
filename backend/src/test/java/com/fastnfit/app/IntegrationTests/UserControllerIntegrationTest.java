package com.fastnfit.app.IntegrationTests;

import com.fastnfit.app.controller.AuthUtils;
import com.fastnfit.app.controller.UserController;
import com.fastnfit.app.dto.UserDetailsDTO;
import com.fastnfit.app.enums.FitnessLevel;
import com.fastnfit.app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthUtils authUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDetailsDTO userDetailsDTO;
    private final Long TEST_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        // Setup test data
        userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setUserId(TEST_USER_ID);
        userDetailsDTO.setUsername("testuser");
        userDetailsDTO.setDob(new Date());
        userDetailsDTO.setHeight(170.0);
        userDetailsDTO.setWeight(65.0);
        userDetailsDTO.setPregnancyStatus("not_pregnant");
        userDetailsDTO.setWorkoutGoal("weight_loss");
        userDetailsDTO.setWorkoutDays(3);
        userDetailsDTO.setFitnessLevel(FitnessLevel.BEGINNER);
        userDetailsDTO.setMenstrualCramps(false);
        userDetailsDTO.setCycleBasedRecommendations(true);
        userDetailsDTO.setWorkoutType("cardio");
        userDetailsDTO.setCurrentStreak(5);
        userDetailsDTO.setLongestStreak(10);

        // Mock the auth utils
        Mockito.when(authUtils.getCurrentUserId()).thenReturn(TEST_USER_ID);
    }

    @Test
    @WithMockUser(username = "1")
    void testGetUserDetails_Success() throws Exception {
        Mockito.when(userService.getUserDetails(TEST_USER_ID))
                .thenReturn(userDetailsDTO);

        mockMvc.perform(get("/api/users/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.workoutGoal").value("weight_loss"))
                .andExpect(jsonPath("$.fitnessLevel").value("BEGINNER"))
                .andExpect(jsonPath("$.currentStreak").value(5))
                .andExpect(jsonPath("$.longestStreak").value(10));
    }

    @Test
    @WithMockUser(username = "1")
    void testGetUserDetails_NotFound() throws Exception {
        Mockito.when(userService.getUserDetails(TEST_USER_ID))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/api/users/details"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "1")
    void testCompleteQuestionnaire_Success() throws Exception {
        Mockito.when(userService.completeUserQuestionnaire(eq(TEST_USER_ID), any(UserDetailsDTO.class)))
                .thenReturn(userDetailsDTO);

        mockMvc.perform(post("/api/users/questionnaire")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDetailsDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.workoutGoal").value("weight_loss"));
    }

    @Test
    @WithMockUser(username = "1")
    void testCompleteQuestionnaire_BadRequest() throws Exception {
        Mockito.when(userService.completeUserQuestionnaire(eq(TEST_USER_ID), any(UserDetailsDTO.class)))
                .thenThrow(new RuntimeException("Invalid questionnaire data"));

        mockMvc.perform(post("/api/users/questionnaire")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDetailsDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "1")
    void testUpdateUserDetails_Success() throws Exception {
        Mockito.when(userService.updateUserDetails(eq(TEST_USER_ID), any(UserDetailsDTO.class)))
                .thenReturn(userDetailsDTO);

        mockMvc.perform(put("/api/users/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDetailsDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.workoutGoal").value("weight_loss"));
    }

    @Test
    @WithMockUser(username = "1")
    void testUpdateUserDetails_NotFound() throws Exception {
        Mockito.when(userService.updateUserDetails(eq(TEST_USER_ID), any(UserDetailsDTO.class)))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(put("/api/users/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDetailsDTO)))
                .andExpect(status().isNotFound());
    }
}