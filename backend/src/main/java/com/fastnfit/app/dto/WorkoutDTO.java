// WorkoutDTO.java
package com.fastnfit.app.dto;

import lombok.Data;

@Data
public class WorkoutDTO {
    private Long workoutId;
    private String category;
    private String name;
    private String description;
    private Integer level;
    private Integer calories;
}
