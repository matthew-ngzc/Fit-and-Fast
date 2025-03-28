package com.fastnfit.app.dto;

import java.time.LocalDate;
import java.util.Date;

import com.fastnfit.app.enums.FitnessLevel;

import lombok.Data;

@Data
public class QuestionnaireDTO {
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
    private Integer periodLength;
    private Integer cycleLength;
    private LocalDate lastPeriodDate;


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
