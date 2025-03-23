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
import org.springframework.http.HttpStatus;
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
    private final AuthUtils authUtils;

    @Autowired
    public HomeController(RecommendationService recommendationService, UserStreakService userStreakService,
            WorkoutService workoutService,
            AuthUtils authUtils) {
        this.recommendationService = recommendationService;
        this.userStreakService = userStreakService;
        this.workoutService = workoutService;
        this.authUtils = authUtils;
    }

    /**
     * Get daily workout recommendation for the user
     */
    @GetMapping("/recommendation")
    public ResponseEntity<RecommendationDTO> getDailyRecommendation() {
        Long userId = authUtils.getCurrentUserId();
        RecommendationDTO recommendation = recommendationService.getDailyRecommendation(userId);
        return ResponseEntity.ok(recommendation);
    }

    /**
     * Get user's current streak
     */
    @GetMapping("/streak")
    public ResponseEntity<StreakDTO> getUserStreak() {
        Long userId = authUtils.getCurrentUserId();
        StreakDTO streak = userStreakService.getUserStreak(userId);
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
        try {
            WorkoutType type = WorkoutType.fromString(category);
            List<WorkoutDTO> categoryWorkouts = workoutService.getWorkoutsByCategory(type);

            Map<String, List<WorkoutDTO>> result = new HashMap<>();
            result.put(category.toLowerCase(), categoryWorkouts);

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
