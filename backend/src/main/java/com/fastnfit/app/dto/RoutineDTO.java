// RoutineDTO.java
package com.fastnfit.app.dto;

import lombok.Data;
import java.util.List;

@Data
public class RoutineDTO {
    private Long routineId;
    private String name;
    private List<WorkoutDTO> workoutOrder;
    private Integer totalCalories;
    private String image;
}
