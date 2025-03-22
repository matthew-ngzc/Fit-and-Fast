package com.fastnfit.app.controller;

import com.fastnfit.app.dto.AchievementResponseDTO;
import com.fastnfit.app.dto.AvatarDTO;
import com.fastnfit.app.dto.GoalsDTO;
import com.fastnfit.app.dto.ProfileDTO;
import com.fastnfit.app.dto.WeeklyWorkoutsDTO;
import com.fastnfit.app.model.UserAchievement;
import com.fastnfit.app.service.UserAchievementService;
import com.fastnfit.app.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class UserProfileController {

    private final UserService userService;
    private final UserAchievementService userAchievementService;

    @Autowired
    public UserProfileController(UserService userService, UserAchievementService userAchievementService) {
        this.userService = userService;
        this.userAchievementService = userAchievementService;
    }

    /**
     * Get user profile information
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ProfileDTO> getUserProfile(@PathVariable Long userId) {
        ProfileDTO profileDTO = userService.getUserProfile(userId);
        return ResponseEntity.ok(profileDTO);
    }

    /**
     * Update basic user profile information
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ProfileDTO> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody ProfileDTO profileDTO) {
        ProfileDTO updatedProfile = userService.updateBasicProfile(userId, profileDTO);
        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * Update user fitness goals
     */
    @PutMapping("/{userId}/goals")
    public ResponseEntity<GoalsDTO> updateUserGoals(
            @PathVariable Long userId,
            @RequestBody GoalsDTO goalsDTO) {
        GoalsDTO updatedGoals = userService.updateUserGoals(userId, goalsDTO);
        return ResponseEntity.ok(updatedGoals);
    }

    /**
     * Update user avatar
     */
    @PutMapping("/{userId}/avatar")
    public ResponseEntity<AvatarDTO> updateUserAvatar(
            @PathVariable Long userId,
            @RequestBody AvatarDTO avatarDTO) {
        AvatarDTO updatedAvatar = userService.updateUserAvatar(userId, avatarDTO);
        return ResponseEntity.ok(updatedAvatar);
    }

    /**
     * Get user's weekly workout statistics
     */
    @GetMapping("/{userId}/weekly-workouts")
    public ResponseEntity<WeeklyWorkoutsDTO> getWeeklyWorkouts(@PathVariable Long userId) {
        WeeklyWorkoutsDTO weeklyWorkoutsDTO = userService.getWeeklyWorkouts(userId);
        return ResponseEntity.ok(weeklyWorkoutsDTO);
    }

    /**
     * Get user's achievements
     */
    @GetMapping("/{userId}/achievements")
    public ResponseEntity<List<AchievementResponseDTO>> getUserAchievements(@PathVariable Long userId) {
        List<UserAchievement> userAchievements = userAchievementService.getUserAchievements(userId);
        
        List<AchievementResponseDTO> achievementDTOs = userAchievements.stream()
            .map(ua -> new AchievementResponseDTO(
                ua.getAchievement().getTitle(),
                ua.getAchievement().getDescription(),
                ua.isCompleted()
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(achievementDTOs);
    }
}