// Workout.java
package com.fastnfit.app.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "workouts")
public class Workout {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workoutId;
    
    @Column(nullable = false)
    private String category;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    private Integer level;
    
    private Integer calories;
    
    @ManyToMany(mappedBy = "workoutOrder")
    private List<Routine> routines;
    
    @ManyToMany(mappedBy = "workoutList")
    private List<History> historyWorkoutList;
    
    @ManyToMany(mappedBy = "workoutDid")
    private List<History> historyWorkoutDid;
}
