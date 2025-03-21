package com.fastnfit.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fastnfit.app.model.UserAchievement;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement,Long>{
    List<UserAchievement> findByUserUserIdAndCompletedTrue(Long userId);
    List<UserAchievement> findByUserUserIdAndCompletedFalse(Long userId);
    List<UserAchievement> findByUserUserId(long userId);
    Optional<UserAchievement> findByUserUserIdAndAchievementAchievementId(long userId,Long achievementId);
}
