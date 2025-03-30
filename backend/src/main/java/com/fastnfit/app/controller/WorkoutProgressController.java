package com.fastnfit.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fastnfit.app.dto.HistoryDTO;
import com.fastnfit.app.dto.WorkoutDTO;
import com.fastnfit.app.dto.WorkoutCompletionRequest;
import com.fastnfit.app.dto.WorkoutCompletionResponse;
import com.fastnfit.app.service.HistoryService;
import com.fastnfit.app.service.WorkoutService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workout-progress")
@CrossOrigin(origins = "*")
public class WorkoutProgressController {

    private final WorkoutService workoutService;
    private final HistoryService historyService;
    private final AuthUtils authUtils;

    @Autowired
    public WorkoutProgressController(
            WorkoutService workoutService,
            HistoryService historyService,
            AuthUtils authUtils
            ) {
        this.workoutService = workoutService;
        this.historyService = historyService;
        this.authUtils=authUtils;
    }

    /**
     * GET endpoint to retrieve workouts ordered by workout ID
     * 
     * @return List of workouts sorted by ID
     */
    @GetMapping("/workouts/ordered")
    public ResponseEntity<List<WorkoutDTO>> getWorkoutsInOrder() {
        List<WorkoutDTO> workouts = workoutService.getAllWorkouts().stream()
                .sorted(Comparator.comparing(WorkoutDTO::getWorkoutId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(workouts);
    }

    /**
     * POST endpoint for users to log a completed workout
     * This will update their workout logs, calories burned, and streak
     * 
     * @param userId The ID of the user who completed the workout, in token
     * @param request The workout completion request containing workout ID
     * @return Workout completion response with updated statistics
     */
    @PostMapping("/complete")
    public ResponseEntity<WorkoutCompletionResponse> completeWorkout(
            @RequestBody WorkoutCompletionRequest request) {
        
        Long userId = authUtils.getCurrentUserId();
        // Get the workout details
        WorkoutDTO workout = workoutService.getWorkoutById(request.getWorkoutId());
        
        // Record the workout completion in history
        HistoryDTO historyDTO = historyService.recordWorkoutCompletion(userId, workout);
        
        // Get updated stats for the response
        int totalWorkouts = historyService.getTotalHistoryCountByUser(userId);
        int totalCaloriesBurned = historyService.getTotalCaloriesBurnedByUser(userId);
        int totalDurationInMinutes=historyService.getTotalDurationByUser(userId);
        
        // Create response with updated user stats
        WorkoutCompletionResponse response = new WorkoutCompletionResponse();
        response.setHistoryId(historyDTO.getHistoryId());
        response.setUserId(userId);
        response.setWorkoutId(workout.getWorkoutId());
        response.setWorkoutName(workout.getName());
        response.setCaloriesBurned(workout.getCalories());
        response.setTotalWorkouts(totalWorkouts);
        response.setTotalCaloriesBurned(totalCaloriesBurned);
        response.setTotalDurationInMinutes(totalDurationInMinutes);
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}