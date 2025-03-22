// History.java
package com.fastnfit.app.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "history")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @Temporal(TemporalType.DATE)
    private Date workoutDate;
    private String name;

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