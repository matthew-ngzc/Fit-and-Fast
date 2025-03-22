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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

    @InjectMocks
    private WorkoutTrackerService workoutTrackerService;

    private User testUser;
    private History testHistory;
    private final Long userId = 1L;

    @BeforeEach
    public void setup() {
        testUser = new User();
        testUser.setEmail(userId.toString());
        testUser.setUserId(userId);

        testHistory = new History();
        testHistory.setHistoryId(1L);
        testHistory.setUser(testUser);
        testHistory.setCaloriesBurned(200);
        testHistory.setDurationInMinutes(30);
    }

    @Test
    public void testLogWorkout_SavesHistoryAndUpdatesStreak() {
        // Arrange
        when(historyRepository.save(any(History.class))).thenReturn(testHistory);
        
        // Act
        History result = workoutTrackerService.logWorkout(testHistory);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getHistoryId());
        verify(historyRepository).save(testHistory);
        verify(userStreakService).updateStreak(userId);
        verify(userRepository).findById(userId); // From checkWorkoutCountAchievements
    }

    @Test
    public void testLogWorkout_ChecksWorkoutCountAchievements() {
        // Arrange
        when(historyRepository.save(any(History.class))).thenReturn(testHistory);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(historyRepository.countByUser(testUser)).thenReturn(10); // Exactly 10 workouts
        
        Achievement tenWorkoutsAchievement = new Achievement();
        tenWorkoutsAchievement.setAchievementId(1L);
        tenWorkoutsAchievement.setTitle("10 Workouts");
        when(achievementRepository.findByTitle("10 Workouts")).thenReturn(Optional.of(tenWorkoutsAchievement));
        
        // Act
        workoutTrackerService.logWorkout(testHistory);
        
        // Assert
        verify(userAchievementService).completeAchievement(userId, 1L);
    }

    @Test
    public void testLogWorkout_NoAchievementWhenWorkoutCountLessThan10() {
        // Arrange
        when(historyRepository.save(any(History.class))).thenReturn(testHistory);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(historyRepository.countByUser(testUser)).thenReturn(9); // Less than 10 workouts
        
        // Act
        workoutTrackerService.logWorkout(testHistory);
        
        // Assert
        verify(achievementRepository, never()).findByTitle(anyString());
        verify(userAchievementService, never()).completeAchievement(anyLong(), anyLong());
    }

    @Test
    public void testLogWorkout_NoAchievementWhenTitleNotFound() {
        // Arrange
        when(historyRepository.save(any(History.class))).thenReturn(testHistory);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(historyRepository.countByUser(testUser)).thenReturn(10); // Exactly 10 workouts
        when(achievementRepository.findByTitle("10 Workouts")).thenReturn(Optional.empty());
        
        // Act
        workoutTrackerService.logWorkout(testHistory);
        
        // Assert
        verify(achievementRepository).findByTitle("10 Workouts");
        verify(userAchievementService, never()).completeAchievement(anyLong(), anyLong());
    }

    @Test
    public void testGetTotalWorkoutCount_UserExists_ReturnsCount() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(historyRepository.countByUser(testUser)).thenReturn(15);
        
        // Act
        int result = workoutTrackerService.getTotalWorkoutCount(userId);
        
        // Assert
        assertEquals(15, result);
        verify(userRepository).findById(userId);
        verify(historyRepository).countByUser(testUser);
    }

    @Test
    public void testGetTotalWorkoutCount_UserNotFound_ReturnsNegativeOne() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // Act
        int result = workoutTrackerService.getTotalWorkoutCount(userId);
        
        // Assert
        assertEquals(-1, result);
        verify(userRepository).findById(userId);
        verify(historyRepository, never()).countByUser(any(User.class));
    }

    @Test
    public void testGetRecentWorkouts_ReturnsLimitedWorkouts() {
        // Arrange
        List<History> historyList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            History h = new History();
            h.setHistoryId((long) (i + 1));
            historyList.add(h);
        }
        
        when(historyRepository.findByUserIdOrderByWorkoutDateTimeDesc(userId, 5)).thenReturn(historyList);
        
        // Act
        List<History> result = workoutTrackerService.getRecentWorkouts(userId, 5);
        
        // Assert
        assertNotNull(result);
        assertEquals(5, result.size());
        verify(historyRepository).findByUserIdOrderByWorkoutDateTimeDesc(userId, 5);
    }

    @Test
    public void testGetTotalCaloriesBurned_ReturnsSumOfCalories() {
        // Arrange
        when(historyRepository.sumCaloriesBurnedByUserId(userId)).thenReturn(1500);
        
        // Act
        int result = workoutTrackerService.getTotalCaloriesBurned(userId);
        
        // Assert
        assertEquals(1500, result);
        verify(historyRepository).sumCaloriesBurnedByUserId(userId);
    }

    @Test
    public void testGetTotalDuration_ReturnsSumOfMinutes() {
        // Arrange
        when(historyRepository.sumTimeExercisedByUserId(userId)).thenReturn(480);
        
        // Act
        int result = workoutTrackerService.getTotalDuration(userId);
        
        // Assert
        assertEquals(480, result);
        verify(historyRepository).sumTimeExercisedByUserId(userId);
    }

    @Test
    public void testCheckWorkoutCountAchievements_MoreThan10Workouts_UnlocksAchievement() {
        // This test accesses the private method through the public logWorkout method
        
        // Arrange
        when(historyRepository.save(any(History.class))).thenReturn(testHistory);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(historyRepository.countByUser(testUser)).thenReturn(20); // More than 10 workouts
        
        Achievement tenWorkoutsAchievement = new Achievement();
        tenWorkoutsAchievement.setAchievementId(1L);
        tenWorkoutsAchievement.setTitle("10 Workouts");
        when(achievementRepository.findByTitle("10 Workouts")).thenReturn(Optional.of(tenWorkoutsAchievement));
        
        // Act
        workoutTrackerService.logWorkout(testHistory);
        
        // Assert
        verify(userAchievementService).completeAchievement(userId, 1L);
    }

    @Test
    public void testLogWorkout_SavesCorrectHistoryRecord() {
        // Arrange
        ArgumentCaptor<History> historyCaptor = ArgumentCaptor.forClass(History.class);
        when(historyRepository.save(historyCaptor.capture())).thenReturn(testHistory);
        
        // Act
        workoutTrackerService.logWorkout(testHistory);
        
        // Assert
        History capturedHistory = historyCaptor.getValue();
        assertEquals(testUser, capturedHistory.getUser());
        assertEquals(testHistory.getCaloriesBurned(), capturedHistory.getCaloriesBurned());
        assertEquals(testHistory.getDurationInMinutes(), capturedHistory.getDurationInMinutes());
    }

    @Test
    public void testLogWorkout_UpdatesStreakWithCorrectUserId() {
        // Arrange
        when(historyRepository.save(any(History.class))).thenReturn(testHistory);
        
        // Act
        workoutTrackerService.logWorkout(testHistory);
        
        // Assert
        verify(userStreakService).updateStreak(userId);
    }
}