package com.fastnfit.app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fastnfit.app.model.Achievement;
import com.fastnfit.app.repository.AchievementRepository;

import java.util.Arrays;
import java.util.List;

@Configuration
public class AchievementInitializer {

    @Bean
    CommandLineRunner initAchievements(AchievementRepository repository) {
        return args -> {
            // Check if achievements are already initialized
            if (repository.count() == 0) {
                List<Achievement> defaultAchievements = Arrays.asList(
                    createAchievement("5 Day Streak", "Complete workouts for 5 consecutive days"),
                    createAchievement("10 Workouts", "Complete 10 workouts"),
                    createAchievement("30 Day Streak", "Complete workouts for 30 consecutive days")
                );
                repository.saveAll(defaultAchievements);
            }
        };
    }
    
    private Achievement createAchievement(String title, String description) {
        Achievement achievement = new Achievement();
        achievement.setTitle(title);
        achievement.setDescription(description);
        return achievement;
    }
}
