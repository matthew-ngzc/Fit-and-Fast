package com.fastnfit.app.UnitTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fastnfit.app.dto.WorkoutDTO;
import com.fastnfit.app.enums.WorkoutLevel;
import com.fastnfit.app.enums.WorkoutType;
import com.fastnfit.app.model.Workout;
import com.fastnfit.app.repository.WorkoutRepository;
import com.fastnfit.app.service.WorkoutService;

public class WorkoutServiceTest {

    @Mock
    private WorkoutRepository workoutRepository;
    
    @InjectMocks
    private WorkoutService workoutService;
    
    private Workout testWorkout1;
    private Workout testWorkout2;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Create test workouts
        testWorkout1 = new Workout();
        testWorkout1.setWorkoutId(1L);
        testWorkout1.setName("Push-ups");
        testWorkout1.setDescription("Basic upper body exercise");
        testWorkout1.setCategory(WorkoutType.STRENGTH);
        testWorkout1.setLevel(WorkoutLevel.Beginner);
        testWorkout1.setCalories(100);
        
        testWorkout2 = new Workout();
        testWorkout2.setWorkoutId(2L);
        testWorkout2.setName("Running");
        testWorkout2.setDescription("Cardio exercise");
        testWorkout2.setCategory(WorkoutType.LOW_IMPACT);
        testWorkout2.setLevel(WorkoutLevel.Intermediate);
        testWorkout2.setCalories(200);
    }
    
    @Test
    public void testGetAllWorkouts() {
        // Arrange
        when(workoutRepository.findAll()).thenReturn(Arrays.asList(testWorkout1, testWorkout2));
        
        // Act
        List<WorkoutDTO> result = workoutService.getAllWorkouts();
        
        // Assert
        assertEquals(2, result.size());
        assertEquals("Push-ups", result.get(0).getName());
        assertEquals("Running", result.get(1).getName());
        assertEquals("Strength", result.get(0).getCategory());
        assertEquals("high-energy", result.get(1).getCategory());
        verify(workoutRepository, times(1)).findAll();
    }
    
    @Test
    public void testGetWorkoutById() {
        // Arrange
        when(workoutRepository.findById(1L)).thenReturn(Optional.of(testWorkout1));
        
        // Act
        WorkoutDTO result = workoutService.getWorkoutById(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getWorkoutId());
        assertEquals("Push-ups", result.getName());
        assertEquals("Basic upper body exercise", result.getDescription());
        assertEquals("Strength", result.getCategory());
        assertEquals(WorkoutLevel.Beginner, result.getLevel());
        assertEquals(100, result.getCalories());
        verify(workoutRepository, times(1)).findById(1L);
    }
    
    @Test
    public void testGetWorkoutByIdNotFound() {
        // Arrange
        when(workoutRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            workoutService.getWorkoutById(999L);
        });
        
        assertEquals("Workout not found", exception.getMessage());
        verify(workoutRepository, times(1)).findById(999L);
    }
    
    @Test
    public void testGetWorkoutsByCategory() {
        // Arrange
        when(workoutRepository.findByCategory(WorkoutType.STRENGTH)).thenReturn(Arrays.asList(testWorkout1));
        
        // Act
        List<WorkoutDTO> result = workoutService.getWorkoutsByCategory(WorkoutType.STRENGTH);
        
        // Assert
        assertEquals(1, result.size());
        assertEquals("Push-ups", result.get(0).getName());
        assertEquals("Strength", result.get(0).getCategory());
        verify(workoutRepository, times(1)).findByCategory(WorkoutType.STRENGTH);
    }
    
    @Test
    public void testGetWorkoutsByLevel() {
        // Arrange
        when(workoutRepository.findByLevel(WorkoutLevel.Intermediate)).thenReturn(Arrays.asList(testWorkout2));
        
        // Act
        List<WorkoutDTO> result = workoutService.getWorkoutsByLevel(WorkoutLevel.Intermediate);
        
        // Assert
        assertEquals(1, result.size());
        assertEquals("Running", result.get(0).getName());
        assertEquals(WorkoutLevel.Intermediate, result.get(0).getLevel());
        verify(workoutRepository, times(1)).findByLevel(WorkoutLevel.Intermediate);
    }
    
    @Test
    public void testConvertToDTO() {
        // Act
        WorkoutDTO result = workoutService.convertToDTO(testWorkout1);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getWorkoutId());
        assertEquals("Push-ups", result.getName());
        assertEquals("Basic upper body exercise", result.getDescription());
        assertEquals("Strength", result.getCategory());
        assertEquals(WorkoutLevel.Beginner, result.getLevel());
        assertEquals(100, result.getCalories());
    }
}