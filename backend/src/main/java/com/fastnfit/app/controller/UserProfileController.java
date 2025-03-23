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
    private final AuthUtils authUtils;

    @Autowired
    public UserProfileController(UserService userService, UserAchievementService userAchievementService,
            AuthUtils authUtils) {
        this.userService = userService;
        this.userAchievementService = userAchievementService;
        this.authUtils = authUtils;
    }

    /**
     * Get user profile information
     */
    @GetMapping("/")
    public ResponseEntity<ProfileDTO> getUserProfile() {
        Long userId = authUtils.getCurrentUserId();
        ProfileDTO profileDTO = userService.getUserProfile(userId);
        return ResponseEntity.ok(profileDTO);
    }

    /**
     * Update basic user profile information
     */
    @PutMapping("/")
    public ResponseEntity<ProfileDTO> updateUserProfile(
            @RequestBody ProfileDTO profileDTO) {
        Long userId = authUtils.getCurrentUserId();
        ProfileDTO updatedProfile = userService.updateBasicProfile(userId, profileDTO);
        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * Update user fitness goals
     */
    @PutMapping("/goals")
    public ResponseEntity<GoalsDTO> updateUserGoals(
            @RequestBody GoalsDTO goalsDTO) {
        Long userId = authUtils.getCurrentUserId();
        GoalsDTO updatedGoals = userService.updateUserGoals(userId, goalsDTO);
        return ResponseEntity.ok(updatedGoals);
    }

    /**
     * Update user avatar
     */
    @PutMapping("/avatar")
    public ResponseEntity<AvatarDTO> updateUserAvatar(
            @RequestBody AvatarDTO avatarDTO) {
        Long userId = authUtils.getCurrentUserId();
        AvatarDTO updatedAvatar = userService.updateUserAvatar(userId, avatarDTO);
        return ResponseEntity.ok(updatedAvatar);
    }

    /**
     * Get user's weekly workout statistics
     */
    @GetMapping("/weekly-workouts")
    public ResponseEntity<WeeklyWorkoutsDTO> getWeeklyWorkouts() {
        Long userId = authUtils.getCurrentUserId();
        WeeklyWorkoutsDTO weeklyWorkoutsDTO = userService.getWeeklyWorkouts(userId);
        return ResponseEntity.ok(weeklyWorkoutsDTO);
    }

    /**
     * Get user's achievements
     */
    @GetMapping("/achievements")
    public ResponseEntity<List<AchievementResponseDTO>> getUserAchievements() {
        Long userId = authUtils.getCurrentUserId();
        List<UserAchievement> userAchievements = userAchievementService.getUserAchievements(userId);

        List<AchievementResponseDTO> achievementDTOs = userAchievements.stream()
                .map(ua -> new AchievementResponseDTO(
                        ua.getAchievement().getTitle(),
                        ua.getAchievement().getDescription(),
                        ua.isCompleted()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(achievementDTOs);
    }
}