// WorkoutController.java
package com.fastnfit.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fastnfit.app.dto.WorkoutDTO;
import com.fastnfit.app.dto.WorkoutExerciseDTO;
import com.fastnfit.app.enums.WorkoutLevel;
import com.fastnfit.app.enums.WorkoutType;
import com.fastnfit.app.service.WorkoutService;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/workouts")
@CrossOrigin(origins = "*")
public class WorkoutController {

    private final WorkoutService workoutService;

    @Autowired
    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @GetMapping
    public ResponseEntity<List<WorkoutDTO>> getAllWorkouts() {
        List<WorkoutDTO> workouts = workoutService.getAllWorkouts();
        return ResponseEntity.ok(workouts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutDTO> getWorkoutById(@PathVariable Long id) {
        WorkoutDTO workout = workoutService.getWorkoutById(id);
        return ResponseEntity.ok(workout);
    }

    @GetMapping("/{id}/exercises")
    public ResponseEntity<List<WorkoutExerciseDTO>> getWorkoutExercises(@PathVariable Long id) {
        return ResponseEntity.ok(workoutService.getWorkoutsWorkoutExercisesById(id));
    }
    

    @GetMapping("/category/{category}")
    public ResponseEntity<List<WorkoutDTO>> getWorkoutsByCategory(@PathVariable String category) {
        WorkoutType type=WorkoutType.fromString(category);
        List<WorkoutDTO> workouts = workoutService.getWorkoutsByCategory(type);
        return ResponseEntity.ok(workouts);
    }

    @GetMapping("/level/{level}")
    public ResponseEntity<List<WorkoutDTO>> getWorkoutsByLevel(@PathVariable String level) {
        List<WorkoutDTO> workouts = workoutService.getWorkoutsByLevel(WorkoutLevel.valueOf(level));
        return ResponseEntity.ok(workouts);
    }
}