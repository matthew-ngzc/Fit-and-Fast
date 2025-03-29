package com.fastnfit.app.IntegrationTests;

/*
To run:
./mvnw test "-Dtest=ChatbotServiceIntegrationTest"
 */

import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastnfit.app.dto.UserDetailsDTO;
import com.fastnfit.app.enums.FitnessLevel;
import com.fastnfit.app.enums.WorkoutGoal;
import com.fastnfit.app.model.ChatHistory;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.UserDetails;
import com.fastnfit.app.repository.UserDetailsRepository;
import com.fastnfit.app.repository.UserRepository;
import com.fastnfit.app.service.ChatbotService;
import com.fastnfit.app.repository.ChatHistoryRepository;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.JsonNode;

@SpringBootTest
@ActiveProfiles("dev")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class ChatbotServiceIntegrationTest {

    @Autowired
    private ChatbotService chatbotService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    private User savedUser;

    @BeforeEach
    void setup() throws Exception {
        String testEmail = "test@example.com";

        savedUser = userRepository.findByEmail(testEmail).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(testEmail);
            newUser.setPassword("password123");
            return userRepository.save(newUser);
        });

        if (userDetailsRepository.findByUser(savedUser).isEmpty()) {
            UserDetails details = new UserDetails();
            details.setUser(savedUser);
            details.setDob(new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01").toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate());
            details.setHeight(165.0);
            details.setWeight(60.0);
            details.setFitnessLevel(FitnessLevel.Intermediate);
            details.setWorkoutGoal(WorkoutGoal.WEIGHT_LOSS.getValue());
            details.setWorkoutType("HIIT");
            details.setMenstrualCramps(false);
            userDetailsRepository.save(details);
        }

        // Clear chat history
        chatHistoryRepository.deleteAllByUser(savedUser);
    }

    @Test
    @Order(1)
    @Transactional
    public void testChatbotMemoryAndNonWorkoutHandling() throws Exception {
        // Set up or retrieve user
        User user = userRepository.findByEmail("test@example.com").orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail("test@example.com");
            newUser.setPassword("password123");
            return userRepository.save(newUser);
        });

        // Set up user details if missing
        userDetailsRepository.findByUser(user).orElseGet(() -> {
            UserDetails details = new UserDetails();
            details.setUser(user);
            details.setDob(java.sql.Date.valueOf("2000-01-01").toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate());
            details.setFitnessLevel(FitnessLevel.Beginner);
            details.setWorkoutGoal("Weight Loss");
            details.setWorkoutType("HIIT");
            details.setHeight(170.0);
            details.setWeight(60.0);
            details.setMenstrualCramps(false);
            return userDetailsRepository.save(details);
        });

        // Add recent chat history
        LocalDateTime now = LocalDateTime.now();
        chatHistoryRepository.save(ChatHistory.builder()
                .user(user)
                .role("user")
                .content("Hello, I want to get fit.")
                .timestamp(now.minusMinutes(3))
                .build());

        chatHistoryRepository.save(ChatHistory.builder()
                .user(user)
                .role("assistant")
                .content("Sure! How often do you exercise?")
                .timestamp(now.minusMinutes(2))
                .build());

        chatHistoryRepository.save(ChatHistory.builder()
                .user(user)
                .role("user")
                .content("I exercise twice a week.")
                .timestamp(now.minusMinutes(1))
                .build());

        // Build user details DTO
        UserDetails details = userDetailsRepository.findByUser(user).orElseThrow();
        UserDetailsDTO dto = new UserDetailsDTO();
        dto.setUserId(user.getUserId());
        dto.setDob(details.getDob());
        dto.setHeight(details.getHeight());
        dto.setWeight(details.getWeight());
        dto.setFitnessLevel(details.getFitnessLevel());
        dto.setWorkoutGoal(details.getWorkoutGoal().getValue());
        dto.setWorkoutType(details.getWorkoutType().getValue());
        dto.setMenstrualCramps(details.getMenstrualCramps());

        // Create fullRequest payload
        JSONObject fullRequest = new JSONObject();
        fullRequest.put("message", "How many times a week do I exercise?");
        fullRequest.put("exercises", List.of()); // no active workout
        fullRequest.put("exercises_supported", List.of(
                Map.of("name", "Jumping Jacks"),
                Map.of("name", "Push Ups"),
                Map.of("name", "Squats")));

        // Invoke chatbot
        String response = chatbotService.getResponse(fullRequest, dto);
        System.out.println("\n=== Chatbot Response ===\n" + response);

        // Check that response references past history
        assertTrue(response.toLowerCase().contains("twice"));
        assertFalse(response.contains("<BEGIN_JSON>")); // Not a workout response
    }

    @Test
    @Order(2)
    void testChatbotGeneratesWorkoutPlan() throws Exception {
        UserDetails userDetails = userDetailsRepository.findByUser(savedUser)
                .orElseThrow(() -> new RuntimeException("UserDetails not found"));

        UserDetailsDTO dto = new UserDetailsDTO();
        dto.setUserId(savedUser.getUserId());
        dto.setDob(userDetails.getDob());
        dto.setHeight(userDetails.getHeight());
        dto.setWeight(userDetails.getWeight());
        dto.setFitnessLevel(userDetails.getFitnessLevel());
        dto.setWorkoutGoal(userDetails.getWorkoutGoal().getValue());
        dto.setWorkoutType(userDetails.getWorkoutType().getValue());
        dto.setMenstrualCramps(userDetails.getMenstrualCramps());

        // create a full request
        JSONObject fullRequest = new JSONObject();
        fullRequest.put("message", "Make it easier");
        fullRequest.put("exercises", List.of(
                Map.of("name", "Jumping Jacks", "duration", 40, "rest", 20),
                Map.of("name", "Push Ups", "duration", 40, "rest", 20)));
        fullRequest.put("exercises_supported", List.of(
                Map.of("name", "Jumping Jacks"),
                Map.of("name", "Push Ups"),
                Map.of("name", "Squats")));

        // 🧠 Talk to AI
        String response = chatbotService.getResponse(fullRequest, dto);
        // .replace("\n", " ")
        // .replace("\r", " ");

        // ✅ Always ensure a response was received
        assertNotNull(response);
        System.out.println("=== Full Chatbot Response ===\n" + response);

        String[] parts = response.split("---");
        if (parts.length < 2) {
            fail("Expected JSON workout section but none was found.");
        }

        // 🔍 Extract JSON block between <BEGIN_JSON> and <END_JSON>
        Pattern jsonPattern = Pattern.compile("<BEGIN_JSON>\\s*([\\s\\S]*?)\\s*<END_JSON>");
        Matcher matcher = jsonPattern.matcher(response);
        assertTrue(matcher.find(), "Response should contain a JSON block enclosed by <BEGIN_JSON> and <END_JSON>");

        String jsonPart = matcher.group(1).trim();
        System.out.println("\n=== Extracted JSON Block ===\n" + jsonPart);

        // 🧪 Parse into a WorkoutDTO or validate JSON keys
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonPart);

        assertTrue(jsonNode.has("name"));
        assertTrue(jsonNode.has("exercises"));
        assertTrue(jsonNode.get("exercises").isArray());

        // 🔍 Extract natural language section
        // String[] split = response.split("\\*\\*\\[Natural Language\\]\\*\\*");
        String[] split = response.split("(?m)^---\\s*$");
        String naturalSection = split[1].trim();

        System.out.println("\n=== Natural Language Section ===\n" + naturalSection);

        // ✅ Ensure the natural text looks like a workout
        assertTrue(split.length > 1, "Response should contain natural language section");

        assertTrue(naturalSection.toLowerCase().contains("warm-up") || naturalSection.length() > 30);

    }

}
