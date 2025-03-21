package com.fastnfit.app.service;

import com.fastnfit.app.model.Achievement;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.UserAchievement;
import com.fastnfit.app.repository.AchievementRepository;
import com.fastnfit.app.repository.UserAchievementRepository;
import com.fastnfit.app.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserAchievementService {
    
    private final UserAchievementRepository userAchievementRepository;
    //private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    
    @Autowired
    public UserAchievementService(
            UserAchievementRepository userAchievementRepository,
            //UserRepository userRepository,
            AchievementRepository achievementRepository) {
        this.userAchievementRepository = userAchievementRepository;
        //this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
    }
    
    // /
    //  * Initialize achievements for a new user
    //  * This method should be called when a new user is created
    //  */
    @Transactional
    public void initializeUserAchievements(User user) {
        List<Achievement> allAchievements = achievementRepository.findAll();
        
        for (Achievement achievement : allAchievements) {
            UserAchievement userAchievement = new UserAchievement();
            userAchievement.setUser(user);
            userAchievement.setAchievement(achievement);
            userAchievement.setCompleted(false);
            userAchievementRepository.save(userAchievement);
        }
    }
    
    // /
    //  * Get all achievements for a user
    //  */
    public List<UserAchievement> getUserAchievements(Long userId) {
        return userAchievementRepository.findByUserUserId(userId);
    }
    
    // /
    //  * Check if a user has completed a specific achievement
    //  */
    public boolean hasCompletedAchievement(Long userId, Long achievementId) {
        Optional<UserAchievement> userAchievement = 
                userAchievementRepository.findByUserUserIdAndAchievementAchievementId(userId, achievementId);
        
        return userAchievement.isPresent() && userAchievement.get().isCompleted();
    }
    
    // /
    //  * Mark an achievement as completed for a user
    //  */
    @Transactional
    public void completeAchievement(Long userId, Long achievementId) {
        Optional<UserAchievement> userAchievementOpt = 
                userAchievementRepository.findByUserUserIdAndAchievementAchievementId(userId, achievementId);
        
        if (userAchievementOpt.isPresent()) {
            UserAchievement userAchievement = userAchievementOpt.get();
            userAchievement.setCompleted(true);
            userAchievementRepository.save(userAchievement);
        }
    }
    
    // /
    //  * Get all completed achievements for a user
    //  */
    public List<UserAchievement> getCompletedAchievements(Long userId) {
        return userAchievementRepository.findByUserUserIdAndCompletedTrue(userId);
    }
    
    // /
    //  * Get all incomplete achievements for a user
    //  */
    public List<UserAchievement> getIncompleteAchievements(Long userId) {
        return userAchievementRepository.findByUserUserIdAndCompletedFalse(userId);
    }
}
