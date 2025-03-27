package com.fastnfit.app.IntegrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastnfit.app.dto.AchievementResponseDTO;
import com.fastnfit.app.dto.AvatarDTO;
import com.fastnfit.app.dto.GoalsDTO;
import com.fastnfit.app.dto.ProfileDTO;
import com.fastnfit.app.dto.WeeklyWorkoutsDTO;
import com.fastnfit.app.enums.WorkoutGoal;
import com.fastnfit.app.model.Achievement;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.UserAchievement;
import com.fastnfit.app.model.UserDetails;
import com.fastnfit.app.repository.AchievementRepository;
import com.fastnfit.app.repository.UserAchievementRepository;
import com.fastnfit.app.repository.UserDetailsRepository;
import com.fastnfit.app.repository.UserRepository;
import com.fastnfit.app.service.JwtService;

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

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserDetailsRepository userDetailsRepository;
    
    @Autowired
    private AchievementRepository achievementRepository;
    
    @Autowired
    private UserAchievementRepository userAchievementRepository;

    @Autowired
    private JwtService jwtService;

    private static final String TEST_EMAIL = "profile_test@example.com";
    private static final String TEST_USERNAME = "profiletester";
    
    private User testUser;
    private UserDetails testUserDetails;
    private String authToken;

    @BeforeEach
    public void setup() {
        userDetailsRepository.deleteAll();
        userRepository.deleteAll();
        achievementRepository.deleteAll();
        userAchievementRepository.deleteAll();
        
        // Create test user
        testUser = new User();
        testUser.setEmail(TEST_EMAIL);
        testUser.setPassword("password123");
        testUser = userRepository.save(testUser);
        
        // Create user details
        testUserDetails = new UserDetails();
        testUserDetails.setUser(testUser);
        testUserDetails.setUsername(TEST_USERNAME);
        testUserDetails.setHeight(175.0);
        testUserDetails.setWeight(70.0);
        testUserDetails.setDob(new Date());
        testUserDetails.setWorkoutGoal(WorkoutGoal.WEIGHT_LOSS);
        testUserDetails.setWorkoutDays(5);
        testUserDetails.setAvatar("default-avatar.png");
        testUserDetails = userDetailsRepository.save(testUserDetails);
        
        // Create test achievement
        Achievement testAchievement = new Achievement();
        testAchievement.setTitle("First Workout");
        testAchievement.setDescription("Complete your first workout");
        testAchievement = achievementRepository.save(testAchievement);
        
        // Link achievement to user
        UserAchievement userAchievement = new UserAchievement();
        userAchievement.setUser(testUser);
        userAchievement.setAchievement(testAchievement);
        userAchievement.setCompleted(false);
        userAchievementRepository.save(userAchievement);
        
        // Generate auth token
        authToken = jwtService.generateToken(testUser.getUserId());
    }

    @Test
    public void testGetUserProfile_Success() throws Exception {
        mockMvc.perform(get("/api/profile/")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.height").value(175.0))
                .andExpect(jsonPath("$.weight").value(70.0))
                .andExpect(jsonPath("$.workoutGoal").value("WEIGHT_LOSS"))
                .andExpect(jsonPath("$.workoutDays").value(5))
                .andExpect(jsonPath("$.avatar").value("default-avatar.png"));
    }

    @Test
    public void testGetUserProfile_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/profile/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdateUserProfile_Success() throws Exception {
        // Create updated profile DTO
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setUsername("updatedUsername");
        profileDTO.setEmail("updated_" + TEST_EMAIL);
        profileDTO.setHeight(180.0);
        profileDTO.setWeight(75.0);
        
        mockMvc.perform(put("/api/profile/")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updatedUsername"))
                .andExpect(jsonPath("$.email").value("updated_" + TEST_EMAIL))
                .andExpect(jsonPath("$.height").value(180.0))
                .andExpect(jsonPath("$.weight").value(75.0));
        
        // Verify changes in database
        UserDetails updatedDetails = userDetailsRepository.findByUser(testUser).orElse(null);
        assertNotNull(updatedDetails);
        assertEquals("updatedUsername", updatedDetails.getUsername());
        assertEquals(180.0, updatedDetails.getHeight());
        assertEquals(75.0, updatedDetails.getWeight());
    }

    @Test
    public void testUpdateUserGoals_Success() throws Exception {
        // Create goals DTO
        GoalsDTO goalsDTO = new GoalsDTO();
        goalsDTO.setWorkoutGoal(WorkoutGoal.STRENGTH_BUILDING);
        goalsDTO.setWorkoutDaysPerWeekGoal(4);
        
        mockMvc.perform(put("/api/profile/goals")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goalsDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workoutGoal").value(WorkoutGoal.STRENGTH_BUILDING.toString()))
                .andExpect(jsonPath("$.workoutDaysPerWeekGoal").value(4));
        
        // Verify changes in database
        UserDetails updatedDetails = userDetailsRepository.findByUser(testUser).orElse(null);
        assertNotNull(updatedDetails);
        assertEquals(WorkoutGoal.STRENGTH_BUILDING, updatedDetails.getWorkoutGoal());
        assertEquals(4, updatedDetails.getWorkoutDays());
    }

    @Test
    public void testUpdateUserAvatar_Success() throws Exception {
        // Create avatar DTO
        AvatarDTO avatarDTO = new AvatarDTO();
        avatarDTO.setAvatarLink("new-avatar-link.png");
        
        mockMvc.perform(put("/api/profile/avatar")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(avatarDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.avatarLink").value("new-avatar-link.png"));
        
        // Verify changes in database
        UserDetails updatedDetails = userDetailsRepository.findByUser(testUser).orElse(null);
        assertNotNull(updatedDetails);
        assertEquals("new-avatar-link.png", updatedDetails.getAvatar());
    }

    @Test
    public void testGetWeeklyWorkouts_Success() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/profile/weekly-workouts")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andReturn();
        
        WeeklyWorkoutsDTO responseDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                WeeklyWorkoutsDTO.class);
        
        // Since we haven't added any workouts, total should be 0
        assertEquals(0, responseDTO.getTotalWorkouts());
    }

    @Test
    public void testGetUserAchievements_Success() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/profile/achievements")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andReturn();
        
        List<AchievementResponseDTO> achievements = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, AchievementResponseDTO.class));
        
        assertFalse(achievements.isEmpty());
        assertEquals("First Workout", achievements.get(0).getTitle());
        assertEquals("Complete your first workout", achievements.get(0).getDescription());
        assertFalse(achievements.get(0).isCompleted());
    }

    @Test
    public void testWrongAuthToken_Unauthorized() throws Exception {
        // Generate a token for a non-existent user
        String wrongToken = jwtService.generateToken(9999L);
        // Create updated profile DTO
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setUsername("updatedUsername");
        profileDTO.setEmail("updated_" + TEST_EMAIL);
        profileDTO.setHeight(180.0);
        profileDTO.setWeight(75.0);
        
        mockMvc.perform(put("/api/profile/")
                .header("Authorization", "Bearer " + wrongToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileDTO)))
                .andExpect(status().isInternalServerError());
    }
}
