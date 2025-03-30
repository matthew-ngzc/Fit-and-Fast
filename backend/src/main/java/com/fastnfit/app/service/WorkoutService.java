package com.fastnfit.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fastnfit.app.dto.WorkoutDTO;
import com.fastnfit.app.dto.WorkoutExerciseDTO;
import com.fastnfit.app.enums.WorkoutLevel;
import com.fastnfit.app.enums.WorkoutType;
import com.fastnfit.app.model.Exercise;
import com.fastnfit.app.model.Workout;
import com.fastnfit.app.model.WorkoutExercise;
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
        dto.setWorkoutExercise(convertWorkoutExerciseToDTO(workout.getWorkoutExercises()));
        
        return dto;
    }

    public List<WorkoutExerciseDTO> convertWorkoutExerciseToDTO(List<WorkoutExercise> exerciseList) {
        List<WorkoutExerciseDTO> workoutExerciseDTO = new ArrayList<>();
        for (int i=0;i<exerciseList.size();i++){
            //create dto and set values
            WorkoutExerciseDTO workoutExercise= new WorkoutExerciseDTO();
            workoutExercise.setDuration(exerciseList.get(i).getDuration());
            workoutExercise.setRest(exerciseList.get(i).getRest());
            workoutExercise.setName(exerciseList.get(i).getExercise().getName());

            workoutExerciseDTO.add(workoutExercise);
        }
        return workoutExerciseDTO;
    }

    public List<WorkoutExercise> convertDTOToWorkoutExercise(List<WorkoutExerciseDTO> dtoList, Workout workout) {
        List<WorkoutExercise> workoutExerciseList = new ArrayList<>();
        for (WorkoutExerciseDTO dto : dtoList) {
            WorkoutExercise workoutExercise = new WorkoutExercise();
            workoutExercise.setDuration(dto.getDuration());
            workoutExercise.setRest(dto.getRest());
            workoutExercise.setWorkout(workout);
    
            Exercise exercise = exerciseRepository.findByName(dto.getName())
                    .orElseThrow(() -> new RuntimeException("Exercise not found: " + dto.getName()));
            workoutExercise.setExercise(exercise);
    
            workoutExerciseList.add(workoutExercise);
        }
        return workoutExerciseList;
    }
    

    @Transactional
    public WorkoutDTO saveCustomWorkoutForUser(WorkoutDTO workoutDTO) {
        Workout workout = new Workout();
        workout.setName(workoutDTO.getName());
        workout.setDescription(workoutDTO.getDescription());
        workout.setDurationInMinutes(workoutDTO.getDurationInMinutes());
        workout.setCalories(workoutDTO.getCalories());
        workout.setLevel(workoutDTO.getLevel());
        workout.setCategory(WorkoutType.fromString(workoutDTO.getCategory()));
        workout.setWorkoutExercises(convertDTOToWorkoutExercise(workoutDTO.getWorkoutExercise(), workout));

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
        existingWorkout.setWorkoutExercises(convertDTOToWorkoutExercise(workoutDTO.getWorkoutExercise(), existingWorkout));

        Workout savedWorkout = workoutRepository.save(existingWorkout);
        return convertToDTO(savedWorkout);
    }

    @Transactional
    public void deleteWorkout(Long workoutId) {
        Workout workout = workoutRepository.findById(workoutId)
            .orElseThrow(() -> new RuntimeException("Workout not found"));
        
        workoutRepository.delete(workout);
    }
}