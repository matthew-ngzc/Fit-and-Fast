package com.fastnfit.app.IntegrationTests;

/*
To run:
./mvnw test "-Dtest=ChatbotServiceIntegrationTest"
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastnfit.app.dto.UserDetailsDTO;
import com.fastnfit.app.enums.FitnessLevel;
import com.fastnfit.app.service.ChatbotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.context.ActiveProfiles;
import com.fasterxml.jackson.databind.JsonNode;


//@SpringBootTest
//@ActiveProfiles("dev")
public class ChatbotServiceIntegrationTest {

    @Autowired
    private ChatbotService chatbotService;

    @Test
    void testChatbotConnectsToOpenAI() throws Exception {
        // 👤 Create fake user profile
        UserDetailsDTO dto = new UserDetailsDTO();
        dto.setDob(Date.valueOf("2000-01-01"));
        dto.setHeight(165.0);
        dto.setWeight(60.0);
        dto.setFitnessLevel(FitnessLevel.INTERMEDIATE);
        dto.setWorkoutGoal("Weight Loss");
        dto.setWorkoutType("HIIT");
        dto.setMenstrualCramps(false);

        // 🏋️ Create workout plan
        Map<String, Object> currentWorkout = new HashMap<>();
        currentWorkout.put("format", "40s work, 20s rest");
        currentWorkout.put("exercises", List.of("Push Ups", "Plank", "Lunges"));

        // 🧠 Talk to AI
        String response = chatbotService.getResponse("Make it easier", dto, currentWorkout);

        // ✅ Always ensure a response was received
        assertNotNull(response);
        System.out.println("=== Full Chatbot Response ===\n" + response);

        // 🔍 Extract '''json
        Pattern jsonPattern = Pattern.compile("```json\\s*([\\s\\S]*?)\\s*```");

        Matcher matcher = jsonPattern.matcher(response);
        assertTrue(matcher.find(), "Response should contain a ```json block");


        String jsonPart = matcher.group(1).trim();
        System.out.println("\n=== Extracted JSON Block ===\n" + jsonPart);

        // 🧪 Parse into a WorkoutDTO or validate JSON keys
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonPart);

        assertTrue(jsonNode.has("name"));
        assertTrue(jsonNode.has("exercises"));
        assertTrue(jsonNode.get("exercises").isArray());

        // 🔍 Extract natural language section
        String[] split = response.split("\\*\\*\\[Natural Language\\]\\*\\*");
        String naturalSection = split[1].trim();

        System.out.println("\n=== Natural Language Section ===\n" + naturalSection);

        // ✅ Ensure the natural text looks like a workout
        assertTrue(split.length > 1, "Response should contain natural language section");

        
        assertTrue(naturalSection.toLowerCase().contains("warm-up") || naturalSection.length() > 30);

    }

}
