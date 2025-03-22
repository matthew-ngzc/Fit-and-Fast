package com.fastnfit.app.service;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.time.LocalDate;
import java.time.DayOfWeek;

@Service
public class RecommendationService {

    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();

    @Autowired
    public RecommendationService(
            WorkoutRepository workoutRepository,
            UserRepository userRepository) {
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get daily workout recommendation for a user
     */
    public RecommendationDTO getDailyRecommendation(Long userId) {
        // Get user's preferences
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (!userOpt.isPresent()) {
            // If user not found, return a default workout
            return getDefaultRecommendation();
        }
        
        User user = userOpt.get();
        UserDetails userDetails = user.getUserDetails();
        
        // If user has no details, return default recommendation
        if (userDetails == null) {
            return getDefaultRecommendation();
        }
        
        // Get user preferences
        WorkoutGoal workoutGoal = userDetails.getWorkoutGoal();
        FitnessLevel fitnessLevel = userDetails.getFitnessLevel();
        WorkoutType workoutType = userDetails.getWorkoutType();
        PregnancyStatus pregnancyStatus = userDetails.getPregnancyStatus();
        
        // Convert fitness level to enum
        WorkoutLevel level = WorkoutLevel.Beginner; // Default
        if (fitnessLevel != null) {
            if (fitnessLevel.name().equalsIgnoreCase("intermediate")) {
                level = WorkoutLevel.Intermediate;
            } else if (fitnessLevel.name().equalsIgnoreCase("advanced")) {
                level = WorkoutLevel.Advanced;
            }
        }
        
        // Get day of week to vary recommendations
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        
        // Build workout filter based on user preferences
        List<Workout> filteredWorkouts;
        
        // First, check pregnancy status for specialized workouts
        if (pregnancyStatus.getValue().equalsIgnoreCase("yes, pregnant")) {
            filteredWorkouts = workoutRepository.findByCategory(WorkoutType.PRENATAL);
            if (!filteredWorkouts.isEmpty()) {
                return convertToRecommendationDTO(getRandomWorkout(filteredWorkouts));
            }
        }else if(pregnancyStatus.getValue().equalsIgnoreCase("yes postpartum")){
            filteredWorkouts = workoutRepository.findByCategory(WorkoutType.POSTNATAL);
            if (!filteredWorkouts.isEmpty()) {
                return convertToRecommendationDTO(getRandomWorkout(filteredWorkouts));
            }
        }
        
        // If no pregnancy workouts or not pregnant, check workout type
        if (workoutType != null) {
            filteredWorkouts = workoutRepository.findByCategoryAndLevel(workoutType, level);
            if (!filteredWorkouts.isEmpty()) {
                return convertToRecommendationDTO(getRandomWorkout(filteredWorkouts));
            }
        }
        
        // If no specific type or no results, try by fitness level
        filteredWorkouts = workoutRepository.findByLevel(level);
        if (!filteredWorkouts.isEmpty()) {
            return convertToRecommendationDTO(getRandomWorkout(filteredWorkouts));
        }
        
        // If still nothing, return default
        return getDefaultRecommendation();
    }
    
    /**
     * Get a default recommendation when user preferences can't be used
     */
    private RecommendationDTO getDefaultRecommendation() {
        List<Workout> allWorkouts = workoutRepository.findAll();
        
        if (allWorkouts.isEmpty()) {
            // Create a fallback recommendation if no workouts in database
            RecommendationDTO fallback = new RecommendationDTO();
            fallback.setTitle("Quick Cardio Workout");
            fallback.setDescription("A simple 15-minute cardio workout to get your heart rate up.");
            fallback.setCategory(WorkoutType.LOW_IMPACT);
            fallback.setLevel(WorkoutLevel.All_Levels);
            fallback.setCalories(150);
            return fallback;
        }
        
        return convertToRecommendationDTO(getRandomWorkout(allWorkouts));
    }
    
    /**
     * Pick a random workout from a list
     */
    private Workout getRandomWorkout(List<Workout> workouts) {
        int randomIndex = random.nextInt(workouts.size());
        return workouts.get(randomIndex);
    }
    
    /**
     * Convert Workout to RecommendationDTO
     */
    private RecommendationDTO convertToRecommendationDTO(Workout workout) {
        RecommendationDTO dto = new RecommendationDTO();
        dto.setWorkoutId(workout.getWorkoutId());
        dto.setTitle(workout.getName());
        dto.setDescription(workout.getDescription());
        dto.setCategory(workout.getCategory());
        dto.setLevel(workout.getLevel());
        dto.setCalories(workout.getCalories());
        return dto;
    }
}
