package com.fastnfit.app.controller;

import com.fastnfit.app.dto.UserDetailsDTO;
import com.fastnfit.app.service.UserService;
import com.fastnfit.app.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    private UserService userService;

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/{userId}")
    public ResponseEntity<String> chat(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> payload
    ) {
        try {
            String userMessage = (String) payload.get("message");
            Map<String, Object> workout = (Map<String, Object>) payload.get("currentWorkout");

            UserDetailsDTO userDetails = userService.getUserDetails(userId);
            String response = chatbotService.getResponse(userMessage, userDetails, workout);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to process chatbot message.");
        }
    }
}
