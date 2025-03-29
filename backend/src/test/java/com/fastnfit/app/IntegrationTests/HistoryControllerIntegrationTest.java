package com.fastnfit.app.IntegrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastnfit.app.dto.ActivityOverviewDTO;
import com.fastnfit.app.dto.HistoryDTO;
import com.fastnfit.app.dto.WorkoutDTO;
import com.fastnfit.app.enums.WorkoutLevel;
import com.fastnfit.app.enums.WorkoutType;
import com.fastnfit.app.model.History;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.Workout;
import com.fastnfit.app.repository.HistoryRepository;
import com.fastnfit.app.repository.UserRepository;
import com.fastnfit.app.repository.WorkoutRepository;
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

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class HistoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private JwtService jwtService;

    private User testUser;
    private Workout testWorkout;
    private String authToken;

    @BeforeEach
    public void setup() {
        // Clear previous test history entries
        historyRepository.deleteAll();
        userRepository.deleteAll();
        workoutRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setEmail("history-test@example.com");
        testUser.setPassword("password123");
        testUser = userRepository.save(testUser);

        // Create test workout
        testWorkout = new Workout();
        testWorkout.setName("Test Workout");
        testWorkout.setDescription("Workout for testing");
        testWorkout.setCalories(200);
        testWorkout.setDurationInMinutes(30);
        testWorkout.setLevel(WorkoutLevel.Beginner);
        testWorkout.setCategory(WorkoutType.LOW_IMPACT);
        testWorkout = workoutRepository.save(testWorkout);

        // Generate auth token for test user
        authToken = jwtService.generateToken(testUser.getUserId());
    }

    @Test
    public void testCreateHistory_Success() throws Exception {
        // Create HistoryDTO for request
        HistoryDTO historyDTO = new HistoryDTO();
        historyDTO.setCaloriesBurned(150);
        historyDTO.setDurationInMinutes(25);

        // Create and set workout DTO with all required properties
        WorkoutDTO workoutDTO = new WorkoutDTO();
        workoutDTO.setWorkoutId(testWorkout.getWorkoutId());
        historyDTO.setWorkout(workoutDTO);

        // Set current time for workout
        Calendar cal = Calendar.getInstance();
        Timestamp currentTime = new Timestamp(cal.getTimeInMillis());
        historyDTO.setWorkoutDateTime(currentTime);

        // Perform create history request
        MvcResult result = mockMvc.perform(post("/api/history/user/" + testUser.getUserId())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(historyDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Workout"))
                .andExpect(jsonPath("$.caloriesBurned").value(150))
                .andExpect(jsonPath("$.durationInMinutes").value(25))
                .andReturn();

        // Parse response
        HistoryDTO response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                HistoryDTO.class);

        // Verify history entry exists in database
        History savedHistory = historyRepository.findById(response.getHistoryId()).orElse(null);
        assertNotNull(savedHistory, "History entry should be saved in database");
        assertEquals("Test Workout", savedHistory.getWorkout().getName(), "History name should match");
        assertEquals(testUser.getUserId(), savedHistory.getUser().getUserId(), "User should match");
    }

    @Test
    public void testGetUserHistory() throws Exception {
        // Create some history entries for the test user
        createTestHistoryEntries(3);

        // Perform get user history request
        MvcResult result = mockMvc.perform(get("/api/history/user/" + testUser.getUserId())
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andReturn();

        // Parse response
        List<?> historyList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                List.class);

        // Verify response contains the correct number of entries
        assertEquals(3, historyList.size(), "Should return 3 history entries");
    }

    @Test
    public void testGetUserHistoryBetweenDates() throws Exception {
        // Create some history entries with different dates
        createHistoryWithCustomDate("2023-01-01");
        createHistoryWithCustomDate("2023-01-15");
        createHistoryWithCustomDate("2023-02-01");

        // Perform get history between dates request
        MvcResult result = mockMvc.perform(get("/api/history/user/" + testUser.getUserId() + "/date-range")
                .header("Authorization", "Bearer " + authToken)
                .param("startDate", "2023-01-01")
                .param("endDate", "2023-01-31"))
                .andExpect(status().isOk())
                .andReturn();

        // Parse response
        List<?> historyList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                List.class);

        // Verify response contains the correct number of entries
        assertEquals(2, historyList.size(), "Should return 2 history entries within the date range");
    }

    @Test
    public void testGetActivityOverview() throws Exception {
        // Create recent history entries
        createTestHistoryEntries(5);

        // Perform get activity overview request
        MvcResult result = mockMvc.perform(get("/api/history/activity/overview")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.today").exists())
                .andExpect(jsonPath("$.weekly").exists())
                .andExpect(jsonPath("$.recentWorkouts").exists())
                .andReturn();

        // Parse response
        ActivityOverviewDTO overview = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ActivityOverviewDTO.class);

        // Verify response structure
        assertNotNull(overview.getToday(), "Today's summary should exist");
        assertEquals(7, overview.getWeekly().size(), "Weekly summary should have 7 entries");
        assertTrue(overview.getRecentWorkouts().size() <= 5, "Recent workouts should have at most 5 entries");
    }

    @Test
    public void testLoadMoreHistory() throws Exception {
        // Create more history entries than the default limit
        createTestHistoryEntries(10);

        // Get current time for after parameter
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(DateTimeFormatter.ISO_DATE_TIME);

        // Perform load more history request
        MvcResult result = mockMvc.perform(get("/api/history/load-more")
                .header("Authorization", "Bearer " + authToken)
                .param("after", formattedDateTime)
                .param("limit", "3"))
                .andExpect(status().isOk())
                .andReturn();

        // Parse response
        List<?> historyList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                List.class);

        // Verify response contains the requested number of entries
        assertEquals(3, historyList.size(), "Should return 3 history entries");
    }

    @Test
    public void testCreateHistory_InvalidData() throws Exception {
        // Create incomplete HistoryDTO (missing required fields)
        HistoryDTO historyDTO = new HistoryDTO();
        historyDTO.setName("Invalid History");
        // Missing caloriesBurned, durationInMinutes, and workout

        // Perform create history request - should fail validation
        mockMvc.perform(post("/api/history/user/" + testUser.getUserId())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(historyDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetUserHistory_Unauthorized() throws Exception {
        // Attempt to get history without authorization
        mockMvc.perform(get("/api/history/user/" + testUser.getUserId()))
                .andExpect(status().isUnauthorized());
    }

    // Helper methods

    private void createTestHistoryEntries(int count) {
        for (int i = 0; i < count; i++) {
            History history = new History();
            history.setUser(testUser);
            history.setWorkout(testWorkout);
            history.setCaloriesBurned(100 + i);
            history.setDurationInMinutes(20 + i);

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, -i); // Make entries have different times
            history.setWorkoutDateTime(new Timestamp(cal.getTimeInMillis()));

            historyRepository.save(history);
        }
    }

    private void createHistoryWithCustomDate(String dateStr) {
        History history = new History();
        history.setUser(testUser);
        history.setWorkout(testWorkout);
        history.setCaloriesBurned(100);
        history.setDurationInMinutes(20);

        // Parse the date string
        LocalDate date = LocalDate.parse(dateStr);
        Timestamp timestamp = Timestamp.valueOf(date.atStartOfDay());
        history.setWorkoutDateTime(timestamp);

        historyRepository.save(history);
    }
}
