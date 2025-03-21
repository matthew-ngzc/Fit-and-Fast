// ProfileDTO.java
package com.fastnfit.app.dto;

import java.util.Date;
import com.fastnfit.app.enums.WorkoutGoal;

public class ProfileDTO {
    private String username;
    private String email;
    private Double height;
    private Double weight;
    private Date dob;
    private WorkoutGoal primaryGoal;
    private Integer workoutDaysPerWeekGoal;
    private String avatar;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}

