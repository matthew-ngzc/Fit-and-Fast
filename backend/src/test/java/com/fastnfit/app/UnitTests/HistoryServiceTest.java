package com.fastnfit.app.UnitTests;

import com.fastnfit.app.dto.HistoryDTO;
import com.fastnfit.app.dto.WorkoutDTO;
import com.fastnfit.app.model.History;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.Workout;
import com.fastnfit.app.repository.HistoryRepository;
import com.fastnfit.app.repository.UserRepository;
import com.fastnfit.app.repository.WorkoutRepository;
import com.fastnfit.app.service.HistoryService;
import com.fastnfit.app.service.UserStreakService;
import com.fastnfit.app.service.WorkoutService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {

    @Mock
    private HistoryRepository historyRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private WorkoutRepository workoutRepository;
    
    @Mock
    private WorkoutService workoutService;
    
    @Mock
    private UserStreakService userStreakService;

    private HistoryService historyService;
    
    private User testUser;
    private Workout testWorkout;
    private History testHistory;
    private WorkoutDTO testWorkoutDTO;
    private HistoryDTO testHistoryDTO;
    
    @BeforeEach
    void setUp() {
        historyService = new HistoryService(
            historyRepository,
            userRepository,
            workoutRepository,
            workoutService,
            userStreakService
        );
        
        // Setup test data
        testUser = new User();
        testUser.setUserId(1L);
        
        testWorkout = new Workout();
        testWorkout.setWorkoutId(1L);
        testWorkout.setName("Test Workout");
        testWorkout.setDurationInMinutes(30);
        testWorkout.setCalories(150);
        
        testWorkoutDTO = new WorkoutDTO();
        testWorkoutDTO.setWorkoutId(1L);
        testWorkoutDTO.setName("Test Workout");
        testWorkoutDTO.setDurationInMinutes(30);
        testWorkoutDTO.setCalories(150);
        
        testHistory = new History();
        testHistory.setHistoryId(1L);
        testHistory.setUser(testUser);
        testHistory.setWorkout(testWorkout);
        testHistory.setWorkoutName("Test Workout");
        testHistory.setCaloriesBurned(150);
        testHistory.setDurationInMinutes(30);
        testHistory.setWorkoutDateTime(new Timestamp(System.currentTimeMillis()));
        
        testHistoryDTO = new HistoryDTO();
        testHistoryDTO.setHistoryId(1L);
        testHistoryDTO.setName("Test Workout");
        testHistoryDTO.setCaloriesBurned(150);
        testHistoryDTO.setDurationInMinutes(30);
        testHistoryDTO.setWorkout(testWorkoutDTO);
        testHistoryDTO.setWorkoutDateTime(new Timestamp(System.currentTimeMillis()));
    }

    @Test
    void getUserHistory_shouldReturnUserHistory() {
        // Given
        List<History> histories = Collections.singletonList(testHistory);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(historyRepository.findByUser(testUser)).thenReturn(histories);
        when(workoutService.convertToDTO(testWorkout)).thenReturn(testWorkoutDTO);
        
        // When
        List<HistoryDTO> result = historyService.getUserHistory(1L);
        
        // Then
        assertEquals(1, result.size());
        assertEquals(testHistory.getHistoryId(), result.get(0).getHistoryId());
        verify(userRepository).findById(1L);
        verify(historyRepository).findByUser(testUser);
    }

    @Test
    void getUserHistory_shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            historyService.getUserHistory(1L);
        });
        
        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(historyRepository, never()).findByUser(any(User.class));
    }

    @Test
    void getUserHistoryBetweenDates_shouldReturnHistoryBetweenDates() {
        // Given
        List<History> histories = Collections.singletonList(testHistory);
        Date startDate = new Date(System.currentTimeMillis() - 86400000); // Yesterday
        Date endDate = new Date(System.currentTimeMillis() + 86400000);   // Tomorrow
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(historyRepository.findByUserAndWorkoutDateTimeBetween(
                eq(testUser), 
                any(Timestamp.class), 
                any(Timestamp.class)
        )).thenReturn(histories);
        when(workoutService.convertToDTO(testWorkout)).thenReturn(testWorkoutDTO);
        
        // When
        List<HistoryDTO> result = historyService.getUserHistoryBetweenDates(1L, startDate, endDate);
        
        // Then
        assertEquals(1, result.size());
        assertEquals(testHistory.getHistoryId(), result.get(0).getHistoryId());
        verify(userRepository).findById(1L);
        verify(historyRepository).findByUserAndWorkoutDateTimeBetween(
                eq(testUser), 
                any(Timestamp.class), 
                any(Timestamp.class)
        );
    }

    @Test
    void createHistory_shouldCreateAndReturnHistory() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(workoutRepository.findById(1L)).thenReturn(Optional.of(testWorkout));
        when(historyRepository.save(any(History.class))).thenReturn(testHistory);
        when(workoutService.convertToDTO(testWorkout)).thenReturn(testWorkoutDTO);
        
        // When
        HistoryDTO result = historyService.createHistory(1L, testHistoryDTO);
        
        // Then
        assertNotNull(result);
        assertEquals(testHistory.getHistoryId(), result.getHistoryId());
        
        // Verify the history was created correctly
        ArgumentCaptor<History> historyCaptor = ArgumentCaptor.forClass(History.class);
        verify(historyRepository).save(historyCaptor.capture());
        
        History capturedHistory = historyCaptor.getValue();
        assertEquals(testUser, capturedHistory.getUser());
        assertEquals(testWorkout, capturedHistory.getWorkout());
        assertEquals(testHistoryDTO.getName(), capturedHistory.getWorkoutName());
        assertEquals(testHistoryDTO.getCaloriesBurned(), capturedHistory.getCaloriesBurned());
        assertEquals(testHistoryDTO.getDurationInMinutes(), capturedHistory.getDurationInMinutes());
    }

    @Test
    void recordWorkoutCompletion_shouldCreateHistoryAndUpdateStreak() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(workoutRepository.findById(1L)).thenReturn(Optional.of(testWorkout));
        when(historyRepository.save(any(History.class))).thenReturn(testHistory);
        when(workoutService.convertToDTO(any(Workout.class))).thenReturn(testWorkoutDTO);
        
        // When
        HistoryDTO result = historyService.recordWorkoutCompletion(1L, testWorkoutDTO);
        
        // Then
        assertNotNull(result);
        verify(userStreakService).updateStreak(1L);
        
        // Verify the history was created with correct data
        ArgumentCaptor<History> historyCaptor = ArgumentCaptor.forClass(History.class);
        verify(historyRepository).save(historyCaptor.capture());
        
        History capturedHistory = historyCaptor.getValue();
        assertEquals(testUser, capturedHistory.getUser());
        assertEquals(testWorkoutDTO.getCalories(), capturedHistory.getCaloriesBurned());
        assertEquals(testWorkoutDTO.getDurationInMinutes(), capturedHistory.getDurationInMinutes());
    }

    @Test
    void convertToDTO_shouldCorrectlyConvertEntityToDTO() {
        // Given
        when(workoutService.convertToDTO(testWorkout)).thenReturn(testWorkoutDTO);
        
        // When
        HistoryDTO result = historyService.convertToDTO(testHistory);
        
        // Then
        assertNotNull(result);
        assertEquals(testHistory.getHistoryId(), result.getHistoryId());
        assertEquals(testHistory.getWorkoutName(), result.getName());
        assertEquals(testHistory.getCaloriesBurned(), result.getCaloriesBurned());
        assertEquals(testHistory.getDurationInMinutes(), result.getDurationInMinutes());
        assertEquals(testWorkoutDTO, result.getWorkout());
    }
}