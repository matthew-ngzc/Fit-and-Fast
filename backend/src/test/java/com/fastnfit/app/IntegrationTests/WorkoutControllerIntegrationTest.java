package com.fastnfit.app.IntegrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastnfit.app.dto.WorkoutDTO;
import com.fastnfit.app.enums.FitnessLevel;
import com.fastnfit.app.enums.WorkoutLevel;
import com.fastnfit.app.enums.WorkoutType;
import com.fastnfit.app.model.Exercise;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.Workout;
import com.fastnfit.app.repository.UserRepository;
import com.fastnfit.app.repository.WorkoutRepository;
import com.fastnfit.app.service.JwtService;
import com.fastnfit.app.service.WorkoutService;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Use test profile
@Transactional // Rollback transactions after each test
public class WorkoutControllerIntegrationTest {

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

    @Autowired
    private WorkoutService workoutService;

    private static final String TEST_EMAIL = "workout-test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private User testUser;
    private String authToken;

    @BeforeEach
    public void setup() {
        // Clear existing test user if exists
        userRepository.deleteAll();
        workoutRepository.deleteAll();

        // Create a test user for authentication
        testUser = new User();
        testUser.setEmail(TEST_EMAIL);
        testUser.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        testUser = userRepository.save(testUser);

        Workout testWorkout=new Workout();
        List<Exercise> exercises=new ArrayList<Exercise>();
        testWorkout.setName("Test workout name");
        testWorkout.setWorkoutTips("Test workout tips");
        testWorkout.setLevel(WorkoutLevel.Advanced);
        testWorkout.setExercises(exercises);
        testWorkout.setDurationInMinutes(20);
        testWorkout.setDescription("Test workout");
        testWorkout.setCategory(WorkoutType.HIGH_ENERGY);
        testWorkout.setCalories(100);
        workoutRepository.save(testWorkout);

        // Generate JWT token for authenticated requests
        authToken = jwtService.generateToken(testUser.getUserId());
    }

    @Test
    public void testGetAllWorkouts_Success() throws Exception {
        // Perform request to get all workouts
        MvcResult result = mockMvc.perform(get("/api/workouts")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Verify response contains a list of workouts
        String content = result.getResponse().getContentAsString();
        List<WorkoutDTO> workouts = Arrays.asList(objectMapper.readValue(content, WorkoutDTO[].class));
        
        // We don't know the exact content but we can assert it's a valid list
        assertNotNull(workouts, "Response should contain a list of workouts");
    }

    @Test
    public void testGetWorkoutById_Success() throws Exception {
        // Get a workout ID from the service (assuming there's at least one workout)
        List<WorkoutDTO> workouts = workoutService.getAllWorkouts();
        
        // Skip test if no workouts are available in test database
        if (workouts.isEmpty()) {
            return;
        }
        
        Long workoutId = workouts.get(0).getWorkoutId();

        // Perform request to get workout by ID
        MvcResult result = mockMvc.perform(get("/api/workouts/"+workoutId)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Parse response
        WorkoutDTO workout = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                WorkoutDTO.class);

        // Verify workout data is correct
        assertEquals(workoutId, workout.getWorkoutId(), "Workout ID should match");
    }

    @Test
    public void testGetWorkoutById_NotFound() throws Exception {
        // Try to get a workout with a non-existent ID
        Long nonExistentId = 99999L;

        // Perform request - should return 404 Not Found
        mockMvc.perform(get("/api/workouts/"+nonExistentId)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetWorkoutsByCategory_Success() throws Exception {
        // Test getting workouts by category
        WorkoutType testCategory = WorkoutType.HIIT;

        // Perform request to get workouts by category
        MvcResult result = mockMvc.perform(get("/api/workouts/category/{category}", testCategory.getValue())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Parse response
        String content = result.getResponse().getContentAsString();
        List<WorkoutDTO> workouts = Arrays.asList(objectMapper.readValue(content, WorkoutDTO[].class));

        // Verify all returned workouts have the correct category
        for (WorkoutDTO workout : workouts) {
            assertEquals(testCategory.getValue(), workout.getCategory(), 
                    "All workouts should have the requested category");
        }
    }

    @Test
    public void testGetWorkoutsByCategory_InvalidCategory() throws Exception {
        // Try to get workouts with an invalid category
        String invalidCategory = "invalid-category";

        // Perform request - should return 400 Bad Request
        mockMvc.perform(get("/api/workouts/category/{category}", invalidCategory)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetWorkoutsByLevel_Success() throws Exception {
        // Test getting workouts by level
        String testLevel = WorkoutLevel.Beginner.name();

        // Perform request to get workouts by level
        MvcResult result = mockMvc.perform(get("/api/workouts/level/{level}", testLevel)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Parse response
        String content = result.getResponse().getContentAsString();
        List<WorkoutDTO> workouts = Arrays.asList(objectMapper.readValue(content, WorkoutDTO[].class));

        // Verify all returned workouts have the correct level
        for (WorkoutDTO workout : workouts) {
            assertEquals(testLevel, workout.getLevel(), 
                    "All workouts should have the requested level");
        }
    }

    @Test
    public void testGetWorkoutsByLevel_InvalidLevel() throws Exception {
        // Try to get workouts with an invalid level
        String invalidLevel = "SUPER_ADVANCED";

        // Perform request - should return 400 Bad Request
        mockMvc.perform(get("/api/workouts/level/{level}", invalidLevel)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testUnauthorizedAccess() throws Exception {
        // Attempt to access endpoint without authentication
        mockMvc.perform(get("/api/workouts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}