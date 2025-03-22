// UserDetailsDTO.java
package com.fastnfit.app.dto;

import lombok.Data;
import java.util.Date;
import com.fastnfit.app.enums.*;

@Data
public class UserDetailsDTO {
    private Long userId;
    private String username;
    private Date dob;
    private Double height;
    private Double weight;
    private String pregnancyStatus;
    private String workoutGoal;
    private Integer workoutDays;
    private FitnessLevel fitnessLevel;
    private boolean menstrualCramps;
    private boolean cycleBasedRecommendations;
    private String workoutType;
    private Integer currentStreak;
    private Integer longestStreak;

    public boolean getMenstrualCramps(){
        return this.menstrualCramps;
    }

    public void setMenstrualCramps(boolean menstrualCramps){
        this.menstrualCramps=menstrualCramps;
    }

    public boolean getCycleBasedRecommendations(){
        return this.cycleBasedRecommendations;
    }

    public void setCycleBasedRecommendations(boolean cycleBasedRecommendations){
        this.cycleBasedRecommendations=cycleBasedRecommendations;
    }
}
