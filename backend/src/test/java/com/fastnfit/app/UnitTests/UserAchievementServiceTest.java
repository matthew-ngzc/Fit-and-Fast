package com.fastnfit.app.UnitTests;

import com.fastnfit.app.model.Achievement;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.UserAchievement;
import com.fastnfit.app.repository.AchievementRepository;
import com.fastnfit.app.repository.UserAchievementRepository;
import com.fastnfit.app.service.UserAchievementService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAchievementServiceTest {

    @Mock
    private UserAchievementRepository userAchievementRepository;

    @Mock
    private AchievementRepository achievementRepository;

    @InjectMocks
    private UserAchievementService userAchievementService;

    private User testUser;
    private Achievement testAchievement1;
    private Achievement testAchievement2;
    private UserAchievement testUserAchievement1;
    private UserAchievement testUserAchievement2;

    @BeforeEach
    void setUp() {
        // Set up test user
        testUser = new User();
        testUser.setUserId(1L);

        // Set up test achievements
        testAchievement1 = new Achievement();
        testAchievement1.setAchievementId(1L);
        testAchievement1.setTitle("First Workout");
        testAchievement1.setDescription("Complete your first workout");

        testAchievement2 = new Achievement();
        testAchievement2.setAchievementId(2L);
        testAchievement2.setTitle("Workout Streak");
        testAchievement2.setDescription("Complete 7 days in a row");

        // Set up test user achievements
        testUserAchievement1 = new UserAchievement();
        testUserAchievement1.setUser(testUser);
        testUserAchievement1.setAchievement(testAchievement1);
        testUserAchievement1.setCompleted(true);

        testUserAchievement2 = new UserAchievement();
        testUserAchievement2.setUser(testUser);
        testUserAchievement2.setAchievement(testAchievement2);
        testUserAchievement2.setCompleted(false);
    }

    @Test
    void testInitializeUserAchievements() {
        // Arrange
        List<Achievement> allAchievements = Arrays.asList(testAchievement1, testAchievement2);
        when(achievementRepository.findAll()).thenReturn(allAchievements);

        // Act
        userAchievementService.initializeUserAchievements(testUser);

        // Assert
        verify(achievementRepository).findAll();
        verify(userAchievementRepository, times(2)).save(any(UserAchievement.class));
    }

    @Test
    void testGetUserAchievements() {
        // Arrange
        List<UserAchievement> userAchievements = Arrays.asList(testUserAchievement1, testUserAchievement2);
        when(userAchievementRepository.findByUserUserId(1L)).thenReturn(userAchievements);

        // Act
        List<UserAchievement> result = userAchievementService.getUserAchievements(1L);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(testUserAchievement1));
        assertTrue(result.contains(testUserAchievement2));
        verify(userAchievementRepository).findByUserUserId(1L);
    }

    @Test
    void testHasCompletedAchievement_WhenCompleted() {
        // Arrange
        when(userAchievementRepository.findByUserUserIdAndAchievementAchievementId(1L, 1L))
                .thenReturn(Optional.of(testUserAchievement1));

        // Act
        boolean result = userAchievementService.hasCompletedAchievement(1L, 1L);

        // Assert
        assertTrue(result);
        verify(userAchievementRepository).findByUserUserIdAndAchievementAchievementId(1L, 1L);
    }

    @Test
    void testHasCompletedAchievement_WhenNotCompleted() {
        // Arrange
        when(userAchievementRepository.findByUserUserIdAndAchievementAchievementId(1L, 2L))
                .thenReturn(Optional.of(testUserAchievement2));

        // Act
        boolean result = userAchievementService.hasCompletedAchievement(1L, 2L);

        // Assert
        assertFalse(result);
        verify(userAchievementRepository).findByUserUserIdAndAchievementAchievementId(1L, 2L);
    }

    @Test
    void testHasCompletedAchievement_WhenNotFound() {
        // Arrange
        when(userAchievementRepository.findByUserUserIdAndAchievementAchievementId(1L, 3L))
                .thenReturn(Optional.empty());

        // Act
        boolean result = userAchievementService.hasCompletedAchievement(1L, 3L);

        // Assert
        assertFalse(result);
        verify(userAchievementRepository).findByUserUserIdAndAchievementAchievementId(1L, 3L);
    }

    @Test
    void testCompleteAchievement_WhenFound() {
        // Arrange
        when(userAchievementRepository.findByUserUserIdAndAchievementAchievementId(1L, 2L))
                .thenReturn(Optional.of(testUserAchievement2));

        // Act
        userAchievementService.completeAchievement(1L, 2L);

        // Assert
        assertTrue(testUserAchievement2.isCompleted());
        verify(userAchievementRepository).findByUserUserIdAndAchievementAchievementId(1L, 2L);
        verify(userAchievementRepository).save(testUserAchievement2);
    }

    @Test
    void testCompleteAchievement_WhenNotFound() {
        // Arrange
        when(userAchievementRepository.findByUserUserIdAndAchievementAchievementId(1L, 3L))
                .thenReturn(Optional.empty());

        // Act
        userAchievementService.completeAchievement(1L, 3L);

        // Assert
        verify(userAchievementRepository).findByUserUserIdAndAchievementAchievementId(1L, 3L);
        verify(userAchievementRepository, never()).save(any());
    }

    @Test
    void testGetCompletedAchievements() {
        // Arrange
        List<UserAchievement> completedAchievements = Arrays.asList(testUserAchievement1);
        when(userAchievementRepository.findByUserUserIdAndCompletedTrue(1L))
                .thenReturn(completedAchievements);

        // Act
        List<UserAchievement> result = userAchievementService.getCompletedAchievements(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(testUserAchievement1, result.get(0));
        verify(userAchievementRepository).findByUserUserIdAndCompletedTrue(1L);
    }

    @Test
    void testGetIncompleteAchievements() {
        // Arrange
        List<UserAchievement> incompleteAchievements = Arrays.asList(testUserAchievement2);
        when(userAchievementRepository.findByUserUserIdAndCompletedFalse(1L))
                .thenReturn(incompleteAchievements);

        // Act
        List<UserAchievement> result = userAchievementService.getIncompleteAchievements(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(testUserAchievement2, result.get(0));
        verify(userAchievementRepository).findByUserUserIdAndCompletedFalse(1L);
    }
}