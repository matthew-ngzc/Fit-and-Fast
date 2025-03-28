package com.fastnfit.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fastnfit.app.dto.WorkoutDTO;
import com.fastnfit.app.enums.WorkoutLevel;
import com.fastnfit.app.enums.WorkoutType;
import com.fastnfit.app.model.Exercise;
import com.fastnfit.app.model.Workout;
import com.fastnfit.app.repository.ExerciseRepository;
import com.fastnfit.app.repository.WorkoutRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkoutService {

    @Autowired
    private final WorkoutRepository workoutRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

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
        
        // Include exercises in the DTO
        if (workout.getExercises() != null) {
            dto.setExercises(convertExercisesToDTO(workout.getExercises()));
        }
        
        return dto;
    }

    public List<Long> convertExercisesToDTO(List<Exercise> exerciseList) {
        List<Long> exerciseIdList=new ArrayList<>();
        for (int i=0;i<exerciseList.size();i++){
            exerciseIdList.add(exerciseList.get(i).getExerciseId());
        }
        return exerciseIdList;
    }

    public List<Exercise> convertDTOToExercises(List<Long> dtoList) {
        List<Exercise> exerciseList=new ArrayList<>();
        for (int i=0;i<dtoList.size();i++){
            Long exerciseId=dtoList.get(i);
            Exercise exercise=exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found: " + exerciseId));
            exerciseList.add(exercise);
        }

        return exerciseList;
    }

    @Transactional
    public WorkoutDTO saveCustomWorkoutForUser(Long userId, WorkoutDTO workoutDTO) {
        Workout workout = new Workout();
        workout.setName(workoutDTO.getName());
        workout.setDescription(workoutDTO.getDescription());
        workout.setDurationInMinutes(workoutDTO.getDurationInMinutes());
        workout.setCalories(workoutDTO.getCalories());
        workout.setLevel(workoutDTO.getLevel());
        workout.setCategory(WorkoutType.fromString(workoutDTO.getCategory()));

        // Handle exercises
        if (workoutDTO.getExercises() != null && !workoutDTO.getExercises().isEmpty()) {
            List<Exercise> exercises = convertDTOToExercises(workoutDTO.getExercises());
            workout.setExercises(exercises);
        }

        Workout savedWorkout = workoutRepository.save(workout);

        return convertToDTO(savedWorkout);
    }

    @Transactional
    public WorkoutDTO updateWorkout(Long workoutId, WorkoutDTO workoutDTO) {
        Workout existingWorkout = workoutRepository.findById(workoutId)
            .orElseThrow(() -> new RuntimeException("Workout not found"));

        // Update basic workout details
        existingWorkout.setName(workoutDTO.getName());
        existingWorkout.setDescription(workoutDTO.getDescription());
        existingWorkout.setDurationInMinutes(workoutDTO.getDurationInMinutes());
        existingWorkout.setCalories(workoutDTO.getCalories());
        existingWorkout.setLevel(workoutDTO.getLevel());
        existingWorkout.setCategory(WorkoutType.fromString(workoutDTO.getCategory()));

        // Update exercises
        if (workoutDTO.getExercises() != null && !workoutDTO.getExercises().isEmpty()) {
            List<Exercise> updatedExercises = convertDTOToExercises(workoutDTO.getExercises());
            existingWorkout.setExercises(updatedExercises);
        } else {
            // Clear exercises if no exercises are provided
            existingWorkout.setExercises(null);
        }

        Workout savedWorkout = workoutRepository.save(existingWorkout);
        return convertToDTO(savedWorkout);
    }

    @Transactional
    public void deleteWorkout(Long workoutId) {
        Workout workout = workoutRepository.findById(workoutId)
            .orElseThrow(() -> new RuntimeException("Workout not found"));
        
        // Remove associations with exercises
        if (workout.getExercises() != null) {
            workout.getExercises().clear();
        }

        workoutRepository.delete(workout);
    }
}