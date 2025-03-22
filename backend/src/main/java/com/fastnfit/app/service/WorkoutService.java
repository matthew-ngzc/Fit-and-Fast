// WorkoutService.java
package com.fastnfit.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastnfit.app.dto.WorkoutDTO;
import com.fastnfit.app.enums.WorkoutLevel;
import com.fastnfit.app.enums.WorkoutType;
import com.fastnfit.app.model.Workout;
import com.fastnfit.app.repository.WorkoutRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;

    @Autowired
    public WorkoutService(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    public List<WorkoutDTO> getAllWorkouts() {
        return workoutRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public WorkoutDTO getWorkoutById(Long id) {
        Workout workout = workoutRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Workout not found"));
        return convertToDTO(workout);
    }

    public List<WorkoutDTO> getWorkoutsByCategory(WorkoutType category) {
        return workoutRepository.findByCategory(category).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<WorkoutDTO> getWorkoutsByLevel(WorkoutLevel level) {
        return workoutRepository.findByLevel(level).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public WorkoutDTO convertToDTO(Workout workout) {
        WorkoutDTO dto = new WorkoutDTO();
        dto.setWorkoutId(workout.getWorkoutId());
        dto.setCategory(workout.getCategory().getValue());
        dto.setName(workout.getName());
        dto.setDescription(workout.getDescription());
        dto.setLevel(workout.getLevel());
        dto.setCalories(workout.getCalories());
        dto.setDurationInMinutes(workout.getDurationInMinutes());
        return dto;
    }
}
