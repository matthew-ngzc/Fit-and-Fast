package com.fastnfit.app.repository;

import com.fastnfit.app.model.Achievement;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement,Long> {
    List<Achievement> findByTitleContaining(String title);
    Optional<Achievement> findByTitle(String title);
}
