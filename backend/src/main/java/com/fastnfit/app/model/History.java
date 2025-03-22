// History.java
package com.fastnfit.app.model;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "history")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @Column(name = "workout_date_time")
    private Timestamp workoutDateTime;
    private String workoutName;

    @ManyToOne
    @JoinColumn(name = "workout_id")
    private Workout workout;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Added fields for tracking calories and completion
    private Integer caloriesBurned;

    private Integer durationInMinutes;
}