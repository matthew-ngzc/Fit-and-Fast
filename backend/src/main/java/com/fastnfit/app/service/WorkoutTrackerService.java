package com.fastnfit.app.service;

import com.fastnfit.app.model.Achievement;
import com.fastnfit.app.model.History;
import com.fastnfit.app.model.User;
import com.fastnfit.app.repository.AchievementRepository;
import com.fastnfit.app.repository.HistoryRepository;
import com.fastnfit.app.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WorkoutTrackerService {
    
    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final UserAchievementService userAchievementService;
    private final UserStreakService userStreakService;
    
    @Autowired
    public WorkoutTrackerService(
            HistoryRepository historyRepository,
            UserRepository userRepository,
            AchievementRepository achievementRepository,
            UserAchievementService userAchievementService,
            UserStreakService userStreakService) {
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
        this.userAchievementService = userAchievementService;
        this.userStreakService = userStreakService;
    }
    
    // /
    //  * Log a completed workout
    //  */
    @Transactional
    public History logWorkout(History history) {
        // Save the workout history
        History savedHistory = historyRepository.save(history);
        
        // Update user streak
        userStreakService.updateStreak(history.getUser().getUserId());
        
        // Check workout count achievements
        checkWorkoutCountAchievements(history.getUser().getUserId());
        
        return savedHistory;
    }
    
    // /
    //  * Get total workout count for a user
    //  */
    public int getTotalWorkoutCount(Long userId) {
        Optional<User> user=userRepository.findById(userId);
        if (user.isPresent()){
            return historyRepository.countByUser(user.get());
        }
        return -1;
    }
    
    // /
    //  * Check and update workout count-based achievements
    //  */
    private void checkWorkoutCountAchievements(Long userId) {
        int workoutCount = getTotalWorkoutCount(userId);
        
        // Check for 10 workouts achievement
        if (workoutCount >= 10) {
            Optional<Achievement> achievement = achievementRepository.findByTitle("10 Workouts");
            if (achievement.isPresent()) {
                userAchievementService.completeAchievement(userId, achievement.get().getAchievementId());
            }
        }
    }
    
    // /
    //  * Get recent workouts for a user
    //  */
    public List<History> getRecentWorkouts(Long userId, int limit) {
        return historyRepository.findByUserIdOrderByWorkoutDateDesc(userId, limit);
    }
    
    /**
     * Get total calories burned by a user
     */
    public int getTotalCaloriesBurned(Long userId) {
        return historyRepository.sumCaloriesBurnedByUserId(userId);
    }

    /**
     * Get total minutes spent by a user
     */
    public int getTotalDuration(Long userId) {
        return historyRepository.sumTimeExercisedByUserId(userId);
    }
}
