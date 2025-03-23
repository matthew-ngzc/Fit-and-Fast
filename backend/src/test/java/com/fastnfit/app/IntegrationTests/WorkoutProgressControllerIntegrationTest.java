package com.fastnfit.app.IntegrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastnfit.app.dto.WorkoutCompletionRequest;
import com.fastnfit.app.dto.WorkoutCompletionResponse;
import com.fastnfit.app.dto.WorkoutDTO;
import com.fastnfit.app.enums.WorkoutLevel;
import com.fastnfit.app.enums.WorkoutType;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.Workout;
import com.fastnfit.app.repository.UserRepository;
import com.fastnfit.app.repository.WorkoutRepository;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Use test profile
@Transactional // Rollback transactions after each test
public class WorkoutProgressControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private static final String TEST_EMAIL = "workout_test@example.com";
    private static final String TEST_PASSWORD = "password123";

    private User testUser;
    private Workout testWorkout;
    private String authToken;

    @BeforeEach
    public void setup() {
        // Clear and create a test user
        userRepository.findByEmail(TEST_EMAIL).ifPresent(user -> userRepository.delete(user));
        
        testUser = new User();
        testUser.setEmail(TEST_EMAIL);
        testUser.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        testUser = userRepository.save(testUser);
        
        // Create a test workout
        testWorkout = new Workout();
        testWorkout.setName("Test Workout");
        testWorkout.setDescription("A workout for testing purposes");
        testWorkout.setCategory(WorkoutType.HIGH_ENERGY);
        testWorkout.setLevel(WorkoutLevel.Beginner);
        testWorkout.setCalories(150);
        testWorkout.setDurationInMinutes(30);
        testWorkout = workoutRepository.save(testWorkout);
        
        // Create JWT token for authentication
        authToken = jwtService.generateToken(testUser.getUserId());
    }

    @Test
    public void testGetWorkoutsInOrder() throws Exception {
        // Create a second workout to ensure ordering
        Workout secondWorkout = new Workout();
        secondWorkout.setName("Second Test Workout");
        secondWorkout.setDescription("Another workout for testing");
        secondWorkout.setCategory(WorkoutType.STRENGTH);
        secondWorkout.setLevel(WorkoutLevel.Intermediate);
        secondWorkout.setCalories(200);
        secondWorkout.setDurationInMinutes(45);
        secondWorkout = workoutRepository.save(secondWorkout);
        
        // Perform GET request to get ordered workouts
        MvcResult result = mockMvc.perform(get("/api/workout-progress/workouts/ordered")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andReturn();
                
        // Parse the response
        String content = result.getResponse().getContentAsString();
        List<WorkoutDTO> workouts = objectMapper.readValue(content, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, WorkoutDTO.class));
        
        // Verify workouts are returned in order of ID
        assertFalse(workouts.isEmpty(), "Workout list should not be empty");
        for (int i = 1; i < workouts.size(); i++) {
            assertTrue(workouts.get(i-1).getWorkoutId() < workouts.get(i).getWorkoutId(), 
                    "Workouts should be ordered by ID");
        }
    }

    @Test
    public void testCompleteWorkout_Success() throws Exception {
        // Create workout completion request
        WorkoutCompletionRequest request = new WorkoutCompletionRequest();
        request.setWorkoutId(testWorkout.getWorkoutId());
        
        // Perform POST request to complete workout
        MvcResult result = mockMvc.perform(post("/api/workout-progress/complete")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.workoutId").value(testWorkout.getWorkoutId()))
                .andExpect(jsonPath("$.workoutName").value(testWorkout.getName()))
                .andExpect(jsonPath("$.caloriesBurned").value(testWorkout.getCalories()))
                .andExpect(jsonPath("$.historyId").isNotEmpty())
                .andExpect(jsonPath("$.totalWorkouts").value(1))
                .andExpect(jsonPath("$.totalCaloriesBurned").value(testWorkout.getCalories()))
                .andExpect(jsonPath("$.totalDurationInMinutes").value(testWorkout.getDurationInMinutes()))
                .andReturn();
        
        // Parse the response
        WorkoutCompletionResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                WorkoutCompletionResponse.class);
        
        // Verify response values
        assertEquals(testUser.getUserId(), response.getUserId(), "User ID should match");
        assertEquals(testWorkout.getWorkoutId(), response.getWorkoutId(), "Workout ID should match");
        assertEquals(testWorkout.getName(), response.getWorkoutName(), "Workout name should match");
        assertEquals(testWorkout.getCalories(), response.getCaloriesBurned(), "Calories burned should match");
        assertEquals(1, response.getTotalWorkouts(), "Total workouts should be 1");
        assertEquals(testWorkout.getCalories(), response.getTotalCaloriesBurned(), "Total calories burned should match");
        assertEquals(testWorkout.getDurationInMinutes(), response.getTotalDurationInMinutes(), "Total duration should match");
    }

    @Test
    public void testCompleteWorkout_NonexistentWorkout() throws Exception {
        // Create workout completion request with non-existent workout ID
        WorkoutCompletionRequest request = new WorkoutCompletionRequest();
        request.setWorkoutId(999L); // Non-existent workout ID
        
        // Perform POST request - should fail
        mockMvc.perform(post("/api/workout-progress/complete")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testCompleteWorkout_Unauthorized() throws Exception {
        // Create workout completion request
        WorkoutCompletionRequest request = new WorkoutCompletionRequest();
        request.setWorkoutId(testWorkout.getWorkoutId());
        
        // Perform POST request without authentication token - should fail
        mockMvc.perform(post("/api/workout-progress/complete/" + testUser.getUserId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}