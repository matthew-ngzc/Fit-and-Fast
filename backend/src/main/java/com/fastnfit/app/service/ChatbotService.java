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

    /*
     * returns 2 parts to the string
     * 1. json, used for extracting the workoutDTO information for accepting of workout
     * 2. human readable section, which is displayed to the user, include extra information that we dont need for the workoutDTO
     */
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
            You are an AI fitness coach helping users get personalized workout routines based on their profile and preferences.
            
            Please respond in TWO clearly separated sections:
            
            ---
            
            **[JSON]**  
            Use this section to structure the workout for the backend. Output strictly valid JSON:
            
            <BEGIN_JSON>
            {
            "name": "Workout Title",
            "description": "Purpose or focus of the workout",
            "durationInMinutes": 20,
            "calories": 180,
            "level": "BEGINNER",
            "category": "STRENGTH",
            "exercises": [
                { "name": "Jumping Jacks", "duration": 40, "rest": 20 },
                { "name": "Bodyweight Squats", "duration": 40, "rest": 20 }
            ]
            }
            <END_JSON>
            
            ---
            
            **[Natural Language]**  
            Use this section to write a motivational and readable workout suggestion for the user.
            
            Format it like this (keep structure, but personalize):
            
            Here's a gentle workout that's more suitable during your period:
            
            **Warm-up (5 minutes)**  
            • Gentle walking in place - 2 minutes  
            • Shoulder rolls - 1 minute  
            • Gentle side stretches - 2 minutes
            
            **Main Workout (10 minutes)**  
            • Modified cat-cow stretches - 2 minutes  
            • Seated overhead stretches - 3 sets of 30 seconds  
            • Gentle core engagement (seated) - 3 sets of 10 reps  
            • Light arm raises with or without light weights - 3 sets of 12 reps  
            • Seated leg extensions - 3 sets of 10 reps
            
            **Cool Down (5 minutes)**  
            • Deep breathing exercises  
            • Gentle full-body stretching
            
            Wrap it up with a line like:  
            "This workout avoids intense abdominal exercises and high-impact movements. Would you like to try this workout?"
            
            ---
            
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
