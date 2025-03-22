// RoutineService.java
package com.fastnfit.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fastnfit.app.dto.RoutineDTO;
import com.fastnfit.app.dto.WorkoutDTO;
import com.fastnfit.app.model.Routine;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.Workout;
import com.fastnfit.app.repository.RoutineRepository;
import com.fastnfit.app.repository.UserRepository;
import com.fastnfit.app.repository.WorkoutRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;

    @Autowired
    public RoutineService(RoutineRepository routineRepository, 
                         UserRepository userRepository,
                         WorkoutRepository workoutRepository) {
        this.routineRepository = routineRepository;
        this.userRepository = userRepository;
        this.workoutRepository = workoutRepository;
    }

    public List<RoutineDTO> getUserRoutines(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return routineRepository.findByUser(user).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public RoutineDTO getRoutineById(Long routineId) {
        Routine routine = routineRepository.findById(routineId)
            .orElseThrow(() -> new RuntimeException("Routine not found"));
        return convertToDTO(routine);
    }

    @Transactional
    public RoutineDTO createRoutine(Long userId, RoutineDTO routineDTO) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Routine routine = new Routine();
        routine.setName(routineDTO.getName());
        routine.setUser(user);
        
        List<Workout> workouts = new ArrayList<>();
        if (routineDTO.getWorkoutOrder() != null) {
            workouts = routineDTO.getWorkoutOrder().stream()
                .map(dto -> workoutRepository.findById(dto.getWorkoutId())
                    .orElseThrow(() -> new RuntimeException("Workout not found")))
                .collect(Collectors.toList());
        }
        routine.setWorkoutOrder(workouts);
        
        Routine savedRoutine = routineRepository.save(routine);
        return convertToDTO(savedRoutine);
    }

    @Transactional
    public RoutineDTO updateRoutine(Long routineId, RoutineDTO routineDTO) {
        Routine routine = routineRepository.findById(routineId)
            .orElseThrow(() -> new RuntimeException("Routine not found"));
        
        routine.setName(routineDTO.getName());
        
        List<Workout> workouts = new ArrayList<>();
        if (routineDTO.getWorkoutOrder() != null) {
            workouts = routineDTO.getWorkoutOrder().stream()
                .map(dto -> workoutRepository.findById(dto.getWorkoutId())
                    .orElseThrow(() -> new RuntimeException("Workout not found")))
                .collect(Collectors.toList());
        }
        routine.setWorkoutOrder(workouts);
        
        Routine savedRoutine = routineRepository.save(routine);
        return convertToDTO(savedRoutine);
    }

    @Transactional
    public void deleteRoutine(Long routineId) {
        if (!routineRepository.existsById(routineId)) {
            throw new RuntimeException("Routine not found");
        }
        routineRepository.deleteById(routineId);
    }

    private RoutineDTO convertToDTO(Routine routine) {
        RoutineDTO dto = new RoutineDTO();
        dto.setRoutineId(routine.getRoutineId());
        dto.setName(routine.getName());
        
        List<WorkoutDTO> workoutDTOs = routine.getWorkoutOrder().stream()
            .map(workout -> {
                WorkoutDTO workoutDTO = new WorkoutDTO();
                workoutDTO.setWorkoutId(workout.getWorkoutId());
                workoutDTO.setCategory(workout.getCategory().getValue());
                workoutDTO.setName(workout.getName());
                workoutDTO.setDescription(workout.getDescription());
                workoutDTO.setLevel(workout.getLevel());
                workoutDTO.setCalories(workout.getCalories());
                return workoutDTO;
            })
            .collect(Collectors.toList());
        
        dto.setWorkoutOrder(workoutDTOs);
        return dto;
    }
}
