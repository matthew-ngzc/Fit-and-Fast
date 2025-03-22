// HistoryDTO.java
package com.fastnfit.app.dto;

import lombok.Data;
import java.util.Date;

@Data
public class HistoryDTO {
    private Long historyId;
    private Date workoutDate;
    private String name;
    private WorkoutDTO workout;
    private Integer caloriesBurned;
}
