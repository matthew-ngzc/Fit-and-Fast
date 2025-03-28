package com.fastnfit.app.IntegrationTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastnfit.app.dto.QuestionnaireDTO;
import com.fastnfit.app.dto.UserDetailsDTO;
import com.fastnfit.app.enums.FitnessLevel;
import com.fastnfit.app.enums.PregnancyStatus;
import com.fastnfit.app.enums.WorkoutGoal;
import com.fastnfit.app.enums.WorkoutType;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.UserDetails;
import com.fastnfit.app.repository.UserDetailsRepository;
import com.fastnfit.app.repository.UserRepository;
import com.fastnfit.app.service.JwtService;
import com.fastnfit.app.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Use test profile
@Transactional // Rollback transactions after each test
public class UserControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private UserService userService;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private UserDetailsRepository userDetailsRepository;

        @Autowired
        private JwtService jwtService;

        private User testUser;
        private String authToken;
        private UserDetailsDTO userDetailsDTO;

        @BeforeEach
        public void setup() {
                userDetailsRepository.deleteAll();
                userRepository.deleteAll();
                // Create test user
                testUser = new User();
                testUser.setEmail("user@example.com");
                testUser.setPassword("password123");
                testUser = userService.createUser(testUser,"");

                // Generate JWT token
                authToken = jwtService.generateToken(testUser.getUserId());

                // Create sample UserDetailsDTO for testing
                userDetailsDTO = new UserDetailsDTO();
                userDetailsDTO.setUsername("testuser");
                userDetailsDTO.setDob(new Date(System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000)); // 1 year ago
                userDetailsDTO.setHeight(170.0);
                userDetailsDTO.setWeight(65.0);
                userDetailsDTO.setPregnancyStatus(PregnancyStatus.NO.getValue());
                userDetailsDTO.setWorkoutGoal(WorkoutGoal.GENERAL.getValue());
                userDetailsDTO.setWorkoutDays(4);
                userDetailsDTO.setFitnessLevel(FitnessLevel.Intermediate);
                userDetailsDTO.setMenstrualCramps(false);
                userDetailsDTO.setCycleBasedRecommendations(false);
                userDetailsDTO.setWorkoutType(WorkoutType.HIGH_ENERGY.getValue());
                userDetailsDTO.setCycleLength(5);
                userDetailsDTO.setPeriodLength(10);
                userDetailsDTO.setLastPeriodDate(LocalDate.of(2025, 4, 1));
        }

        @Test
        public void testCompleteQuestionnaire_Success() throws Exception {
                // Perform POST request
                QuestionnaireDTO dto = new QuestionnaireDTO();
                dto.setDob(new Date(System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000)); // 1 year ago
                dto.setHeight(170.0);
                dto.setWeight(65.0);
                dto.setPregnancyStatus(PregnancyStatus.NO.getValue());
                dto.setWorkoutGoal(WorkoutGoal.GENERAL.getValue());
                dto.setWorkoutDays(4);
                dto.setFitnessLevel(FitnessLevel.Intermediate);
                dto.setMenstrualCramps(false);
                dto.setCycleBasedRecommendations(false);
                dto.setWorkoutType(WorkoutType.HIGH_ENERGY.getValue());
                dto.setCycleLength(5);
                dto.setPeriodLength(10);
                dto.setLastPeriodDate(LocalDate.of(2025, 4, 1));
                MvcResult result = mockMvc.perform(post("/api/users/questionnaire")
                                .header("Authorization", "Bearer " + authToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.workoutGoal").value(WorkoutGoal.GENERAL.getValue()))
                                .andExpect(jsonPath("$.workoutDays").value(4))
                                .andReturn();

                // Verify user details saved in database
                Optional<UserDetails> savedDetails = userDetailsRepository.findByUserUserId(testUser.getUserId());
                assertTrue(savedDetails.isPresent(), "User details should be saved in database");
                assertEquals(testUser,savedDetails.get().getUser(), "User should remain the same");
                assertEquals(4, savedDetails.get().getWorkoutDays(), "Workout days should match");
                assertEquals(5,savedDetails.get().getCycleLength(),"Period cycle failed to set");
                assertEquals(10,savedDetails.get().getPeriodLength(),"Period length failed to set");
                assertEquals(dto.getLastPeriodDate(),savedDetails.get().getLastPeriodStartDate(),"Period length failed to set");
        }

        @Test
        public void testCompleteQuestionnaire_Unauthorized() throws Exception {
                // Perform POST request without token
                mockMvc.perform(post("/api/users/questionnaire")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDetailsDTO)))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        public void testGetUserDetails_Success() throws Exception {
                // Create user details first
                UserDetails userDetails = userDetailsRepository.findByUser(testUser).get();
                userDetails.setUsername("testuser");
                userDetails.setDob(new Date(System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000));
                userDetails.setHeight(170.0);
                userDetails.setWeight(65.0);
                userDetails.setPregnancyStatus(PregnancyStatus.NO);
                userDetails.setWorkoutGoal(WorkoutGoal.WEIGHT_LOSS.getValue());
                userDetails.setWorkoutDays(4);
                userDetails.setFitnessLevel(FitnessLevel.Beginner);
                userDetails.setMenstrualCramps(false);
                userDetails.setCycleBasedRecommendations(false);
                userDetails.setWorkoutType(WorkoutType.HIGH_ENERGY);
                userDetails = userDetailsRepository.save(userDetails);

                // Perform GET request
                mockMvc.perform(get("/api/users/details")
                                .header("Authorization", "Bearer " + authToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("testuser"))
                                .andExpect(jsonPath("$.height").value(170.0))
                                .andExpect(jsonPath("$.weight").value(65.0))
                                .andExpect(jsonPath("$.workoutGoal").value("weight-loss"))
                                .andExpect(jsonPath("$.workoutDays").value(4));
        }

        @Test
        public void testGetUserDetails_NotFound() throws Exception {
                // Don't create user details - should result in not found

                // Perform GET request
                mockMvc.perform(get("/api/users/details")
                                .header("Authorization", "Bearer " + authToken))
                                .andExpect(status().isNotFound());
        }

        @Test
        public void testGetUserDetails_Unauthorized() throws Exception {
                // Perform GET request without token
                mockMvc.perform(get("/api/users/details"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        public void testUpdateUserDetails_Success() throws Exception {
                // Create user details first
                UserDetails userDetails = userDetailsRepository.findByUser(testUser).get();
                userDetails.setUsername("initialusername");
                userDetails.setHeight(165.0);
                userDetails.setWeight(60.0);
                userDetails.setWorkoutDays(3);
                userDetails.setPregnancyStatus(PregnancyStatus.NO);
                userDetails.setWorkoutGoal(WorkoutGoal.WEIGHT_LOSS);
                userDetails.setFitnessLevel(FitnessLevel.Beginner);
                userDetails.setWorkoutType(WorkoutType.HIIT);
                userDetailsRepository.save(userDetails);

                // Update details
                UserDetailsDTO updateDTO = new UserDetailsDTO();
                updateDTO.setUsername("updatedusername");
                updateDTO.setWeight(70.0);
                updateDTO.setWorkoutDays(5);
                updateDTO.setFitnessLevel(FitnessLevel.Advanced);
                updateDTO.setWorkoutGoal(WorkoutGoal.STRENGTH_BUILDING.getValue());
                updateDTO.setWorkoutType(WorkoutType.HIGH_ENERGY.getValue());

                // Perform PUT request
                mockMvc.perform(put("/api/users/details")
                                .header("Authorization", "Bearer " + authToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("updatedusername"))
                                .andExpect(jsonPath("$.weight").value(70.0))
                                .andExpect(jsonPath("$.workoutDays").value(5))
                                .andExpect(jsonPath("$.fitnessLevel").value("Advanced"))
                                .andExpect(jsonPath("$.workoutGoal").value(WorkoutGoal.STRENGTH_BUILDING.getValue()))
                                .andExpect(jsonPath("$.workoutType").value(WorkoutType.HIGH_ENERGY.getValue()));

                // Verify updates in database
                userDetails = userDetailsRepository.findByUser(testUser).orElse(null);
                assertNotNull(userDetails, "User details should exist");
                assertEquals("updatedusername", userDetails.getUsername(), "Username should be updated");
                assertEquals(70.0, userDetails.getWeight(), "Weight should be updated");
                assertEquals(5, userDetails.getWorkoutDays(), "Workout days should be updated");
                assertEquals(FitnessLevel.Advanced, userDetails.getFitnessLevel(), "Fitness level should be updated");
                assertEquals(WorkoutGoal.STRENGTH_BUILDING, userDetails.getWorkoutGoal(),
                                "Workout goal should be updated");
                assertEquals(WorkoutType.HIGH_ENERGY, userDetails.getWorkoutType(), "Workout type should be updated");
        }

        @Test
        public void testUpdateUserDetails_NotFound() throws Exception {
                // Don't create user details
                UserDetailsDTO emptyDto=new UserDetailsDTO();
                // Perform PUT request - should result in not found
                mockMvc.perform(put("/api/users/details")
                                .header("Authorization", "Bearer " + authToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(emptyDto)))
                                .andExpect(status().isNotFound());
        }

        @Test
        public void testUpdateUserDetails_Unauthorized() throws Exception {
                // Perform PUT request without token
                mockMvc.perform(put("/api/users/details")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDetailsDTO)))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        public void testInvalidToken() throws Exception {
                // Use invalid token
                String invalidToken = "invalid.token.here";

                // Perform GET request with invalid token
                mockMvc.perform(get("/api/users/details")
                                .header("Authorization", "Bearer " + invalidToken))
                                .andExpect(status().isUnauthorized());
        }
}