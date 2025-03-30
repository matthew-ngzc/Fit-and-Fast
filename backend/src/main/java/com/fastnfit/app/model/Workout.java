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
    
    // @OneToMany(mappedBy = "workout",fetch=FetchType.LAZY)
    // private List<History> historyWorkoutList;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "workout_exercise",
        joinColumns=@JoinColumn(name="workout_id"),
        inverseJoinColumns=@JoinColumn(name="exercise_id")
    )
    private List<Exercise> exercises;
}
