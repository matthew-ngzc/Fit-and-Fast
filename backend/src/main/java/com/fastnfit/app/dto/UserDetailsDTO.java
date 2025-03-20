// UserDetailsDTO.java
package com.fastnfit.app.dto;

import lombok.Data;
import java.util.Date;

@Data
public class UserDetailsDTO {
    private Long userId;
    private String username;
    private Date dob;
    private Integer height;
    private Integer weight;
    private Boolean isPregnant;
    private String workoutGoal;
    private Integer workoutDays;
}
