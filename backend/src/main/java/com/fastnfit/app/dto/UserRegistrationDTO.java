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
    private String pregnancyStatus;
    private String workoutGoal;
    private Integer workoutDays;
    private String fitnessLevel;
    private boolean menstrualCramps;
    private boolean cycleBasedRecommendations;
    private String workoutType;
}