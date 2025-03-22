// Workout.java
package com.fastnfit.app.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import com.fastnfit.app.enums.WorkoutLevel;
import com.fastnfit.app.enums.WorkoutType;

@Data
@Entity
@Table(name = "workouts")
public class Workout {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workoutId;
    
    @Enumerated(EnumType.STRING)
    private WorkoutType category;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    private String workoutTips;

    @Enumerated(EnumType.STRING)
    private WorkoutLevel level;
    
    private Integer calories;
    private Integer durationInMinutes;
    
    @OneToMany(mappedBy = "workout")
    private List<History> historyWorkoutList;

    @OneToMany(mappedBy = "workout", fetch = FetchType.LAZY)
    private List<Exercise> exercises;
}
