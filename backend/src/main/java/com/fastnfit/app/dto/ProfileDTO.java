// ProfileDTO.java
package com.fastnfit.app.dto;

import java.util.Date;
import com.fastnfit.app.enums.WorkoutGoal;

import lombok.Data;

@Data
public class ProfileDTO {
    private String username;
    private String email;
    private Double height;
    private Double weight;
    private Date dob;
    private WorkoutGoal workoutGoal;
    private Integer workoutDays;
    private String avatar;
}

