// UserRegistrationDTO.java
package com.fastnfit.app.dto;

import lombok.Data;
import java.util.Date;
import com.fastnfit.app.enums.*;

@Data
public class UserRegistrationDTO {
    private String email;
    private String password;
    private String username;
    private Date dob;
    private Double height;
    private Double weight;
    private PregnancyStatus pregnancyStatus;
    private WorkoutGoal workoutGoal;
    private Integer workoutDays;
    private FitnessLevel fitnessLevel;
    private boolean menstrualCramps;
    private boolean cycleBasedRecommendations;
    private WorkoutType workoutType;
}