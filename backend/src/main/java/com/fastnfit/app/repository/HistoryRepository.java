// HistoryRepository.java
package com.fastnfit.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fastnfit.app.model.History;
import com.fastnfit.app.model.User;

import java.util.Date;
import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

    List<History> findByUser(User user);

    List<History> findByUserAndRoutineDateBetween(User user, Date startDate, Date endDate);

    Integer countByUserAndRoutineDateBetween(User user, Date startDate, Date endDate);

    Boolean existsByUserAndRoutineDateGreaterThanEqual(User user, Date startDate);

    Integer countWorkoutsByUserAndRoutineDate(User user, Date date);

    // /
    // * Count all workouts for a user
    // */
    int countByUser(User user);

    // /
    // * Find recent workouts for a user
    // */
    @Query("SELECT h FROM History h WHERE h.user.userId = :userId ORDER BY h.routineDate DESC, h.routineTime DESC LIMIT :limit")
    List<History> findByUserIdOrderByRoutineDateDescRoutineTimeDesc(@Param("userId") Long userId,
            @Param("limit") int limit);

    // /
    // * Sum total calories burned by a user
    // */
    @Query("SELECT COALESCE(SUM(h.caloriesBurned), 0) FROM History h WHERE h.user.userId = :userId")
    int sumCaloriesBurnedByUserId(@Param("userId") Long userId);
}