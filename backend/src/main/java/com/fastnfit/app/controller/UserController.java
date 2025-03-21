// UserController.java
package com.fastnfit.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fastnfit.app.dto.UserDetailsDTO;
import com.fastnfit.app.dto.ProfileDTO;
import com.fastnfit.app.dto.GoalsDTO;
import com.fastnfit.app.dto.AvatarDTO;
import com.fastnfit.app.dto.WeeklyWorkoutsDTO;
import com.fastnfit.app.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}/details")
    public ResponseEntity<UserDetailsDTO> getUserDetails(@PathVariable Long userId) {
        try {
            UserDetailsDTO userDetailsDTO = userService.getUserDetails(userId);
            return ResponseEntity.ok(userDetailsDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{userId}/questionnaire")
    public ResponseEntity<UserDetailsDTO> completeQuestionnaire(
            @PathVariable Long userId,
            @RequestBody UserDetailsDTO questionnaireData) {
        try {
            UserDetailsDTO userDetailsDTO = userService.completeUserQuestionnaire(userId, questionnaireData);
            return ResponseEntity.status(HttpStatus.CREATED).body(userDetailsDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{userId}/details")
    public ResponseEntity<UserDetailsDTO> updateUserDetails(
            @PathVariable Long userId,
            @RequestBody UserDetailsDTO userDetailsDTO) {
        try {
            UserDetailsDTO updatedDetails = userService.updateUserDetails(userId, userDetailsDTO);
            return ResponseEntity.ok(updatedDetails);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/{userId}/profile")
    public ResponseEntity<ProfileDTO> getUserProfile(@PathVariable Long userId) {
        try {
            ProfileDTO profileDTO = userService.getUserProfile(userId);
            return ResponseEntity.ok(profileDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @PutMapping("/{userId}/profile")
    public ResponseEntity<ProfileDTO> updateBasicProfile(
            @PathVariable Long userId,
            @RequestBody ProfileDTO profileDTO) {
        try {
            ProfileDTO updatedProfile = userService.updateBasicProfile(userId, profileDTO);
            return ResponseEntity.ok(updatedProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @PutMapping("/{userId}/goals")
    public ResponseEntity<GoalsDTO> updateUserGoals(
            @PathVariable Long userId,
            @RequestBody GoalsDTO goalsDTO) {
        try {
            GoalsDTO updatedGoals = userService.updateUserGoals(userId, goalsDTO);
            return ResponseEntity.ok(updatedGoals);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @PutMapping("/{userId}/avatar")
    public ResponseEntity<AvatarDTO> updateUserAvatar(
            @PathVariable Long userId,
            @RequestBody AvatarDTO avatarDTO) {
        try {
            AvatarDTO updatedAvatar = userService.updateUserAvatar(userId, avatarDTO);
            return ResponseEntity.ok(updatedAvatar);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/{userId}/weekly-workouts")
    public ResponseEntity<WeeklyWorkoutsDTO> getWeeklyWorkouts(@PathVariable Long userId) {
        try {
            WeeklyWorkoutsDTO weeklyWorkouts = userService.getWeeklyWorkouts(userId);
            return ResponseEntity.ok(weeklyWorkouts);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}