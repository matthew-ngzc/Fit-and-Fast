package com.fastnfit.app.UnitTests;

import com.fastnfit.app.dto.ChatbotResponseDTO;

//./mvnw test "-Dtest=ChatbotServiceTest"

import com.fastnfit.app.dto.UserDetailsDTO;
import com.fastnfit.app.dto.WorkoutDTO;
import com.fastnfit.app.enums.FitnessLevel;
import com.fastnfit.app.model.ChatHistory;
import com.fastnfit.app.model.User;
import com.fastnfit.app.repository.ChatHistoryRepository;
import com.fastnfit.app.repository.UserRepository;
import com.fastnfit.app.service.ChatbotService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatbotServiceTest {

    @InjectMocks
    private ChatbotService chatbotService;

    @Mock
    private ChatHistoryRepository chatHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestTemplate restTemplate;

    private User testUser;
    private UserDetailsDTO testUserDetails;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);

        testUserDetails = new UserDetailsDTO();
        testUserDetails.setUserId(1L);
        testUserDetails.setFitnessLevel(FitnessLevel.Beginner);
        testUserDetails.setWorkoutGoal("WEIGHT_LOSS");
        testUserDetails.setWorkoutType("HIIT");
        testUserDetails.setMenstrualCramps(false);
        testUserDetails.setHeight(165.0);
        testUserDetails.setWeight(55.0);
    }

    @Test
    void chatbotShouldRespondWithMemory() {
        // Arrange: mock previous history
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(testUser));
        when(chatHistoryRepository.findByUserOrderByTimestampDesc(eq(testUser), any())).thenReturn(List.of(
                ChatHistory.builder()
                        .user(testUser)
                        .role("user")
                        .content("I'm feeling tired today.")
                        .timestamp(LocalDateTime.now().minusMinutes(10))
                        .build(),
                ChatHistory.builder()
                        .user(testUser)
                        .role("assistant")
                        .content("You should consider something light.")
                        .timestamp(LocalDateTime.now().minusMinutes(9))
                        .build()
        ));

        JSONObject mockResponse = new JSONObject()
                .put("choices", List.of(
                        Map.of("message", Map.of("content", "Here's a light workout plan."))
                ));

        ResponseEntity<String> mockEntity = new ResponseEntity<>(mockResponse.toString(), HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(mockEntity);

        // Act
        JSONObject request = new JSONObject();
        request.put("message", "What workout should I do?");
        request.put("exercises", List.of());
        request.put("exercises_supported", List.of(Map.of("name", "Jumping Jacks")));


        String reply = chatbotService.getResponse(request, testUserDetails).getResponse();

        // Assert
        assertTrue(reply.contains("light workout"));
        verify(chatHistoryRepository, times(2)).save(any());
    }

    @Test
    void chatbotShouldReturnWorkoutJson() {
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(testUser));
        when(chatHistoryRepository.findByUserOrderByTimestampDesc(eq(testUser), any())).thenReturn(List.of());

        JSONObject mockResponse = new JSONObject()
                .put("choices", List.of(
                        Map.of("message", Map.of("content",
                                "Sure! Here's your plan:\n<BEGIN_JSON>{ \"name\": \"Full Body Blast\" }<END_JSON>"))
                ));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(mockResponse.toString(), HttpStatus.OK));

        JSONObject request = new JSONObject();
        request.put("message", "Give me a beginner workout");
        request.put("exercises", List.of());
        request.put("exercises_supported", List.of(Map.of("name", "Jumping Jacks")));


        WorkoutDTO workout = chatbotService.getResponse(request, testUserDetails).getWorkout();
        assertTrue(workout != null);
        assertEquals("Full Body Blast", workout.getName());
    }

    @Test
    void chatbotShouldReturnConversationalAnswer() {
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(testUser));
        when(chatHistoryRepository.findByUserOrderByTimestampDesc(eq(testUser), any())).thenReturn(List.of());

        JSONObject mockResponse = new JSONObject()
                .put("choices", List.of(
                        Map.of("message", Map.of("content",
                                "Burpees are a full-body exercise that improve strength and cardio."))
                ));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(mockResponse.toString(), HttpStatus.OK));

        JSONObject request = new JSONObject();
        request.put("message", "What are burpees?");
        request.put("exercises", List.of());
        request.put("exercises_supported", List.of(Map.of("name", "Jumping Jacks")));


        ChatbotResponseDTO response = chatbotService.getResponse(request, testUserDetails);
        String reply = response.getResponse();

        assertFalse(reply.contains("<BEGIN_JSON>"));
        assertTrue(reply.contains("Burpees"));
    }
}
