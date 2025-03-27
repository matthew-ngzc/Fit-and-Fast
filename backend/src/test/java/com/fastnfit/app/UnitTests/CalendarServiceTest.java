package com.fastnfit.app.UnitTests;

import com.fastnfit.app.dto.CycleInfoDTO;
import com.fastnfit.app.dto.CycleUpdateDTO;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.UserDetails;
import com.fastnfit.app.repository.HistoryRepository;
import com.fastnfit.app.repository.UserRepository;
import com.fastnfit.app.service.CalendarService;
import com.fastnfit.app.service.RecommendationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CalendarServiceTest {

    @Mock
    private HistoryRepository historyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private CalendarService calendarService;

    private User testUser;
    private UserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUserDetails = new UserDetails();
        testUser.setUserDetails(testUserDetails);
    }

    @Test
    void testGetWorkoutDatesForMonth() {
        // Arrange
        Long userId = 1L;
        int year = 2024;
        int month = 3;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Mock history repository return
        com.fastnfit.app.model.History history1 = mock(com.fastnfit.app.model.History.class);
        com.fastnfit.app.model.History history2 = mock(com.fastnfit.app.model.History.class);

        when(history1.getWorkoutDateTime()).thenReturn(Timestamp.valueOf("2024-03-15 10:00:00"));
        when(history2.getWorkoutDateTime()).thenReturn(Timestamp.valueOf("2024-03-20 14:30:00"));

        when(historyRepository.findByUserAndWorkoutDateTimeBetween(
                eq(testUser), 
                any(Timestamp.class), 
                any(Timestamp.class)
        )).thenReturn(Arrays.asList(history1, history2));

        // Act
        List<LocalDate> workoutDates = calendarService.getWorkoutDatesForMonth(userId, year, month);

        // Assert
        assertEquals(2, workoutDates.size());
        assertTrue(workoutDates.contains(LocalDate.of(2024, 3, 15)));
        assertTrue(workoutDates.contains(LocalDate.of(2024, 3, 20)));
        
        verify(userRepository).findById(userId);
        verify(historyRepository).findByUserAndWorkoutDateTimeBetween(
                eq(testUser), 
                any(Timestamp.class), 
                any(Timestamp.class)
        );
    }

    @Test
    void testUpdateCycleInfo() {
        // Arrange
        Long userId = 1L;
        CycleUpdateDTO dto = new CycleUpdateDTO();
        dto.setCycleLength(30);
        dto.setPeriodLength(6);
        dto.setLastPeriodStartDate(LocalDate.of(2024, 3, 1));

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        calendarService.updateCycleInfo(userId, dto);

        // Assert
        assertEquals(30, testUser.getUserDetails().getCycleLength());
        assertEquals(6, testUser.getUserDetails().getPeriodLength());
        assertEquals(LocalDate.of(2024, 3, 1), testUser.getUserDetails().getLastPeriodStartDate());
        
        verify(userRepository).findById(userId);
        verify(userRepository).save(testUser);
    }

    @Test
    void testGetCycleInfo_WithCustomDetails() {
        // Arrange
        Long userId = 1L;
        testUserDetails.setCycleLength(30);
        testUserDetails.setPeriodLength(6);
        testUserDetails.setLastPeriodStartDate(LocalDate.of(2024, 3, 1));

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        CycleInfoDTO cycleInfo = calendarService.getCycleInfo(userId);

        // Assert
        assertEquals(LocalDate.of(2024, 3, 1), cycleInfo.getLastPeriodStartDate());
        assertEquals(LocalDate.of(2024, 3, 6), cycleInfo.getLastPeriodEndDate());
        assertEquals(30, cycleInfo.getCycleLength());
        assertEquals(6, cycleInfo.getPeriodLength());
        assertEquals(LocalDate.of(2024, 3, 31), cycleInfo.getNextPeriodStartDate());
        assertNotNull(cycleInfo.getCurrentPhase());
        //assertEquals(5,cycleInfo.getDaysUntilNextPeriod());
        
        verify(userRepository).findById(userId);
    }

    @Test
    void testGetCycleInfo_WithDefaultDetails() {
        // Arrange
        Long userId = 1L;
        // No custom details set, will use defaults

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        CycleInfoDTO cycleInfo = calendarService.getCycleInfo(userId);

        // Assert
        assertEquals(28, cycleInfo.getCycleLength());
        assertEquals(5, cycleInfo.getPeriodLength());
        assertNotNull(cycleInfo.getLastPeriodStartDate());
        assertNotNull(cycleInfo.getLastPeriodEndDate());
        assertNotNull(cycleInfo.getNextPeriodStartDate());
        assertNotNull(cycleInfo.getCurrentPhase());
        assertTrue(cycleInfo.getDaysUntilNextPeriod() >= 0);
        
        verify(userRepository).findById(userId);
    }
}