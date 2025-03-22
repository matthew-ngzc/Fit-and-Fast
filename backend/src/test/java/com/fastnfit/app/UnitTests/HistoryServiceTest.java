package com.fastnfit.app.UnitTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
public class HistoryServiceTest {

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
    
    @InjectMocks
    private HistoryService historyService;
    
    private User testUser;
    private Workout testWorkout;
    private History testHistory;
    private WorkoutDTO testWorkoutDTO;
    
    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setEmail("test@example.com");
        
        // Create test workout
        testWorkout = new Workout();
        testWorkout.setWorkoutId(1L);
        testWorkout.setName("Test Workout");
        testWorkout.setCalories(150);
        
        // Create test workout DTO
        testWorkoutDTO = new WorkoutDTO();
        testWorkoutDTO.setWorkoutId(1L);
        testWorkoutDTO.setName("Test Workout");
        testWorkoutDTO.setCalories(150);
        
        // Create test history
        testHistory = new History();
        testHistory.setHistoryId(1L);
        testHistory.setUser(testUser);
        testHistory.setWorkout(testWorkout);
        testHistory.setWorkoutDate(new Date());
        testHistory.setCaloriesBurned(150);
    }
    
    @Test
    void testGetUserHistory() {
        // Setup
        List<History> histories = new ArrayList<>();
        histories.add(testHistory);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(historyRepository.findByUser(testUser)).thenReturn(histories);
        when(workoutService.convertToDTO(testWorkout)).thenReturn(testWorkoutDTO);
        
        // Execute
        List<HistoryDTO> result = historyService.getUserHistory(1L);
        
        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getHistoryId());
        
        verify(userRepository).findById(1L);
        verify(historyRepository).findByUser(testUser);
        verify(workoutService).convertToDTO(testWorkout);
    }
    
    @Test
    void testGetUserHistoryBetweenDates() {
        // Setup
        List<History> histories = new ArrayList<>();
        histories.add(testHistory);
        
        Date startDate = new Date();
        Date endDate = new Date();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(historyRepository.findByUserAndWorkoutDateBetween(testUser, startDate, endDate)).thenReturn(histories);
        when(workoutService.convertToDTO(testWorkout)).thenReturn(testWorkoutDTO);
        
        // Execute
        List<HistoryDTO> result = historyService.getUserHistoryBetweenDates(1L, startDate, endDate);
        
        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getHistoryId());
        
        verify(userRepository).findById(1L);
        verify(historyRepository).findByUserAndWorkoutDateBetween(testUser, startDate, endDate);
        verify(workoutService).convertToDTO(testWorkout);
    }
    
    @Test
    void testCreateHistory() {
        // Setup
        HistoryDTO historyDTO = new HistoryDTO();
        historyDTO.setWorkoutDate(new Date());
        historyDTO.setName("Test History");
        historyDTO.setWorkout(testWorkoutDTO);
        historyDTO.setCaloriesBurned(150);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(workoutRepository.findById(1L)).thenReturn(Optional.of(testWorkout));
        when(historyRepository.save(any(History.class))).thenReturn(testHistory);
        when(workoutService.convertToDTO(testWorkout)).thenReturn(testWorkoutDTO);
        
        // Execute
        HistoryDTO result = historyService.createHistory(1L, historyDTO);
        
        // Verify
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(workoutRepository).findById(1L);
        verify(historyRepository).save(any(History.class));
        verify(workoutService).convertToDTO(testWorkout);
    }
    
    @Test
    void testRecordWorkoutCompletion() {
        // Setup
        WorkoutDTO workoutDTO = new WorkoutDTO();
        workoutDTO.setWorkoutId(1L);
        workoutDTO.setName("Test Workout");
        workoutDTO.setCalories(150);
        
        HistoryDTO historyDTO = new HistoryDTO();
        historyDTO.setWorkoutDate(new Date());
        historyDTO.setWorkout(workoutDTO);
        historyDTO.setCaloriesBurned(150);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(workoutRepository.findById(1L)).thenReturn(Optional.of(testWorkout));
        when(historyRepository.save(any(History.class))).thenReturn(testHistory);
        when(workoutService.convertToDTO(testWorkout)).thenReturn(testWorkoutDTO);
        
        // Execute
        HistoryDTO result = historyService.recordWorkoutCompletion(1L, workoutDTO);
        
        // Verify
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(historyRepository).save(any(History.class));
        verify(workoutService).convertToDTO(testWorkout);
        verify(userStreakService).updateStreak(1L);
    }
    
    @Test
    void testConvertToDTO() {
        // Setup
        when(workoutService.convertToDTO(testWorkout)).thenReturn(testWorkoutDTO);
        
        // Execute
        HistoryDTO result = historyService.convertToDTO(testHistory);
        
        // Verify
        assertNotNull(result);
        assertEquals(1L, result.getHistoryId());
        assertEquals(testHistory.getWorkoutDate(), result.getWorkoutDate());
        assertEquals(testWorkoutDTO, result.getWorkout());
        
        verify(workoutService).convertToDTO(testWorkout);
    }
}