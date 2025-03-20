// WorkoutRepository.java
package com.fastnfit.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fastnfit.app.model.Workout;

import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    List<Workout> findByCategory(String category);
    List<Workout> findByLevel(Integer level);
}
