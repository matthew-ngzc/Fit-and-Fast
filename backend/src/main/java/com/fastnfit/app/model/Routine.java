// Routine.java
package com.fastnfit.app.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "routines")
public class Routine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routineId;

    @ManyToMany
    @JoinTable(name = "routine_workout", joinColumns = @JoinColumn(name = "routine_id"), inverseJoinColumns = @JoinColumn(name = "workout_id"))
    private List<Workout> workoutOrder = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    // Sum of all calories from all workouts
    private Integer totalCalories = 0;

    @PrePersist
    @PreUpdate
    private void calculateTotalCalories() {
        this.totalCalories = workoutOrder.stream().mapToInt(Workout::getCalories).sum();
    }
}
