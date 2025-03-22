package com.fastnfit.app.dto;

import com.fastnfit.app.enums.WorkoutLevel;
import com.fastnfit.app.enums.WorkoutType;

import lombok.Data;

@Data
public class RecommendationDTO {
    private Long workoutId;
    private String title;
    private String description;
    private WorkoutType category;
    private WorkoutLevel level;
    private Integer calories;
}