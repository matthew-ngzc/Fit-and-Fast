package com.fastnfit.app.UnitTests;

import com.fastnfit.app.dto.StreakDTO;
import com.fastnfit.app.model.Achievement;
import com.fastnfit.app.model.History;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.UserDetails;
import com.fastnfit.app.repository.AchievementRepository;
import com.fastnfit.app.repository.HistoryRepository;
import com.fastnfit.app.repository.UserDetailsRepository;
import com.fastnfit.app.repository.UserRepository;
import com.fastnfit.app.service.UserAchievementService;
import com.fastnfit.app.service.UserStreakService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserStreakServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HistoryRepository historyRepository;

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private UserAchievementService userAchievementService;

    @Mock
    private UserDetailsRepository userDetailsRepository;

    private UserStreakService userStreakService;

    private User testUser;
    private UserDetails testUserDetails;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userStreakService = new UserStreakService(
                userRepository,
                historyRepository,
                achievementRepository,
                userAchievementService,
                userDetailsRepository);

        // Set up test user and user details
        testUser = new User();
        testUser.setUserId(userId);

        testUserDetails = new UserDetails();
        testUserDetails.setCurrentStreak(0);
        testUserDetails.setLongestStreak(0);
        testUser.setUserDetails(testUserDetails);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userDetailsRepository.findById(userId)).thenReturn(Optional.of(testUserDetails));
    }

    @Test
    void updateStreak_WhenWorkoutTodayAndNoStreak_ShouldIncrementStreak() {
        // Arrange
        List<History> todayWorkouts = new ArrayList<>();
        todayWorkouts.add(new History());

        when(historyRepository.findByUserAndWorkoutDateBetween(eq(testUser), any(Date.class), any(Date.class)))
                .thenReturn(todayWorkouts) // Workout today
                .thenReturn(new ArrayList<>()); // No workouts yesterday

        // Act
        userStreakService.updateStreak(userId);

        // Assert
        verify(userRepository).save(testUser);
        assertEquals(1, testUserDetails.getCurrentStreak());
        assertEquals(1, testUserDetails.getLongestStreak());
    }

    @Test
    void updateStreak_WhenWorkoutTodayAndYesterday_ShouldIncrementStreak() {
        // Arrange
        testUserDetails.setCurrentStreak(1);
        testUserDetails.setLongestStreak(5);

        List<History> workouts = new ArrayList<>();
        workouts.add(new History());

        when(historyRepository.findByUserAndWorkoutDateBetween(eq(testUser), any(Date.class), any(Date.class)))
                .thenReturn(workouts); // Workouts both yesterday and today

        // Act
        userStreakService.updateStreak(userId);

        // Assert
        verify(userRepository).save(testUser);
        assertEquals(2, testUserDetails.getCurrentStreak());
        assertEquals(5, testUserDetails.getLongestStreak()); // Should not change as current < longest
    }

    @Test
    void updateStreak_WhenWorkoutTodayAndNewLongestStreak_ShouldUpdateLongestStreak() {
        // Arrange
        testUserDetails.setCurrentStreak(5);
        testUserDetails.setLongestStreak(5);

        List<History> workouts = new ArrayList<>();
        workouts.add(new History());

        when(historyRepository.findByUserAndWorkoutDateBetween(eq(testUser), any(Date.class), any(Date.class)))
                .thenReturn(workouts); // Workouts both yesterday and today

        // Act
        userStreakService.updateStreak(userId);

        // Assert
        verify(userRepository).save(testUser);
        assertEquals(6, testUserDetails.getCurrentStreak());
        assertEquals(6, testUserDetails.getLongestStreak()); // Should update as new streak > longest
    }

    @Test
    void updateStreak_WhenNoWorkoutTodayAndYesterday_ShouldResetStreak() {
        // Arrange
        testUserDetails.setCurrentStreak(5);
        testUserDetails.setLongestStreak(10);

        when(historyRepository.findByUserAndWorkoutDateBetween(eq(testUser), any(Date.class), any(Date.class)))
                .thenReturn(new ArrayList<>()); // No workouts on either day

        // Act
        userStreakService.updateStreak(userId);

        // Assert
        verify(userRepository).save(testUser);
        assertEquals(0, testUserDetails.getCurrentStreak());
        assertEquals(10, testUserDetails.getLongestStreak()); // Longest streak remains unchanged
    }

    @Test
    void updateStreak_WhenNoWorkoutTodayButWorkoutYesterday_ShouldNotChangeStreak() {
        // Arrange
        testUserDetails.setCurrentStreak(5);

        when(historyRepository.findByUserAndWorkoutDateBetween(eq(testUser), any(Date.class), any(Date.class)))
                .thenReturn(List.of(new History())) // Workout yesterday
                .thenReturn(new ArrayList<>()); // No workout today

        // Act
        userStreakService.updateStreak(userId);

        // Assert
        verify(userRepository).save(testUser);
        assertEquals(5, testUserDetails.getCurrentStreak()); // Should remain unchanged
    }

    @Test
    void updateStreak_WhenUserDetailsIsNull_ShouldDoNothing() {
        // Arrange
        User userWithoutDetails = new User();
        userWithoutDetails.setUserId(2L);
        userWithoutDetails.setUserDetails(null);

        when(userRepository.findById(2L)).thenReturn(Optional.of(userWithoutDetails));

        // Act
        userStreakService.updateStreak(2L);

        // Assert
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateStreak_WhenStreak5_ShouldAward5DayStreakAchievement() {
        // Arrange
        testUserDetails.setCurrentStreak(4);

        List<History> workouts = new ArrayList<>();
        workouts.add(new History());

        Achievement achievement = new Achievement();
        achievement.setAchievementId(1L);
        achievement.setTitle("5 Day Streak");

        when(historyRepository.findByUserAndWorkoutDateBetween(eq(testUser), any(Date.class), any(Date.class)))
                .thenReturn(workouts); // Workouts both yesterday and today

        when(achievementRepository.findByTitle("5 Day Streak")).thenReturn(Optional.of(achievement));

        // Act
        userStreakService.updateStreak(userId);

        // Assert
        verify(userRepository).save(testUser);
        assertEquals(5, testUserDetails.getCurrentStreak());
        verify(userAchievementService).completeAchievement(userId, 1L);
    }

    @Test
    void updateStreak_WhenStreak30_ShouldAward30DayStreakAchievement() {
        // Arrange
        testUserDetails.setCurrentStreak(29);

        List<History> workouts = new ArrayList<>();
        workouts.add(new History());

        Achievement achievement5Day = new Achievement();
        achievement5Day.setAchievementId(1L);
        achievement5Day.setTitle("5 Day Streak");

        Achievement achievement30Day = new Achievement();
        achievement30Day.setAchievementId(2L);
        achievement30Day.setTitle("30 Day Streak");

        when(historyRepository.findByUserAndWorkoutDateBetween(eq(testUser), any(Date.class), any(Date.class)))
                .thenReturn(workouts); // Workouts both yesterday and today

        when(achievementRepository.findByTitle("5 Day Streak")).thenReturn(Optional.of(achievement5Day));
        when(achievementRepository.findByTitle("30 Day Streak")).thenReturn(Optional.of(achievement30Day));

        // Act
        userStreakService.updateStreak(userId);

        // Assert
        verify(userRepository).save(testUser);
        assertEquals(30, testUserDetails.getCurrentStreak());
        verify(userAchievementService).completeAchievement(userId, 1L); // 5-day achievement
        verify(userAchievementService).completeAchievement(userId, 2L); // 30-day achievement
    }

    @Test
    void getUserStreak_ShouldReturnCorrectStreak() {
        // Arrange
        testUserDetails.setCurrentStreak(7);

        // Act
        StreakDTO result = userStreakService.getUserStreak(userId);

        // Assert
        assertEquals(7, result.getDays());
        verify(userDetailsRepository).findById(userId);
    }

    @Test
    void getLongestUserStreak_ShouldReturnCorrectStreak() {
        // Arrange
        testUserDetails.setLongestStreak(15);

        // Act
        StreakDTO result = userStreakService.getLongestUserStreak(userId);

        // Assert
        assertEquals(15, result.getDays());
        verify(userDetailsRepository).findById(userId);
    }

    @Test
    void getUserStreak_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        when(userDetailsRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userStreakService.getUserStreak(999L);
        });

        assertEquals("User details not found", exception.getMessage());
    }

    @Test
    void getLongestUserStreak_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        when(userDetailsRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userStreakService.getLongestUserStreak(999L);
        });

        assertEquals("User details not found", exception.getMessage());
    }
}
