package com.fastnfit.app.UnitTests;

import com.fastnfit.app.dto.RecommendationDTO;
import com.fastnfit.app.enums.FitnessLevel;
import com.fastnfit.app.enums.PregnancyStatus;
import com.fastnfit.app.enums.WorkoutGoal;
import com.fastnfit.app.enums.WorkoutLevel;
import com.fastnfit.app.enums.WorkoutType;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.UserDetails;
import com.fastnfit.app.model.Workout;
import com.fastnfit.app.repository.UserRepository;
import com.fastnfit.app.repository.WorkoutRepository;
import com.fastnfit.app.service.RecommendationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    private User testUser;
    private UserDetails testUserDetails;
    private Workout testWorkout;
    private List<Workout> workoutList;

    @BeforeEach
    void setUp() {
        // Set up test user
        testUser = new User();
        testUser.setUserId(1L);
        testUserDetails = new UserDetails();
        testUserDetails.setWorkoutGoal(WorkoutGoal.WEIGHT_LOSS);
        testUserDetails.setFitnessLevel(FitnessLevel.INTERMEDIATE);
        testUserDetails.setWorkoutType(WorkoutType.HIIT);
        testUserDetails.setPregnancyStatus(PregnancyStatus.NO);
        testUser.setUserDetails(testUserDetails);

        // Set up test workout
        testWorkout = new Workout();
        testWorkout.setWorkoutId(1L);
        testWorkout.setName("Test Workout");
        testWorkout.setDescription("Test Description");
        testWorkout.setCategory(WorkoutType.HIIT);
        testWorkout.setLevel(WorkoutLevel.Intermediate);
        testWorkout.setCalories(200);

        workoutList = Arrays.asList(testWorkout);
    }

    @Test
    void testGetDailyRecommendation_UserFound_WithPreferences() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(workoutRepository.findByCategoryAndLevel(WorkoutType.HIIT, WorkoutLevel.Intermediate))
                .thenReturn(workoutList);

        // Act
        RecommendationDTO recommendation = recommendationService.getDailyRecommendation(1L);

        // Assert
        assertNotNull(recommendation);
        assertEquals("Test Workout", recommendation.getTitle());
        assertEquals(WorkoutType.HIIT, recommendation.getCategory());
        assertEquals(WorkoutLevel.Intermediate, recommendation.getLevel());
        
        verify(userRepository).findById(1L);
        verify(workoutRepository).findByCategoryAndLevel(WorkoutType.HIIT, WorkoutLevel.Intermediate);
    }

    @Test
    void testGetDailyRecommendation_UserNotFound() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        when(workoutRepository.findAll()).thenReturn(workoutList);

        // Act
        RecommendationDTO recommendation = recommendationService.getDailyRecommendation(99L);

        // Assert
        assertNotNull(recommendation);
        assertEquals("Test Workout", recommendation.getTitle());
        
        verify(userRepository).findById(99L);
        verify(workoutRepository).findAll();
    }

    @Test
    void testGetDailyRecommendation_NoUserDetails() {
        // Arrange
        User userWithoutDetails = new User();
        userWithoutDetails.setUserId(2L);
        userWithoutDetails.setUserDetails(null);
        
        when(userRepository.findById(2L)).thenReturn(Optional.of(userWithoutDetails));
        when(workoutRepository.findAll()).thenReturn(workoutList);

        // Act
        RecommendationDTO recommendation = recommendationService.getDailyRecommendation(2L);

        // Assert
        assertNotNull(recommendation);
        assertEquals("Test Workout", recommendation.getTitle());
        
        verify(userRepository).findById(2L);
        verify(workoutRepository).findAll();
    }

    @Test
    void testGetDailyRecommendation_PregnantUser() {
        // Arrange
        UserDetails pregnantUserDetails = new UserDetails();
        pregnantUserDetails.setWorkoutGoal(WorkoutGoal.WEIGHT_LOSS);
        pregnantUserDetails.setFitnessLevel(FitnessLevel.INTERMEDIATE);
        pregnantUserDetails.setWorkoutType(WorkoutType.HIIT);
        pregnantUserDetails.setPregnancyStatus(PregnancyStatus.PREGNANT);
        
        User pregnantUser = new User();
        pregnantUser.setUserId(3L);
        pregnantUser.setUserDetails(pregnantUserDetails);
        
        Workout prenatalWorkout = new Workout();
        prenatalWorkout.setWorkoutId(2L);
        prenatalWorkout.setName("Prenatal Yoga");
        prenatalWorkout.setDescription("Safe yoga for pregnant women");
        prenatalWorkout.setCategory(WorkoutType.PRENATAL);
        prenatalWorkout.setLevel(WorkoutLevel.All_Levels);
        prenatalWorkout.setCalories(150);
        
        when(userRepository.findById(3L)).thenReturn(Optional.of(pregnantUser));
        when(workoutRepository.findByCategory(WorkoutType.PRENATAL))
                .thenReturn(Arrays.asList(prenatalWorkout));

        // Act
        RecommendationDTO recommendation = recommendationService.getDailyRecommendation(3L);

        // Assert
        assertNotNull(recommendation);
        assertEquals("Prenatal Yoga", recommendation.getTitle());
        assertEquals(WorkoutType.PRENATAL, recommendation.getCategory());
        
        verify(userRepository).findById(3L);
        verify(workoutRepository).findByCategory(WorkoutType.PRENATAL);
    }

    @Test
    void testGetDailyRecommendation_PostpartumUser() {
        // Arrange
        UserDetails postpartumUserDetails = new UserDetails();
        postpartumUserDetails.setWorkoutGoal(WorkoutGoal.WEIGHT_LOSS);
        postpartumUserDetails.setFitnessLevel(FitnessLevel.INTERMEDIATE);
        postpartumUserDetails.setWorkoutType(WorkoutType.HIIT);
        postpartumUserDetails.setPregnancyStatus(PregnancyStatus.POSTPARTUM);
        
        User postpartumUser = new User();
        postpartumUser.setUserId(4L);
        postpartumUser.setUserDetails(postpartumUserDetails);
        
        Workout postnatalWorkout = new Workout();
        postnatalWorkout.setWorkoutId(3L);
        postnatalWorkout.setName("Postnatal Recovery");
        postnatalWorkout.setDescription("Safe exercises for postpartum recovery");
        postnatalWorkout.setCategory(WorkoutType.POSTNATAL);
        postnatalWorkout.setLevel(WorkoutLevel.Beginner);
        postnatalWorkout.setCalories(120);
        
        when(userRepository.findById(4L)).thenReturn(Optional.of(postpartumUser));
        when(workoutRepository.findByCategory(WorkoutType.POSTNATAL))
                .thenReturn(Arrays.asList(postnatalWorkout));

        // Act
        RecommendationDTO recommendation = recommendationService.getDailyRecommendation(4L);

        // Assert
        assertNotNull(recommendation);
        assertEquals("Postnatal Recovery", recommendation.getTitle());
        assertEquals(WorkoutType.POSTNATAL, recommendation.getCategory());
        
        verify(userRepository).findById(4L);
        verify(workoutRepository).findByCategory(WorkoutType.POSTNATAL);
    }

    @Test
    void testGetDailyRecommendation_NoMatchingWorkouts_UsesFitnessLevel() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(workoutRepository.findByCategoryAndLevel(WorkoutType.HIIT, WorkoutLevel.Intermediate))
                .thenReturn(new ArrayList<>());
        when(workoutRepository.findByLevel(WorkoutLevel.Intermediate))
                .thenReturn(workoutList);

        // Act
        RecommendationDTO recommendation = recommendationService.getDailyRecommendation(1L);

        // Assert
        assertNotNull(recommendation);
        assertEquals("Test Workout", recommendation.getTitle());
        
        verify(userRepository).findById(1L);
        verify(workoutRepository).findByCategoryAndLevel(WorkoutType.HIIT, WorkoutLevel.Intermediate);
        verify(workoutRepository).findByLevel(WorkoutLevel.Intermediate);
    }

    @Test
    void testGetDailyRecommendation_NoMatchingWorkouts_DefaultRecommendation() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(workoutRepository.findByCategoryAndLevel(WorkoutType.HIIT, WorkoutLevel.Intermediate))
                .thenReturn(new ArrayList<>());
        when(workoutRepository.findByLevel(WorkoutLevel.Intermediate))
                .thenReturn(new ArrayList<>());
        when(workoutRepository.findAll())
                .thenReturn(workoutList);

        // Act
        RecommendationDTO recommendation = recommendationService.getDailyRecommendation(1L);

        // Assert
        assertNotNull(recommendation);
        assertEquals("Test Workout", recommendation.getTitle());
        
        verify(userRepository).findById(1L);
        verify(workoutRepository).findByCategoryAndLevel(WorkoutType.HIIT, WorkoutLevel.Intermediate);
        verify(workoutRepository).findByLevel(WorkoutLevel.Intermediate);
        verify(workoutRepository).findAll();
    }

    @Test
    void testGetDailyRecommendation_NoWorkoutsInDatabase() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(workoutRepository.findByCategoryAndLevel(any(), any()))
                .thenReturn(new ArrayList<>());
        when(workoutRepository.findByLevel(any()))
                .thenReturn(new ArrayList<>());
        when(workoutRepository.findAll())
                .thenReturn(new ArrayList<>());

        // Act
        RecommendationDTO recommendation = recommendationService.getDailyRecommendation(1L);

        // Assert
        assertNotNull(recommendation);
        assertEquals("Quick Cardio Workout", recommendation.getTitle());
        assertEquals("A simple 15-minute cardio workout to get your heart rate up.", recommendation.getDescription());
        assertEquals(WorkoutType.LOW_IMPACT, recommendation.getCategory());
        assertEquals(WorkoutLevel.All_Levels, recommendation.getLevel());
        assertEquals(150, recommendation.getCalories());
        
        verify(userRepository).findById(1L);
        verify(workoutRepository).findByCategoryAndLevel(WorkoutType.HIIT, WorkoutLevel.Intermediate);
        verify(workoutRepository).findByLevel(WorkoutLevel.Intermediate);
        verify(workoutRepository).findAll();
    }

    @Test
    void testGetDailyRecommendation_AdvancedFitnessLevel() {
        // Arrange
        UserDetails advancedUserDetails = new UserDetails();
        advancedUserDetails.setWorkoutGoal(WorkoutGoal.WEIGHT_LOSS);
        advancedUserDetails.setFitnessLevel(FitnessLevel.ADVANCED);
        advancedUserDetails.setWorkoutType(WorkoutType.STRENGTH);
        advancedUserDetails.setPregnancyStatus(PregnancyStatus.NO);
        
        User advancedUser = new User();
        advancedUser.setUserId(5L);
        advancedUser.setUserDetails(advancedUserDetails);
        
        Workout advancedWorkout = new Workout();
        advancedWorkout.setWorkoutId(4L);
        advancedWorkout.setName("Advanced Strength Training");
        advancedWorkout.setDescription("High intensity strength workout");
        advancedWorkout.setCategory(WorkoutType.STRENGTH);
        advancedWorkout.setLevel(WorkoutLevel.Advanced);
        advancedWorkout.setCalories(300);
        
        when(userRepository.findById(5L)).thenReturn(Optional.of(advancedUser));
        when(workoutRepository.findByCategoryAndLevel(WorkoutType.STRENGTH, WorkoutLevel.Advanced))
                .thenReturn(Arrays.asList(advancedWorkout));

        // Act
        RecommendationDTO recommendation = recommendationService.getDailyRecommendation(5L);

        // Assert
        assertNotNull(recommendation);
        assertEquals("Advanced Strength Training", recommendation.getTitle());
        assertEquals(WorkoutLevel.Advanced, recommendation.getLevel());
        
        verify(userRepository).findById(5L);
        verify(workoutRepository).findByCategoryAndLevel(WorkoutType.STRENGTH, WorkoutLevel.Advanced);
    }
}