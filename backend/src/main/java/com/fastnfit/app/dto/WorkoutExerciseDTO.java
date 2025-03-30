package com.fastnfit.app.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class WorkoutExerciseDTO {
    private String name;      // exercise name
    private Integer duration; // in seconds
    private Integer rest;     // in seconds
}
