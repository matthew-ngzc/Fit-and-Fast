package com.fastnfit.app.IntegrationTests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastnfit.app.dto.RecommendationDTO;
import com.fastnfit.app.dto.StreakDTO;
import com.fastnfit.app.dto.WorkoutDTO;
import com.fastnfit.app.enums.FitnessLevel;
import com.fastnfit.app.enums.PregnancyStatus;
import com.fastnfit.app.enums.WorkoutGoal;
import com.fastnfit.app.enums.WorkoutLevel;
import com.fastnfit.app.enums.WorkoutType;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.UserDetails;
import com.fastnfit.app.model.Workout;
import com.fastnfit.app.repository.UserDetailsRepository;
import com.fastnfit.app.repository.UserRepository;
import com.fastnfit.app.repository.WorkoutRepository;
import com.fastnfit.app.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class HomeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private JwtService jwtService;

    private User testUser;
    private String authToken;

    @BeforeEach
    public void setup() {
        // Create test user
        testUser = new User();
        testUser.setEmail("hometest@example.com");
        testUser.setPassword("password123");
        testUser = userRepository.save(testUser);

        // Create user details
        UserDetails userDetails = new UserDetails();
        userDetails.setUser(testUser);
        userDetails.setFitnessLevel(FitnessLevel.INTERMEDIATE);
        userDetails.setWorkoutGoal(WorkoutGoal.WEIGHT_LOSS);
        userDetails.setWorkoutType(WorkoutType.HIIT);
        userDetails.setPregnancyStatus(PregnancyStatus.NO);
        userDetails.setCurrentStreak(5);
        userDetails.setLongestStreak(7);
        userDetailsRepository.save(userDetails);

        // Create test workouts for different categories
        createTestWorkouts();

        // Generate JWT token for authentication
        authToken = jwtService.generateToken(testUser.getUserId());
    }

    private void createTestWorkouts() {
        // Create HIIT workout
        Workout hiitWorkout = new Workout();
        hiitWorkout.setName("Test HIIT Workout");
        hiitWorkout.setDescription("Test HIIT workout description");
        hiitWorkout.setCategory(WorkoutType.HIIT);
        hiitWorkout.setLevel(WorkoutLevel.Intermediate);
        hiitWorkout.setCalories(300);
        hiitWorkout.setDurationInMinutes(30);
        workoutRepository.save(hiitWorkout);

        // Create Yoga workout
        Workout yogaWorkout = new Workout();
        yogaWorkout.setName("Test Yoga Workout");
        yogaWorkout.setDescription("Test Yoga workout description");
        yogaWorkout.setCategory(WorkoutType.Yoga);
        yogaWorkout.setLevel(WorkoutLevel.Beginner);
        yogaWorkout.setCalories(150);
        yogaWorkout.setDurationInMinutes(45);
        workoutRepository.save(yogaWorkout);

        // Create Prenatal workout
        Workout prenatalWorkout = new Workout();
        prenatalWorkout.setName("Test Prenatal Workout");
        prenatalWorkout.setDescription("Test Prenatal workout description");
        prenatalWorkout.setCategory(WorkoutType.PRENATAL);
        prenatalWorkout.setLevel(WorkoutLevel.All_Levels);
        prenatalWorkout.setCalories(120);
        prenatalWorkout.setDurationInMinutes(25);
        workoutRepository.save(prenatalWorkout);
        
        // Create Strength workout
        Workout strengthWorkout = new Workout();
        strengthWorkout.setName("Test Strength Workout");
        strengthWorkout.setDescription("Test Strength workout description");
        strengthWorkout.setCategory(WorkoutType.STRENGTH);
        strengthWorkout.setLevel(WorkoutLevel.Advanced);
        strengthWorkout.setCalories(350);
        strengthWorkout.setDurationInMinutes(60);
        workoutRepository.save(strengthWorkout);
    }

    @Test
    public void testGetDailyRecommendation_Authenticated() throws Exception {
        // Perform request with JWT token
        MvcResult result = mockMvc.perform(get("/api/home/recommendation")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Parse response
        RecommendationDTO recommendation = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                RecommendationDTO.class);

        assertNotNull(recommendation, "Recommendation should not be null");
        assertNotNull(recommendation.getTitle(), "Recommendation title should not be null");
        assertNotNull(recommendation.getDescription(), "Recommendation description should not be null");
    }

    @Test
    public void testGetDailyRecommendation_Unauthenticated() throws Exception {
        // Perform request without JWT token
        mockMvc.perform(get("/api/home/recommendation")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetUserStreak_Authenticated() throws Exception {
        // Perform request with JWT token
        MvcResult result = mockMvc.perform(get("/api/home/streak")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Parse response
        StreakDTO streak = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                StreakDTO.class);

        assertNotNull(streak, "Streak should not be null");
        assertEquals(5, streak.getDays(), "Streak days should match the value in user details");
    }

    @Test
    public void testGetUserStreak_Unauthenticated() throws Exception {
        // Perform request without JWT token
        mockMvc.perform(get("/api/home/streak")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetCategorizedWorkouts_Authenticated() throws Exception {
        // Perform request with JWT token
        MvcResult result = mockMvc.perform(get("/api/home/workouts")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Parse response using TypeReference
        String content = result.getResponse().getContentAsString();
        Map<String, List<WorkoutDTO>> workoutsByCategory = objectMapper.readValue(
                content,
                new TypeReference<Map<String, List<WorkoutDTO>>>() {}
        );

        assertNotNull(workoutsByCategory, "Categorized workouts should not be null");
        assertTrue(workoutsByCategory.containsKey("hiit"), "Should contain HIIT category");
        assertTrue(workoutsByCategory.containsKey("yoga"), "Should contain Yoga category");
        assertTrue(workoutsByCategory.containsKey("prenatal"), "Should contain Prenatal category");
        assertTrue(workoutsByCategory.containsKey("strength"), "Should contain Strength category");
    }

    @Test
    public void testGetCategorizedWorkouts_Unauthenticated() throws Exception {
        // Perform request without JWT token
        mockMvc.perform(get("/api/home/workouts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetWorkoutsByCategory_Authenticated() throws Exception {
        // Test valid category
        MvcResult result = mockMvc.perform(get("/api/home/workouts/yoga")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.yoga").isArray())
                .andReturn();

        // Parse response using TypeReference
        String content = result.getResponse().getContentAsString();
        Map<String, List<WorkoutDTO>> workoutsByCategory = objectMapper.readValue(
                content,
                new TypeReference<Map<String, List<WorkoutDTO>>>() {}
        );

        List<WorkoutDTO> yogaWorkouts = workoutsByCategory.get("yoga");
        assertNotNull(yogaWorkouts, "Yoga workouts should not be null");
        assertFalse(yogaWorkouts.isEmpty(), "Yoga workouts should not be empty");
        assertEquals("Test Yoga Workout", yogaWorkouts.get(0).getName(), "Workout name should match");
    }

    @Test
    public void testGetWorkoutsByCategory_InvalidCategory() throws Exception {
        // Test invalid category
        mockMvc.perform(get("/api/home/workouts/invalid_category")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetWorkoutsByCategory_Unauthenticated() throws Exception {
        // Perform request without JWT token
        mockMvc.perform(get("/api/home/workouts/yoga")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
    
    // Alternative approach using Spring Security's @WithMockUser
    @Test
    @WithMockUser(username = "1") // Assumes user ID is "1" - matches what AuthUtils expects
    public void testGetDailyRecommendation_WithMockUser() throws Exception {
        // Create a user with ID 1 to match the mock user
        User mockUser = new User();
        mockUser.setUserId(1L);
        mockUser.setEmail("mockuser@example.com");
        mockUser.setPassword("password123");
        userRepository.save(mockUser);
        
        // Create user details for the mock user
        UserDetails mockUserDetails = new UserDetails();
        mockUserDetails.setUser(mockUser);
        mockUserDetails.setFitnessLevel(FitnessLevel.BEGINNER);
        mockUserDetails.setWorkoutGoal(WorkoutGoal.STRENGTH_BUILDING);
        mockUserDetails.setWorkoutType(WorkoutType.STRENGTH);
        mockUserDetails.setPregnancyStatus(PregnancyStatus.NO);
        userDetailsRepository.save(mockUserDetails);
        
        // Perform request using @WithMockUser annotation instead of JWT token
        mockMvc.perform(get("/api/home/recommendation")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").isNotEmpty())
                .andExpect(jsonPath("$.description").isNotEmpty());
    }
}