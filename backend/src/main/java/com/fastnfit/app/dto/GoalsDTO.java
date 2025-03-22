// GoalsDTO.java
package com.fastnfit.app.dto;

import com.fastnfit.app.enums.WorkoutGoal;

import lombok.Data;

@Data
public class GoalsDTO {
    private WorkoutGoal workoutGoal;
    private Integer workoutDaysPerWeekGoal;
}
