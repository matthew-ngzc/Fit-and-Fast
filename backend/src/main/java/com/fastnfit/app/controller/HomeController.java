// HomeController.java
package com.fastnfit.app.controller;

import com.fastnfit.app.dto.RecommendationDTO;
import com.fastnfit.app.dto.StreakDTO;
import com.fastnfit.app.dto.WorkoutDTO;
import com.fastnfit.app.enums.WorkoutType;
import com.fastnfit.app.service.RecommendationService;
import com.fastnfit.app.service.UserStreakService;
import com.fastnfit.app.service.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/home")
@CrossOrigin(origins = "*")
public class HomeController {

    private final RecommendationService recommendationService;
    private final UserStreakService userStreakService;
    private final WorkoutService workoutService;

    @Autowired
    public HomeController(RecommendationService recommendationService, UserStreakService userStreakService, WorkoutService workoutService) {
        this.recommendationService = recommendationService;
        this.userStreakService = userStreakService;
        this.workoutService = workoutService;
    }

    /**
     * Get daily workout recommendation for the user
     */
    @GetMapping("/recommendation")
    public ResponseEntity<RecommendationDTO> getDailyRecommendation(
            @RequestParam(required = false) Long userId) {
        // Default to user ID 1 if not provided (for demo purposes)
        Long effectiveUserId = (userId != null) ? userId : 1L;
        RecommendationDTO recommendation = recommendationService.getDailyRecommendation(effectiveUserId);
        return ResponseEntity.ok(recommendation);
    }

    /**
     * Get user's current streak
     */
    @GetMapping("/streak")
    public ResponseEntity<StreakDTO> getUserStreak(
            @RequestParam(required = false) Long userId) {
        // Default to user ID 1 if not provided (for demo purposes)
        Long effectiveUserId = (userId != null) ? userId : 1L;
        StreakDTO streak = userStreakService.getUserStreak(effectiveUserId);
        return ResponseEntity.ok(streak);
    }

    /**
     * Get all workouts categorized for the home page
     */
    @GetMapping("/workouts")
    public ResponseEntity<Map<String, List<WorkoutDTO>>> getCategorizedWorkouts() {
        List<WorkoutDTO> allWorkouts = workoutService.getAllWorkouts();

        // Group workouts by category
        Map<String, List<WorkoutDTO>> workoutsByCategory = allWorkouts.stream()
                .collect(Collectors.groupingBy(workout -> {
                    String category = workout.getCategory();
                    if (category.equalsIgnoreCase("yoga"))
                        return "yoga";
                    if (category.equalsIgnoreCase("hiit"))
                        return "hiit";
                    if (category.equalsIgnoreCase("strength"))
                        return "strength";
                    if (category.equalsIgnoreCase("prenatal"))
                        return "prenatal";
                    if (category.equalsIgnoreCase("postnatal"))
                        return "postnatal";
                    return "others";
                }));

        return ResponseEntity.ok(workoutsByCategory);
    }

    /**
     * Get workouts for a specific category
     */
    @GetMapping("/workouts/{category}")
    public ResponseEntity<Map<String, List<WorkoutDTO>>> getWorkoutsByCategory(
            @PathVariable String category) {
        WorkoutType type = WorkoutType.fromString(category);
        List<WorkoutDTO> categoryWorkouts = workoutService.getWorkoutsByCategory(type);

        Map<String, List<WorkoutDTO>> result = new HashMap<>();
        result.put(category.toLowerCase(), categoryWorkouts);

        return ResponseEntity.ok(result);
    }
}
