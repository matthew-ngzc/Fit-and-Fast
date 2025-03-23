package com.fastnfit.app.dto;

import lombok.Data;

@Data
public class ExerciseDTO {

    // Used when linking existing exercises from the frontend or AI
    private Long exerciseId;

    // Used when displaying exercise info in a workout
    private String name;
    private Integer duration; // in seconds
    private Integer rest;     // in seconds

    // Optional: for display in UI or AI preview
    private String description;
    private String image;
    private String tips;
}
