package com.fastnfit.app.dto;

import lombok.Data;

@Data
public class WorkoutCompletionResponse {
    private Long historyId;
    private Long userId;
    private Long workoutId;
    private String workoutName;
    private int caloriesBurned;
    private int totalWorkouts;
    private int totalCaloriesBurned;
    private int totalDurationInMinutes;
}
