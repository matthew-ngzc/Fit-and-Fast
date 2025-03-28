package com.fastnfit.app.repository;

import com.fastnfit.app.model.Exercise;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise,Long> {
    //so that when AI returns the name we can get the exercise we want
    Optional<Exercise> findByName(String name);

    //retrieve the names of all exercises we have to pass to AI
    @Query("SELECT e.name FROM Exercise e WHERE e.name IS NOT NULL")
    List<String> findAllExerciseNames();
    
}
