package com.fastnfit.app.repository;

import com.fastnfit.app.model.Exercise;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise,Long> {
    
}
