package com.fastnfit.app.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "workout_exercise")
@Data
// This class represents the many-to-many relationship between Workout and Exercise
public class WorkoutExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workout_id")
    private Workout workout;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    private Integer duration; // in seconds
    private Integer rest;     // in seconds
}
