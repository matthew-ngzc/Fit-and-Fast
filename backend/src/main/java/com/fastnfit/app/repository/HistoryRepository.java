// HistoryRepository.java
package com.fastnfit.app.repository;


import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fastnfit.app.model.History;
import com.fastnfit.app.model.User;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

    List<History> findByUser(User user);

    List<History> findByUserAndWorkoutDateTimeBetween(User user, Timestamp startDate, Timestamp endDate);

    Integer countByUserAndWorkoutDateTimeBetween(User user, Timestamp startDate, Timestamp endDate);

    Boolean existsByUserAndWorkoutDateTimeGreaterThanEqual(User user, Timestamp startDate);

    Integer countWorkoutsByUserAndWorkoutDateTime(User user, Timestamp date);

    // Count all workouts for a user
    int countByUser(User user);

    // Find recent workouts for a user
    @Query("SELECT h FROM History h WHERE h.user.userId = :userId ORDER BY h.workoutDateTime DESC LIMIT :limit")
    List<History> findByUserIdOrderByWorkoutDateTimeDesc(@Param("userId") Long userId,
            @Param("limit") int limit);

    // Sum total calories burned by a user
    @Query("SELECT COALESCE(SUM(h.caloriesBurned), 0) FROM History h WHERE h.user.userId = :userId")
    int sumCaloriesBurnedByUserId(@Param("userId") Long userId);

    // Sum total time in minutes exercised by a user
    @Query("SELECT COALESCE(SUM(h.durationInMinutes), 0) FROM History h WHERE h.user.userId = :userId")
    int sumTimeExercisedByUserId(@Param("userId") Long userId);

    //sum of calories within some duration
    @Query("SELECT COALESCE(SUM(h.caloriesBurned), 0) FROM History h WHERE h.user.userId = :userId AND h.workoutDateTime BETWEEN :start AND :end")
    int sumCaloriesBurnedByUserBetween(@Param("userId") Long userId, @Param("start") Timestamp start, @Param("end") Timestamp end);

    //sum of minutes within some duration
    @Query("SELECT COALESCE(SUM(h.durationInMinutes), 0) FROM History h WHERE h.user.userId = :userId AND h.workoutDateTime BETWEEN :start AND :end")
    int sumTimeExercisedByUserBetween(@Param("userId") Long userId, @Param("start") Timestamp start, @Param("end") Timestamp end);

    //load more history (some number of entries after this datetime(the datetime of last entry retrieved))
    //pageable means to load chunks of data at once
    @Query("SELECT h FROM History h WHERE h.user.userId = :userId AND h.workoutDateTime < :after ORDER BY h.workoutDateTime DESC")
    List<History> findMoreHistory(@Param("userId") Long userId, @Param("after") Timestamp after, Pageable pageable);

}