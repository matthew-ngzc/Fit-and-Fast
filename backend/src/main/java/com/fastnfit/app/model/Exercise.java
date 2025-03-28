// Exercise.java
package com.fastnfit.app.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "exercises")
public class Exercise {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exerciseId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    private Integer duration;
    
    private Integer rest;
    
    private String image;
    
    @Column(length = 500)
    private String tips;
    
    @ManyToMany(mappedBy = "exercises", fetch = FetchType.LAZY)
    private List<Workout> workout;
}