package com.fastnfit.app.UnitTests;

import com.fastnfit.app.model.Achievement;
import com.fastnfit.app.repository.AchievementRepository;
import com.fastnfit.app.service.AchievementService;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AchievementServiceTest {

    @Mock
    private AchievementRepository achievementRepository;

    @InjectMocks
    private AchievementService achievementService;

    private Achievement testAchievement1;
    private Achievement testAchievement2;

    @BeforeEach
    void setUp() {
        // Set up test achievements
        testAchievement1 = new Achievement();
        testAchievement1.setAchievementId(1L);
        testAchievement1.setTitle("First Workout");
        testAchievement1.setDescription("Complete your first workout");

        testAchievement2 = new Achievement();
        testAchievement2.setAchievementId(2L);
        testAchievement2.setTitle("Workout Streak");
        testAchievement2.setDescription("Complete 7 days in a row");
    }

    @Test
    void testGetAllAchievements() {
        // Arrange
        List<Achievement> achievements = Arrays.asList(testAchievement1, testAchievement2);
        when(achievementRepository.findAll()).thenReturn(achievements);

        // Act
        List<Achievement> result = achievementService.getAllAchievements();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(testAchievement1));
        assertTrue(result.contains(testAchievement2));
        verify(achievementRepository).findAll();
    }

    @Test
    void testGetAchievementById_WhenFound() {
        // Arrange
        when(achievementRepository.findById(1L)).thenReturn(Optional.of(testAchievement1));

        // Act
        Optional<Achievement> result = achievementService.getAchievementById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testAchievement1, result.get());
        verify(achievementRepository).findById(1L);
    }

    @Test
    void testGetAchievementById_WhenNotFound() {
        // Arrange
        when(achievementRepository.findById(3L)).thenReturn(Optional.empty());

        // Act
        Optional<Achievement> result = achievementService.getAchievementById(3L);

        // Assert
        assertFalse(result.isPresent());
        verify(achievementRepository).findById(3L);
    }

    @Test
    void testGetAchievementByTitle_WhenFound() {
        // Arrange
        when(achievementRepository.findByTitle("First Workout")).thenReturn(Optional.of(testAchievement1));

        // Act
        Optional<Achievement> result = achievementService.getAchievementByTitle("First Workout");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testAchievement1, result.get());
        verify(achievementRepository).findByTitle("First Workout");
    }

    @Test
    void testGetAchievementByTitle_WhenNotFound() {
        // Arrange
        when(achievementRepository.findByTitle("Unknown Achievement")).thenReturn(Optional.empty());

        // Act
        Optional<Achievement> result = achievementService.getAchievementByTitle("Unknown Achievement");

        // Assert
        assertFalse(result.isPresent());
        verify(achievementRepository).findByTitle("Unknown Achievement");
    }

    @Test
    void testCreateAchievement() {
        // Arrange
        Achievement newAchievement = new Achievement();
        newAchievement.setTitle("New Achievement");
        newAchievement.setDescription("Description for new achievement");
        
        when(achievementRepository.save(newAchievement)).thenReturn(newAchievement);

        // Act
        Achievement result = achievementService.createAchievement(newAchievement);

        // Assert
        assertEquals(newAchievement, result);
        verify(achievementRepository).save(newAchievement);
    }

    @Test
    void testUpdateAchievement() {
        // Arrange
        Achievement updatedAchievement = new Achievement();
        updatedAchievement.setAchievementId(1L);
        updatedAchievement.setTitle("Updated Achievement");
        updatedAchievement.setDescription("Updated description");
        
        when(achievementRepository.save(updatedAchievement)).thenReturn(updatedAchievement);

        // Act
        Achievement result = achievementService.updateAchievement(updatedAchievement);

        // Assert
        assertEquals(updatedAchievement, result);
        verify(achievementRepository).save(updatedAchievement);
    }

    @Test
    void testDeleteAchievement() {
        // Act
        achievementService.deleteAchievement(1L);

        // Assert
        verify(achievementRepository).deleteById(1L);
    }
}