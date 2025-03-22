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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserStreakServiceTest {

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

    @InjectMocks
    private UserStreakService userStreakService;

    private User testUser;
    private UserDetails testUserDetails;
    private final Long userId = 1L;

    @BeforeEach
    public void setup() {
        testUserDetails = new UserDetails();
        testUserDetails.setCurrentStreak(0);
        testUserDetails.setLongestStreak(0);

        testUser = new User();
        testUser.setUserId(userId);
        testUser.setUserDetails(testUserDetails);
    }

    @Test
    public void testUpdateStreak_WorkedOutTodayButMissedYesterday_StreakStaysUnchanged() {
        // Arrange
        testUserDetails.setCurrentStreak(3);
        testUserDetails.setLongestStreak(5);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Mock workouts for today but not yesterday
        List<History> todayWorkouts = new ArrayList<>();
        todayWorkouts.add(new History());
        List<History> emptyList = new ArrayList<>();

        when(historyRepository.findByUserAndWorkoutDateTimeBetween(eq(testUser), any(Timestamp.class),
                any(Timestamp.class)))
                .thenReturn(todayWorkouts) // First call for today
                .thenReturn(emptyList); // Second call for yesterday

        // Act
        userStreakService.updateStreak(userId);

        // Assert
        assertEquals(3, testUserDetails.getCurrentStreak());
        assertEquals(5, testUserDetails.getLongestStreak());
        verify(userRepository).save(testUser);
    }

    @Test
    public void testUpdateStreak_MissedTwoDaysInARow_StreakResetsToZero() {
        // Arrange
        testUserDetails.setCurrentStreak(7);
        testUserDetails.setLongestStreak(10);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Mock no workouts for today and yesterday
        List<History> emptyList = new ArrayList<>();

        when(historyRepository.findByUserAndWorkoutDateTimeBetween(eq(testUser), any(Timestamp.class),
                any(Timestamp.class)))
                .thenReturn(emptyList) // First call for today
                .thenReturn(emptyList); // Second call for yesterday

        // Act
        userStreakService.updateStreak(userId);

        // Assert
        assertEquals(0, testUserDetails.getCurrentStreak());
        assertEquals(10, testUserDetails.getLongestStreak());
        verify(userRepository).save(testUser);
    }

    @Test
    public void testUpdateStreak_NewStreakBecomesBest_LongestStreakUpdated() {
        // Arrange
        testUserDetails.setCurrentStreak(9);
        testUserDetails.setLongestStreak(9);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Mock workouts for today and yesterday
        List<History> workouts = new ArrayList<>();
        workouts.add(new History());

        when(historyRepository.findByUserAndWorkoutDateTimeBetween(eq(testUser), any(Timestamp.class),
                any(Timestamp.class)))
                .thenReturn(workouts) // First call for today
                .thenReturn(workouts); // Second call for yesterday

        // Act
        userStreakService.updateStreak(userId);

        // Assert
        assertEquals(10, testUserDetails.getCurrentStreak());
        assertEquals(10, testUserDetails.getLongestStreak());
        verify(userRepository).save(testUser);
    }

    @Test
    public void testUpdateStreak_StartingNewStreak_StreakSetToOne() {
        // Arrange
        testUserDetails.setCurrentStreak(0);
        testUserDetails.setLongestStreak(5);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Mock workout for today but not yesterday
        List<History> todayWorkouts = new ArrayList<>();
        todayWorkouts.add(new History());
        List<History> emptyList = new ArrayList<>();

        when(historyRepository.findByUserAndWorkoutDateTimeBetween(eq(testUser), any(Timestamp.class),
                any(Timestamp.class)))
                .thenReturn(todayWorkouts) // First call for today
                .thenReturn(emptyList); // Second call for yesterday

        // Act
        userStreakService.updateStreak(userId);

        // Assert
        assertEquals(1, testUserDetails.getCurrentStreak());
        assertEquals(5, testUserDetails.getLongestStreak());
        verify(userRepository).save(testUser);
    }

    @Test
    public void testUpdateStreak_Reaches5DayStreak_AchievementUnlocked() {
        // Arrange
        testUserDetails.setCurrentStreak(4);
        testUserDetails.setLongestStreak(4);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Mock workouts for today and yesterday
        List<History> workouts = new ArrayList<>();
        workouts.add(new History());

        when(historyRepository.findByUserAndWorkoutDateTimeBetween(eq(testUser), any(Timestamp.class),
                any(Timestamp.class)))
                .thenReturn(workouts) // First call for today
                .thenReturn(workouts); // Second call for yesterday

        // Mock achievement
        Achievement fiveDayAchievement = new Achievement();
        fiveDayAchievement.setAchievementId(1L);
        fiveDayAchievement.setTitle("5 Day Streak");
        when(achievementRepository.findByTitle("5 Day Streak")).thenReturn(Optional.of(fiveDayAchievement));

        // Act
        userStreakService.updateStreak(userId);

        // Assert
        assertEquals(5, testUserDetails.getCurrentStreak());
        assertEquals(5, testUserDetails.getLongestStreak());
        verify(userRepository).save(testUser);
        verify(userAchievementService).completeAchievement(userId, 1L);
    }

    @Test
    public void testUpdateStreak_WorkedOutTodayAndYesterday_StreakIncremented() {
        // Arrange
        testUserDetails.setCurrentStreak(1);
        testUserDetails.setLongestStreak(3);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Mock workouts for today and yesterday
        List<History> todayWorkouts = new ArrayList<>();
        todayWorkouts.add(new History());
        List<History> yesterdayWorkouts = new ArrayList<>();
        yesterdayWorkouts.add(new History());

        // Use doAnswer to handle different timestamp ranges
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            Timestamp start = invocation.getArgument(1);
            Timestamp end = invocation.getArgument(2);

            // Return today's workouts if the timestamp is for today
            LocalDate today = LocalDate.now();
            if (start.toLocalDateTime().toLocalDate().equals(today)) {
                return todayWorkouts;
            }
            // Return yesterday's workouts if the timestamp is for yesterday
            else if (start.toLocalDateTime().toLocalDate().equals(today.minusDays(1))) {
                return yesterdayWorkouts;
            }
            return new ArrayList<History>();
        }).when(historyRepository).findByUserAndWorkoutDateTimeBetween(any(User.class), any(Timestamp.class),
                any(Timestamp.class));

        // Act
        userStreakService.updateStreak(userId);

        // Assert
        assertEquals(2, testUserDetails.getCurrentStreak());
        assertEquals(3, testUserDetails.getLongestStreak());
        verify(userRepository).save(testUser);
    }

    @Test
    public void testUpdateStreak_UserNotFound_NoChanges() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        userStreakService.updateStreak(userId);

        // Assert
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testUpdateStreak_UserDetailsNull_NoChanges() {
        // Arrange
        testUser.setUserDetails(null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        userStreakService.updateStreak(userId);

        // Assert
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testGetUserStreak_ReturnsCorrectStreak() {
        // Arrange
        testUserDetails.setCurrentStreak(7);
        when(userDetailsRepository.findById(userId)).thenReturn(Optional.of(testUserDetails));

        // Act
        StreakDTO result = userStreakService.getUserStreak(userId);

        // Assert
        assertNotNull(result);
        assertEquals(7, result.getDays());
    }

    @Test
    public void testGetLongestUserStreak_ReturnsCorrectStreak() {
        // Arrange
        testUserDetails.setLongestStreak(14);
        when(userDetailsRepository.findById(userId)).thenReturn(Optional.of(testUserDetails));

        // Act
        StreakDTO result = userStreakService.getLongestUserStreak(userId);

        // Assert
        assertNotNull(result);
        assertEquals(14, result.getDays());
    }

    @Test
    public void testGetUserStreak_UserNotFound_ThrowsException() {
        // Arrange
        when(userDetailsRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userStreakService.getUserStreak(userId));
    }

    @Test
    public void testGetLongestUserStreak_UserNotFound_ThrowsException() {
        // Arrange
        when(userDetailsRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userStreakService.getLongestUserStreak(userId));
    }
}