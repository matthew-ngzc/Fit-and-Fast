// UserController.java
package com.fastnfit.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fastnfit.app.dto.QuestionnaireDTO;
import com.fastnfit.app.dto.UserDetailsDTO;
import com.fastnfit.app.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final AuthUtils authUtils;

    @Autowired
    public UserController(UserService userService,AuthUtils authUtils) {
        this.userService = userService;
        this.authUtils=authUtils;
    }

    @GetMapping("/details")
    public ResponseEntity<UserDetailsDTO> getUserDetails() {
        try {
            Long userId = authUtils.getCurrentUserId();
            UserDetailsDTO userDetailsDTO = userService.getUserDetails(userId);
            return ResponseEntity.ok(userDetailsDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/questionnaire")
    public ResponseEntity<QuestionnaireDTO> completeQuestionnaire(
            @RequestBody QuestionnaireDTO questionnaireData) {
        try {
            Long userId = authUtils.getCurrentUserId();
            QuestionnaireDTO userDetailsDTO = userService.completeUserQuestionnaire(userId, questionnaireData);
            return ResponseEntity.status(HttpStatus.CREATED).body(userDetailsDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/details")
    public ResponseEntity<UserDetailsDTO> updateUserDetails(
            @RequestBody UserDetailsDTO userDetailsDTO) {
        try {
            Long userId = authUtils.getCurrentUserId();
            UserDetailsDTO updatedDetails = userService.updateUserDetails(userId, userDetailsDTO);
            return ResponseEntity.ok(updatedDetails);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}