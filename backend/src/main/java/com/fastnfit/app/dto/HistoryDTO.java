// HistoryDTO.java
package com.fastnfit.app.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class HistoryDTO {
    private Long historyId;
    private Timestamp workoutDateTime;
    private String name;
    private WorkoutDTO workout;
    private Integer caloriesBurned;
    private Integer durationInMinutes;
}
