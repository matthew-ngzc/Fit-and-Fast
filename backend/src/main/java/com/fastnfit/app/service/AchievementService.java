package com.fastnfit.app.service;

import com.fastnfit.app.model.Achievement;
import com.fastnfit.app.repository.AchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AchievementService {
    
    private final AchievementRepository achievementRepository;
    
    @Autowired
    public AchievementService(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }
    
    // /
    //  * Get all achievements
    //  */
    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }
    
    // /
    //  * Get achievement by ID
    //  */
    public Optional<Achievement> getAchievementById(Long id) {
        return achievementRepository.findById(id);
    }
    
    // /
    //  * Get achievement by title
    //  */
    public Optional<Achievement> getAchievementByTitle(String title) {
        return achievementRepository.findByTitle(title);
    }
    
    // /
    //  * Create a new achievement
    //  */
    public Achievement createAchievement(Achievement achievement) {
        return achievementRepository.save(achievement);
    }
    

    //  * Update an existing achievement
    //  */
    public Achievement updateAchievement(Achievement achievement) {
        return achievementRepository.save(achievement);
    }
    
    // /
    //  * Delete an achievement
    //  */
    public void deleteAchievement(Long id) {
        achievementRepository.deleteById(id);
    }
}