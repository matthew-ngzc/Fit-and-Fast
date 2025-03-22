package com.fastnfit.app.service;

import com.fastnfit.app.dto.UserDetailsDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

//one shot give response, not streamed
@Service
public class ChatbotService {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    public String getResponse(String userInput, UserDetailsDTO userDetailsDTO, Map<String, Object> currentWorkout) {
        String apiUrl = "https://api.openai.com/v1/chat/completions";

        String systemPrompt = buildSystemPrompt(userDetailsDTO, currentWorkout);

        JSONArray messages = new JSONArray()
                .put(new JSONObject().put("role", "system").put("content", systemPrompt))
                .put(new JSONObject().put("role", "user").put("content", userInput));

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-4o-mini");
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.3);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openAiApiKey);
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

        return new JSONObject(response.getBody())
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
    }

    private String buildSystemPrompt(UserDetailsDTO dto, Map<String, Object> workout) {
        List<String> exercises = (List<String>) workout.get("exercises");

        return """
            You are an AI fitness trainer specializing in time-efficient, effective workouts for busy women.
            Format your reply like this:
            **Here is your modified routine**
            **Format:** 40s work, 20s rest
            - Exercise 1
            - Exercise 2
            ...
            **Would you like to use this instead?**
            
            Avoid long explanations. Be concise.
            
            User Profile:
            - Age: %s
            - Height: %.1f cm
            - Weight: %.1f kg
            - Fitness Level: %s
            - Goal: %s
            - Workout Type: %s
            - Menstrual Cramps: %s
            
            Current Workout:
            - Format: %s
            - Exercises: %s
            
            Adjust for menstrual comfort, and offer variety if possible.
        """.formatted(
                dto.getDob() != null ? dto.getDob().toString().substring(0, 4) : "N/A",
                dto.getHeight() != null ? dto.getHeight() : 0,
                dto.getWeight() != null ? dto.getWeight() : 0,
                dto.getFitnessLevel(),
                dto.getWorkoutGoal(),
                dto.getWorkoutType(),
                dto.getMenstrualCramps() ? "Yes" : "No",
                workout.get("format"),
                String.join(", ", exercises)
        );
    }
}
