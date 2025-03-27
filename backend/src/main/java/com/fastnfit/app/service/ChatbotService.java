package com.fastnfit.app.service;

import com.fastnfit.app.dto.UserDetailsDTO;
import com.fastnfit.app.model.ChatHistory;
import com.fastnfit.app.model.User;
import com.fastnfit.app.repository.ChatHistoryRepository;
import com.fastnfit.app.repository.UserRepository;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

//one shot give response, not streamed
@Service
public class ChatbotService {

    private final ChatHistoryRepository chatHistoryRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;


    public ChatbotService(ChatHistoryRepository chatHistoryRepository, UserRepository userRepository, RestTemplate restTemplate) {
        this.chatHistoryRepository = chatHistoryRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    @Value("${openai.api.key}")
    private String openAiApiKey;

    public String getOpenaiApiKey() {
        return openAiApiKey;
    }

    /*
     * returns 2 parts to the string IF user is asking for a workout. OTHERWISE chats like normal
     * 1. json, used for extracting the workoutDTO information for accepting of workout
     * 2. human readable section, which is displayed to the user, include extra information that we dont need for the workoutDTO
     */
    public String getResponse(JSONObject fullRequest, UserDetailsDTO userDetailsDTO) {
        String apiUrl = "https://api.openai.com/v1/chat/completions";
        Long userId = userDetailsDTO.getUserId();
        User user = userRepository.findById(userId).orElseThrow();

        // Extract parts from frontend JSON
        String userInput = fullRequest.getString("message");
        List<String> exerciseList = fullRequest.getJSONArray("exercises_supported")
                                            .toList().stream().map(Object::toString).toList();
        List<String> workoutExercises = fullRequest.getJSONArray("exercises")
                                                .toList().stream().map(obj -> {
                                                    JSONObject o = new JSONObject((Map<?, ?>) obj);
                                                    return o.getString("name");
                                                }).toList();

        String systemPrompt = buildSystemPrompt(userDetailsDTO, exerciseList, workoutExercises);

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "system").put("content", systemPrompt));

        List<ChatHistory> history = new ArrayList<>(chatHistoryRepository.findByUserOrderByTimestampDesc(user,
            PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "timestamp"))
        ));

        // Reverse to chronological order (oldest → newest)
        Collections.reverse(history);

        // Add chat history
        for (ChatHistory msg : history) {
            messages.put(new JSONObject()
                    .put("role", msg.getRole())
                    .put("content", msg.getContent()));
        }
    
        // Add current user input
        messages.put(new JSONObject()
                    .put("role", "user")
                    .put( "content", userInput));
        
        // Save user message
        chatHistoryRepository.save(ChatHistory.builder()
                            .user(user)
                            .role("user")
                            .content(userInput)
                            .timestamp(LocalDateTime.now())
                            .build());


        // Prepare OpenAI request
        JSONObject requestBody = new JSONObject()
                                    .put("model", "gpt-4o-mini")
                                    .put("messages", messages)
                                    .put("temperature", 0.3);

        //create headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openAiApiKey);
        headers.set("Content-Type", "application/json");

        //send request
        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                new HttpEntity<>(requestBody.toString(), headers),
                String.class
        );
    
        String chatbotReply = new JSONObject(response.getBody())
                                .getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");

        System.out.println("Saving chat content. Length: " + chatbotReply.length());

        // Save assistant response
        chatHistoryRepository.save(ChatHistory.builder()
                                    .user(user)
                                    .role("assistant")
                                    .content(chatbotReply)
                                    .timestamp(LocalDateTime.now())
                                    .build());

        return chatbotReply;
    }

    private String buildSystemPrompt(UserDetailsDTO dto, List<String> exerciseList, List<String> workoutExercises) {
        String ageStr = dto.getDob() != null ? String.valueOf(calculateAge(dto.getDob())) : "N/A";
        String heightStr = dto.getHeight() != null ? String.format("%.1f", dto.getHeight()) : "N/A";
        String weightStr = dto.getWeight() != null ? String.format("%.1f", dto.getWeight()) : "N/A";

        return """
                You are an AI fitness coach helping users get personalized workout routines based on their profile and preferences.
                
                Your main role is to assist with workouts, fitness plans, and exercise-related questions.  
                ❗ If the user asks about something unrelated to exercise or fitness (e.g., jokes, the weather, or personal questions), politely guide them back to fitness topics with a message like:  
                *"Let's stay focused on your fitness goals. How can I help with your workout today?"*

                ---

                ❗ When generating workout plans, strictly choose only from the list of supported exercises provided.  
                Do not invent new exercises or suggest ones outside the supported list.

                If the user is asking for a workout plan, respond in TWO clearly separated sections:

                ---

                **[JSON]**  
                Use this section to structure the workout for the backend. Output strictly valid JSON with the following structure. Make sure to include <BEGIN JSON> and <END JSON> tags as they are needed for parsing.:

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

                Only use the JSON format when generating a workout plan. For general fitness questions, respond naturally and conversationally without any JSON or structured format.

                ---

                User Profile:
                - Age: %s
                - Height: %s cm
                - Weight: %s kg
                - Fitness Level: %s
                - Goal: %s
                - Workout Type: %s
                - Menstrual Cramps: %s

                Supported Exercises:
                - %s

                Current Workout:
                - Exercises: %s
                """.formatted(
                    ageStr,
                    heightStr,
                    weightStr,
                    dto.getFitnessLevel(),
                    dto.getWorkoutGoal(),
                    dto.getWorkoutType(),
                    dto.getMenstrualCramps() ? "Yes" : "No",
                    String.join(", ", exerciseList),
                    String.join(", ", workoutExercises)
            );

    }

    private int calculateAge(Date dob) {
    if (dob == null) return -1;

    // Always convert to java.util.Date before using toInstant
    java.util.Date safeDate = new java.util.Date(dob.getTime());

    LocalDate birthDate = safeDate.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate();

    return Period.between(birthDate, LocalDate.now()).getYears();
}

    
}
