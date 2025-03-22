package com.fastnfit.app.UnitTests;

import com.fastnfit.app.model.Achievement;
import com.fastnfit.app.model.History;
import com.fastnfit.app.model.User;
import com.fastnfit.app.repository.AchievementRepository;
import com.fastnfit.app.repository.HistoryRepository;
import com.fastnfit.app.repository.UserRepository;
import com.fastnfit.app.service.UserAchievementService;
import com.fastnfit.app.service.UserStreakService;
import com.fastnfit.app.service.WorkoutTrackerService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkoutTrackerServiceTest {

    @Mock
    private HistoryRepository historyRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private AchievementRepository achievementRepository;
    
    @Mock
    private UserAchievementService userAchievementService;
    
    @Mock
    private UserStreakService userStreakService;
    
    private WorkoutTrackerService workoutTrackerService;
    
    private User testUser;
    private History testHistory;
    private Achievement testAchievement;
    
    @BeforeEach
    public void setUp() {
        workoutTrackerService = new WorkoutTrackerService(
            historyRepository,
            userRepository,
            achievementRepository,
            userAchievementService,
            userStreakService
        );
        
        // Create test data
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        
        testHistory = new History();
        testHistory.setHistoryId(1L);
        testHistory.setUser(testUser);
        testHistory.setWorkoutDate(new Date());
        testHistory.setCaloriesBurned(200);
        
        testAchievement = new Achievement();
        testAchievement.setAchievementId(1L);
        testAchievement.setTitle("10 Workouts");
    }
    
    @Test
    public void testLogWorkout_SavesWorkoutAndUpdatesStreakAndChecksAchievements() {
        // Arrange
        when(historyRepository.save(any(History.class))).thenReturn(testHistory);
        
        // Act
        History result = workoutTrackerService.logWorkout(testHistory);
        
        // Assert
        assertEquals(testHistory, result);
        verify(historyRepository, times(1)).save(testHistory);
        verify(userStreakService, times(1)).updateStreak(testUser.getUserId());
    }
    
    @Test
    public void testGetTotalWorkoutCount_UserExists_ReturnsCount() {
        // Arrange
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));
        when(historyRepository.countByUser(testUser)).thenReturn(5);
        
        // Act
        int result = workoutTrackerService.getTotalWorkoutCount(testUser.getUserId());
        
        // Assert
        assertEquals(5, result);
    }
    
    @Test
    public void testGetTotalWorkoutCount_UserDoesNotExist_ReturnsNegativeOne() {
        // Arrange
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.empty());
        
        // Act
        int result = workoutTrackerService.getTotalWorkoutCount(testUser.getUserId());
        
        // Assert
        assertEquals(-1, result);
    }
    
    @Test
    public void testGetRecentWorkouts_ReturnsLimitedWorkouts() {
        // Arrange
        List<History> mockHistories = Arrays.asList(
            testHistory,
            new History(),
            new History()
        );
        when(historyRepository.findByUserIdOrderByWorkoutDateDesc(eq(testUser.getUserId()), eq(3)))
            .thenReturn(mockHistories);
        
        // Act
        List<History> result = workoutTrackerService.getRecentWorkouts(testUser.getUserId(), 3);
        
        // Assert
        assertEquals(3, result.size());
        assertEquals(mockHistories, result);
    }
    
    @Test
    public void testGetTotalCaloriesBurned_ReturnsSumOfCalories() {
        // Arrange
        when(historyRepository.sumCaloriesBurnedByUserId(testUser.getUserId())).thenReturn(500);
        
        // Act
        int result = workoutTrackerService.getTotalCaloriesBurned(testUser.getUserId());
        
        // Assert
        assertEquals(500, result);
    }
    
    @Test
    public void testCheckWorkoutCountAchievements_WorkoutsUnder10_NoAchievementGiven() {
        // Arrange
        WorkoutTrackerService spyService = spy(workoutTrackerService);
        doReturn(9).when(spyService).getTotalWorkoutCount(testUser.getUserId());
        
        // Act
        spyService.logWorkout(testHistory);
        
        // Assert
        verify(achievementRepository, never()).findByTitle(anyString());
        verify(userAchievementService, never()).completeAchievement(anyLong(), anyLong());
    }
    
    @Test
    public void testCheckWorkoutCountAchievements_10Workouts_AchievementGiven() {
        // Arrange
        WorkoutTrackerService spyService = spy(workoutTrackerService);
        doReturn(10).when(spyService).getTotalWorkoutCount(testUser.getUserId());
        when(achievementRepository.findByTitle("10 Workouts")).thenReturn(Optional.of(testAchievement));
        
        // Act
        spyService.logWorkout(testHistory);
        
        // Assert
        verify(achievementRepository, times(1)).findByTitle("10 Workouts");
        verify(userAchievementService, times(1)).completeAchievement(
            testUser.getUserId(), 
            testAchievement.getAchievementId()
        );
    }
    
    @Test
    public void testCheckWorkoutCountAchievements_10WorkoutsButNoAchievementFound_NoAchievementGiven() {
        // Arrange
        WorkoutTrackerService spyService = spy(workoutTrackerService);
        doReturn(10).when(spyService).getTotalWorkoutCount(testUser.getUserId());
        when(achievementRepository.findByTitle("10 Workouts")).thenReturn(Optional.empty());
        
        // Act
        spyService.logWorkout(testHistory);
        
        // Assert
        verify(achievementRepository, times(1)).findByTitle("10 Workouts");
        verify(userAchievementService, never()).completeAchievement(anyLong(), anyLong());
    }
}
