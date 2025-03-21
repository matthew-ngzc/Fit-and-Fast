// WorkoutDTO.java
package com.fastnfit.app.dto;

import lombok.Data;
import com.fastnfit.app.enums.WorkoutLevel;

@Data
public class WorkoutDTO {
    private Long workoutId;
    private String category;
    private String name;
    private String description;
    private WorkoutLevel level;
    private Integer calories;
}
