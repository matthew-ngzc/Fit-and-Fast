// HistoryDTO.java
package com.fastnfit.app.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class HistoryDTO {
    private Long historyId;
    private Date routineDate;
    private Date routineTime;
    private String name;
    private List<WorkoutDTO> workoutList;
    private List<WorkoutDTO> workoutDid;
}
