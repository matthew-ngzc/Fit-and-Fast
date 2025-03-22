package com.fastnfit.app.UnitTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fastnfit.app.dto.RoutineDTO;
import com.fastnfit.app.dto.WorkoutDTO;
import com.fastnfit.app.enums.WorkoutLevel;
import com.fastnfit.app.enums.WorkoutType;
import com.fastnfit.app.model.Routine;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.Workout;
import com.fastnfit.app.repository.RoutineRepository;
import com.fastnfit.app.repository.UserRepository;
import com.fastnfit.app.repository.WorkoutRepository;
import com.fastnfit.app.service.RoutineService;

public class RoutineServiceTest {

    @Mock
    private RoutineRepository routineRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private WorkoutRepository workoutRepository;
    
    @InjectMocks
    private RoutineService routineService;
    
    private User testUser;
    private Workout testWorkout1;
    private Workout testWorkout2;
    private Routine testRoutine;
    private WorkoutDTO testWorkoutDTO1;
    private WorkoutDTO testWorkoutDTO2;
    private RoutineDTO testRoutineDTO;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Create test user
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        
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
        testWorkout2.setName("Squats");
        testWorkout2.setDescription("Basic lower body exercise");
        testWorkout2.setCategory(WorkoutType.STRENGTH);
        testWorkout2.setLevel(WorkoutLevel.Beginner);
        testWorkout2.setCalories(150);
        
        // Create test routine
        testRoutine = new Routine();
        testRoutine.setRoutineId(1L);
        testRoutine.setName("Full Body Workout");
        testRoutine.setUser(testUser);
        testRoutine.setWorkoutOrder(Arrays.asList(testWorkout1, testWorkout2));
        
        // Create test DTOs
        testWorkoutDTO1 = new WorkoutDTO();
        testWorkoutDTO1.setWorkoutId(1L);
        testWorkoutDTO1.setName("Push-ups");
        testWorkoutDTO1.setDescription("Basic upper body exercise");
        testWorkoutDTO1.setCategory("STRENGTH");
        testWorkoutDTO1.setLevel(WorkoutLevel.Beginner);
        testWorkoutDTO1.setCalories(100);
        
        testWorkoutDTO2 = new WorkoutDTO();
        testWorkoutDTO2.setWorkoutId(2L);
        testWorkoutDTO2.setName("Squats");
        testWorkoutDTO2.setDescription("Basic lower body exercise");
        testWorkoutDTO2.setCategory("STRENGTH");
        testWorkoutDTO2.setLevel(WorkoutLevel.Beginner);
        testWorkoutDTO2.setCalories(150);
        
        testRoutineDTO = new RoutineDTO();
        testRoutineDTO.setRoutineId(1L);
        testRoutineDTO.setName("Full Body Workout");
        testRoutineDTO.setWorkoutOrder(Arrays.asList(testWorkoutDTO1, testWorkoutDTO2));
    }
    
    @Test
    public void testGetUserRoutines() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(routineRepository.findByUser(testUser)).thenReturn(Arrays.asList(testRoutine));
        
        // Act
        List<RoutineDTO> result = routineService.getUserRoutines(1L);
        
        // Assert
        assertEquals(1, result.size());
        assertEquals("Full Body Workout", result.get(0).getName());
        assertEquals(2, result.get(0).getWorkoutOrder().size());
        verify(userRepository, times(1)).findById(1L);
        verify(routineRepository, times(1)).findByUser(testUser);
    }
    
    @Test
    public void testGetUserRoutinesUserNotFound() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            routineService.getUserRoutines(999L);
        });
        
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
    }
    
    @Test
    public void testGetRoutineById() {
        // Arrange
        when(routineRepository.findById(1L)).thenReturn(Optional.of(testRoutine));
        
        // Act
        RoutineDTO result = routineService.getRoutineById(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getRoutineId());
        assertEquals("Full Body Workout", result.getName());
        assertEquals(2, result.getWorkoutOrder().size());
        verify(routineRepository, times(1)).findById(1L);
    }
    
    @Test
    public void testGetRoutineByIdNotFound() {
        // Arrange
        when(routineRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            routineService.getRoutineById(999L);
        });
        
        assertEquals("Routine not found", exception.getMessage());
        verify(routineRepository, times(1)).findById(999L);
    }
    
    @Test
    public void testCreateRoutine() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(workoutRepository.findById(1L)).thenReturn(Optional.of(testWorkout1));
        when(workoutRepository.findById(2L)).thenReturn(Optional.of(testWorkout2));
        
        // Setup the mock to return the routine with ID set when save is called
        when(routineRepository.save(any(Routine.class))).thenAnswer(invocation -> {
            Routine savedRoutine = invocation.getArgument(0);
            savedRoutine.setRoutineId(1L);
            return savedRoutine;
        });
        
        // Act
        RoutineDTO result = routineService.createRoutine(1L, testRoutineDTO);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getRoutineId());
        assertEquals("Full Body Workout", result.getName());
        assertEquals(2, result.getWorkoutOrder().size());
        
        verify(userRepository, times(1)).findById(1L);
        verify(workoutRepository, times(1)).findById(1L);
        verify(workoutRepository, times(1)).findById(2L);
        verify(routineRepository, times(1)).save(any(Routine.class));
    }
    
    @Test
    public void testCreateRoutineUserNotFound() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            routineService.createRoutine(999L, testRoutineDTO);
        });
        
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
        verify(routineRepository, never()).save(any(Routine.class));
    }
    
    @Test
    public void testCreateRoutineWorkoutNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(workoutRepository.findById(1L)).thenReturn(Optional.of(testWorkout1));
        when(workoutRepository.findById(2L)).thenReturn(Optional.empty());
        
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            routineService.createRoutine(1L, testRoutineDTO);
        });
        
        assertEquals("Workout not found", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(workoutRepository, times(1)).findById(1L);
        verify(workoutRepository, times(1)).findById(2L);
        verify(routineRepository, never()).save(any(Routine.class));
    }
    
    @Test
    public void testUpdateRoutine() {
        // Arrange
        when(routineRepository.findById(1L)).thenReturn(Optional.of(testRoutine));
        when(workoutRepository.findById(1L)).thenReturn(Optional.of(testWorkout1));
        when(workoutRepository.findById(2L)).thenReturn(Optional.of(testWorkout2));
        
        // Update the DTO
        testRoutineDTO.setName("Updated Workout Routine");
        
        when(routineRepository.save(any(Routine.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        RoutineDTO result = routineService.updateRoutine(1L, testRoutineDTO);
        
        // Assert
        assertNotNull(result);
        assertEquals("Updated Workout Routine", result.getName());
        assertEquals(2, result.getWorkoutOrder().size());
        
        verify(routineRepository, times(1)).findById(1L);
        verify(workoutRepository, times(1)).findById(1L);
        verify(workoutRepository, times(1)).findById(2L);
        verify(routineRepository, times(1)).save(any(Routine.class));
    }
    
    @Test
    public void testUpdateRoutineNotFound() {
        // Arrange
        when(routineRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            routineService.updateRoutine(999L, testRoutineDTO);
        });
        
        assertEquals("Routine not found", exception.getMessage());
        verify(routineRepository, times(1)).findById(999L);
        verify(routineRepository, never()).save(any(Routine.class));
    }
    
    @Test
    public void testUpdateRoutineWorkoutNotFound() {
        // Arrange
        when(routineRepository.findById(1L)).thenReturn(Optional.of(testRoutine));
        when(workoutRepository.findById(1L)).thenReturn(Optional.of(testWorkout1));
        when(workoutRepository.findById(2L)).thenReturn(Optional.empty());
        
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            routineService.updateRoutine(1L, testRoutineDTO);
        });
        
        assertEquals("Workout not found", exception.getMessage());
        verify(routineRepository, times(1)).findById(1L);
        verify(workoutRepository, times(1)).findById(1L);
        verify(workoutRepository, times(1)).findById(2L);
        verify(routineRepository, never()).save(any(Routine.class));
    }
    
    @Test
    public void testDeleteRoutine() {
        // Arrange
        when(routineRepository.existsById(1L)).thenReturn(true);
        doNothing().when(routineRepository).deleteById(1L);
        
        // Act
        routineService.deleteRoutine(1L);
        
        // Assert
        verify(routineRepository, times(1)).existsById(1L);
        verify(routineRepository, times(1)).deleteById(1L);
    }
    
    @Test
    public void testDeleteRoutineNotFound() {
        // Arrange
        when(routineRepository.existsById(anyLong())).thenReturn(false);
        
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            routineService.deleteRoutine(999L);
        });
        
        assertEquals("Routine not found", exception.getMessage());
        verify(routineRepository, times(1)).existsById(999L);
        verify(routineRepository, never()).deleteById(anyLong());
    }
}