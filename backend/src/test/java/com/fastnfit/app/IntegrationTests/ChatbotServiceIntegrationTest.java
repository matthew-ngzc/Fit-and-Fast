package com.fastnfit.app.IntegrationTests;

import com.fastnfit.app.dto.UserDetailsDTO;
import com.fastnfit.app.enums.FitnessLevel;
import com.fastnfit.app.service.ChatbotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("dev")
public class ChatbotServiceIntegrationTest {

    @Autowired
    private ChatbotService chatbotService;

    @Test
    void testChatbotConnectsToOpenAI() {
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
        String response = chatbotService.getResponse(
                "Make it easier",
                dto,
                currentWorkout
        );

        // ✅ Check response
        System.out.println("Chatbot replied:\n" + response);
        assertNotNull(response);
        assertTrue(response.toLowerCase().contains("push") || response.length() > 10);
    }
}
