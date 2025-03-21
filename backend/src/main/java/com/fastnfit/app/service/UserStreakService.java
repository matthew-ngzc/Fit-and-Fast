package com.fastnfit.app.service;

import com.fastnfit.app.dto.StreakDTO;
import com.fastnfit.app.model.Achievement;
import com.fastnfit.app.model.History;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.UserDetails;
import com.fastnfit.app.repository.AchievementRepository;
import com.fastnfit.app.repository.HistoryRepository;
import com.fastnfit.app.repository.UserDetailsRepository;
import com.fastnfit.app.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserStreakService {
    private UserRepository userRepository;
    private HistoryRepository historyRepository;
    private AchievementRepository achievementRepository;
    private UserAchievementService userAchievementService;
    private UserDetailsRepository userDetailsRepository;
    
    @Autowired
    public UserStreakService(
            UserRepository userRepository,
            HistoryRepository historyRepository,
            AchievementRepository achievementRepository,
            UserAchievementService userAchievementService,
            UserDetailsRepository userDetailsRepository) {
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.achievementRepository = achievementRepository;
        this.userAchievementService = userAchievementService;
        this.userDetailsRepository=userDetailsRepository;
    }
    
    // /
    //  * Update streak when a workout is completed
    //  */
    @Transactional
    public void updateStreak(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            UserDetails userDetails = user.getUserDetails();
            
            if (userDetails != null) {
                // Get current date
                LocalDate today = LocalDate.now();
                LocalDate yesterday = today.minusDays(1);
                
                // Check if user worked out today
                boolean workedOutToday = hasWorkoutOnDate(userId, today);
                
                // Check if user worked out yesterday
                boolean workedOutYesterday = hasWorkoutOnDate(userId, yesterday);
                
                // Update streak
                int currentStreak = userDetails.getCurrentStreak();
                
                if (workedOutToday) {
                    if (workedOutYesterday || currentStreak == 0) {
                        // Increment streak if worked out yesterday or starting a new streak
                        currentStreak = currentStreak + 1;
                        userDetails.setCurrentStreak(currentStreak);
                        
                        // Update longest streak if needed
                        if (currentStreak > userDetails.getLongestStreak()) {
                            userDetails.setLongestStreak(currentStreak);
                        }
                        
                        // Check for streak-based achievements
                        checkStreakAchievements(userId, currentStreak);
                    }
                } else if (!workedOutYesterday) {
                    // Reset streak if missed two days in a row
                    userDetails.setCurrentStreak(0);
                }
                
                userRepository.save(user);
            }
        }
    }
    
    // /
    //  * Check if a user has a workout on a specific date
    //  */
    private boolean hasWorkoutOnDate(Long userId, LocalDate date) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User details not found"));
        
        Date startDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<History> workouts = historyRepository.findByUserAndRoutineDateBetween(user, startDate, endDate);
        return !workouts.isEmpty();
    }

    // * Check and update streak-based achievements
    //  */
    private void checkStreakAchievements(Long userId, int streak) {
        // Check for 5-day streak achievement
        if (streak >= 5) {
            Optional<Achievement> achievement = achievementRepository.findByTitle("5 Day Streak");
            if (achievement.isPresent()) {
                userAchievementService.completeAchievement(userId, achievement.get().getAchievementId());
            }
        }
        
        // Check for 30-day streak achievement
        if (streak >= 30) {
            Optional<Achievement> achievement = achievementRepository.findByTitle("30 Day Streak");
            if (achievement.isPresent()) {
                userAchievementService.completeAchievement(userId, achievement.get().getAchievementId());
            }
        }
    }

    public StreakDTO getUserStreak(Long userId) {
        UserDetails userDetails = userDetailsRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User details not found"));
        StreakDTO streakDTO=new StreakDTO();
        streakDTO.setDays(userDetails.getCurrentStreak());
        return streakDTO;
    }

    public StreakDTO getLongestUserStreak(Long userId) {
        UserDetails userDetails = userDetailsRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User details not found"));
        StreakDTO streakDTO=new StreakDTO();
        streakDTO.setDays(userDetails.getLongestStreak());
        return streakDTO;
    }

}
