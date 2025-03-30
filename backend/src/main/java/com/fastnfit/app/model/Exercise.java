// Exercise.java
package com.fastnfit.app.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "exercises")
public class Exercise {
    
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    // private Long exerciseId;
    
    @Id
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    
    
    // @Column(length = 1000)
    // private String description;
    
    // //removed cos we dw each exercise to have a fixed duration and rest time, it should be set in the workout
    
    // // private Integer duration;
    
    // // private Integer rest;
    
    // private String image;
    
    // @Column(length = 500)
    // private String tips;
    
    // @ManyToMany(mappedBy = "exercises", fetch = FetchType.LAZY)
    // private List<Workout> workout;
}