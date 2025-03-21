// GoalsDTO.java
package com.fastnfit.app.dto;

import com.fastnfit.app.enums.WorkoutGoal;

public class GoalsDTO {
    private WorkoutGoal primaryGoal;
    private Integer workoutDaysPerWeekGoal;

    // Getters and Setters
    public WorkoutGoal getPrimaryGoal() {
        return primaryGoal;
    }

    public void setPrimaryGoal(WorkoutGoal primaryGoal) {
        this.primaryGoal = primaryGoal;
    }

    public Integer getWorkoutDaysPerWeekGoal() {
        return workoutDaysPerWeekGoal;
    }

    public void setWorkoutDaysPerWeekGoal(Integer workoutDaysPerWeekGoal) {
        this.workoutDaysPerWeekGoal = workoutDaysPerWeekGoal;
    }
}
