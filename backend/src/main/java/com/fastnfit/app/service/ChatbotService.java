package com.fastnfit.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastnfit.app.dto.ChatbotResponseDTO;
import com.fastnfit.app.dto.UserDetailsDTO;
import com.fastnfit.app.dto.WorkoutDTO;
import com.fastnfit.app.model.ChatHistory;
import com.fastnfit.app.model.Exercise;
import com.fastnfit.app.model.User;
import com.fastnfit.app.repository.ChatHistoryRepository;
import com.fastnfit.app.repository.ExerciseRepository;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

//one shot give response, not streamed
@Service
public class ChatbotService {

    private final ChatHistoryRepository chatHistoryRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final ExerciseRepository exerciseRepository;

    public ChatbotService(ChatHistoryRepository chatHistoryRepository, UserRepository userRepository,
            RestTemplate restTemplate, ExerciseRepository exerciseRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.exerciseRepository = exerciseRepository;
    }

    @Value("${openai.api.key}")
    private String openAiApiKey;

    public String getOpenaiApiKey() {
        return openAiApiKey;
    }

    /*
     * returns 2 parts to the string IF user is asking for a workout. OTHERWISE
     * chats like normal
     * 1. json, used for extracting the workoutDTO information for accepting of
     * workout
     * 2. human readable section, which is displayed to the user, include extra
     * information that we dont need for the workoutDTO
     */
    public ChatbotResponseDTO getResponse(JSONObject fullRequest, UserDetailsDTO userDetailsDTO) {
        String apiUrl = "https://api.openai.com/v1/chat/completions";
        Long userId = userDetailsDTO.getUserId();
        User user = userRepository.findById(userId).orElseThrow();

        // Extract parts from frontend JSON
        String userInput = fullRequest.getString("message");
        List<String> exerciseList = exerciseRepository.findAllExerciseNames();
        // fullRequest.getJSONArray("exercises_supported")
        //         .toList().stream().map(Object::toString).toList();
        String workoutSummary = fullRequest.getJSONArray("exercises")
                .toList().stream()
                .map(obj -> {
                    JSONObject o = new JSONObject((Map<?, ?>) obj);
                    String name = o.getString("name");
                    int duration = o.optInt("duration", 0);
                    int rest = o.optInt("rest", 0);
                    return String.format("%s (%ds work, %ds rest)", name, duration, rest);
                })
                .collect(Collectors.joining(", "));

        String systemPrompt = buildSystemPrompt(userDetailsDTO, exerciseList, workoutSummary);

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "system").put("content", systemPrompt));

        List<ChatHistory> history = new ArrayList<>(chatHistoryRepository.findByUserOrderByTimestampDesc(user,
                PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "timestamp"))));

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
                .put("content", userInput));

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

        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openAiApiKey);
        headers.set("Content-Type", "application/json");

        // send request
        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                new HttpEntity<>(requestBody.toString(), headers),
                String.class
        );

        System.out.println("\n\n=== OpenAI Request JSON ===\n" + requestBody.toString(2) + "\n\n");

    
        String chatbotReply = new JSONObject(response.getBody())
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");

        System.out.println("\n\n\nChatbot reply: " + chatbotReply + "\n\n");

        // extract the JSON and Natural Language segments
        Pattern jsonPattern = Pattern.compile("<BEGIN_JSON>\\s*([\\s\\S]*?)\\s*<END_JSON>");
        Matcher matcher = jsonPattern.matcher(chatbotReply);

        String jsonPart = null;
        String responsePart = chatbotReply;

        if (matcher.find()) {
            jsonPart = matcher.group(1).trim();
            responsePart = chatbotReply.substring(matcher.end()).trim();
        }

        ObjectMapper mapper = new ObjectMapper();
        // Convert JSON into DTO
        WorkoutDTO workout = null;
        if (jsonPart != null) {
            try {
                workout = mapper.readValue(jsonPart, WorkoutDTO.class);
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                System.err.println("Failed to parse workout JSON: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Wrap into chatbot response DTO
        ChatbotResponseDTO result = new ChatbotResponseDTO(workout, responsePart);

        System.out.println("Saving chat content. Length: " + chatbotReply.length());

        // Save assistant response
        chatHistoryRepository.save(ChatHistory.builder()
                .user(user)
                .role("assistant")
                .content(chatbotReply)
                .timestamp(LocalDateTime.now())
                .build());

        return result;
    }

    private String buildSystemPrompt(UserDetailsDTO dto, List<String> exerciseList, String workoutSummary) {
        String ageStr = dto.getDob() != null ? String.valueOf(calculateAge(dto.getDob())) : "N/A";
        String heightStr = dto.getHeight() != null ? String.format("%.1f", dto.getHeight()) : "N/A";
        String weightStr = dto.getWeight() != null ? String.format("%.1f", dto.getWeight()) : "N/A";

        return """
                You are an AI fitness coach helping users get personalized workout routines based on their profile and preferences.

                Your main role is to assist with workouts, fitness plans, and exercise-related questions.
                You should only redirect the user back to fitness topics if they ask questions that are clearly unrelated, like jokes, the weather, celebrity gossip, or personal questions about the AI.

                ✅ Acceptable fitness-related topics include:
                - Questions about workout frequency or schedule (e.g., "How often do I exercise?")
                - Questions about current or past workouts
                - Fitness goals and progress
                - Preferences or feedback about previous workouts

                If a question is even slightly related to exercise, workouts, or fitness habits, always treat it seriously and respond appropriately.


                ---

                When generating workout plans, strictly choose only from the list of supported exercises provided.
                Do not invent new exercises or suggest ones outside the supported list.
                By default, the total duration of the workout should be 7 minutes. This is because our main target audience is busy women professionals who do not have time for a longer workout.

                🧠 If the user message contains ANY indication that they want a workout — such as words like "suggest a workout", "routine", "exercise plan", "workout for today", or "gentle session" — you MUST interpret it as a workout request and follow the two-section response format: a JSON block inside <BEGIN_JSON> and <END_JSON>, and a natural language section.
                Do NOT wait for the user to use the word “plan”. Treat any phrasing like “workout for today” or “something gentle” as a valid request for a workout plan.

                ⚠️ You must ALWAYS output <BEGIN_JSON>...</END_JSON> if recommending a workout.
                If the user is asking for a workout plan, respond in TWO clearly separated sections:

                ---

                **[JSON]**
                Use this section to structure the workout for the backend. Output strictly valid JSON with the following structure. Make sure to include <BEGIN JSON> and <END JSON> tags as they are needed for parsing.:

                🛑 DO NOT include exercise IDs.
                ✅ Only use the format: name, duration, rest.

                • `"level"` must be one of:
                    - "Beginner"
                    - "Intermediate"
                    - "Advanced"
                    - "All_Levels"

                • `"category"` must be one of:
                    - "low-impact"
                    - "others"
                    - "prenatal"
                    - "postnatal"
                    - "yoga"
                    - "HIIT"
                    - "strength"
                    - "body-weight"

                Example:

                <BEGIN_JSON>
                {
                "name": "Workout Title",
                "description": "Lower body strength and power workout",
                "durationInMinutes": 20,
                "calories": 180,
                "level": "Beginner",
                "category": "strength",
                "workoutExercise": [
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

                Strictly only use exercises from the supported exercise list.
                Do not invent new exercises.
                Do not add IDs - only include name, duration, and rest for each exercise.


                Current Workout:
                - Exercises: %s
                """
                .formatted(
                        ageStr,
                        heightStr,
                        weightStr,
                        dto.getFitnessLevel(),
                        dto.getWorkoutGoal(),
                        dto.getWorkoutType(),
                        dto.getMenstrualCramps() ? "Yes" : "No",
                        String.join(", ", exerciseList),
                        workoutSummary);
    }

    private int calculateAge(LocalDate dob) {
        if (dob == null)
            return -1;

        // Always convert to java.util.Date before using toInstant
        // java.util.Date safeDate = new java.util.Date(dob.getTime());

        // LocalDate birthDate = safeDate.toInstant()
        // .atZone(ZoneId.systemDefault())
        // .toLocalDate();

        return Period.between(dob, LocalDate.now()).getYears();
    }

}
