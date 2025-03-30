// HistoryService.java
package com.fastnfit.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import com.fastnfit.app.dto.ActivityOverviewDTO;
import com.fastnfit.app.dto.DailySummaryDTO;
import com.fastnfit.app.dto.HistoryDTO;
import com.fastnfit.app.dto.WorkoutDTO;
import com.fastnfit.app.model.Achievement;
import com.fastnfit.app.model.History;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.Workout;
import com.fastnfit.app.repository.HistoryRepository;
import com.fastnfit.app.repository.UserRepository;
import com.fastnfit.app.repository.WorkoutRepository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final WorkoutService workoutService;
    private final UserStreakService userStreakService;
    private final AchievementService achievementService;
    private final UserAchievementService userAchievementService;

    @Autowired
    public HistoryService(HistoryRepository historyRepository,
            UserRepository userRepository,
            WorkoutRepository workoutRepository,
            WorkoutService workoutService,
            UserStreakService userStreakService,
            AchievementService achievementService,
            UserAchievementService userAchievementService) {
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
        this.workoutRepository = workoutRepository;
        this.workoutService = workoutService;
        this.userStreakService = userStreakService;
        this.achievementService = achievementService;
        this.userAchievementService = userAchievementService;
    }

    public List<HistoryDTO> getUserHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return historyRepository.findByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<HistoryDTO> getUserHistoryBetweenDates(Long userId, Date startDate, Date endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Timestamp startTimestamp = new Timestamp(startDate.getTime());
        Timestamp endTimestamp = new Timestamp(endDate.getTime());

        return historyRepository.findByUserAndWorkoutDateTimeBetween(user, startTimestamp, endTimestamp).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public HistoryDTO createHistory(Long userId, HistoryDTO historyDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        History history = new History();
        history.setUser(user);

        // Convert Date to Timestamp if needed
        if (historyDTO.getWorkoutDateTime() != null) {
            history.setWorkoutDateTime(new Timestamp(historyDTO.getWorkoutDateTime().getTime()));
        }

        Optional<Workout> workout = workoutRepository.findById(historyDTO.getWorkout().getWorkoutId());
        if (workout.isPresent()) {
            history.setWorkout(workout.get());
        }

        history.setCaloriesBurned(historyDTO.getCaloriesBurned());
        history.setDurationInMinutes(historyDTO.getDurationInMinutes());

        History savedHistory = historyRepository.save(history);
        return convertToDTO(savedHistory);
    }

    public HistoryDTO recordWorkoutCompletion(Long userId, WorkoutDTO workout) {
        Calendar currentUtilCalendar = Calendar.getInstance();
        HistoryDTO dto = new HistoryDTO();
        dto.setCaloriesBurned(workout.getCalories());
        dto.setWorkout(workout);
        dto.setWorkoutDateTime(new Timestamp(currentUtilCalendar.getTimeInMillis()));
        dto.setDurationInMinutes(workout.getDurationInMinutes());

        HistoryDTO result = createHistory(userId, dto);
        userStreakService.updateStreak(userId);
        checkWorkoutCountAchievementsForUser(userId);
        return result;
    }

    // ACTIVITY METHODS
    public DailySummaryDTO getTodaySummary(Long userId) {
        LocalDate today = LocalDate.now();
        Timestamp start = Timestamp.valueOf(today.atStartOfDay());
        Timestamp end = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        int calories = historyRepository.sumCaloriesBurnedByUserBetween(userId, start, end);
        int minutes = historyRepository.sumTimeExercisedByUserBetween(userId, start, end);

        return new DailySummaryDTO(today, calories, minutes);
    }

    // returns a list of daily summaries in ascending date order
    public List<DailySummaryDTO> getWeeklySummary(Long userId) {
        List<DailySummaryDTO> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            Timestamp start = Timestamp.valueOf(date.atStartOfDay());
            Timestamp end = Timestamp.valueOf(date.plusDays(1).atStartOfDay());

            int calories = historyRepository.sumCaloriesBurnedByUserBetween(userId, start, end);
            int minutes = historyRepository.sumTimeExercisedByUserBetween(userId, start, end);

            result.add(new DailySummaryDTO(date, calories, minutes));
        }

        return result;
    }

    // method to 1 shot load all the things needed for activity, instead of multiple
    // http requests
    public ActivityOverviewDTO getActivityOverview(Long userId) {
        DailySummaryDTO today = getTodaySummary(userId);
        List<DailySummaryDTO> weekly = getWeeklySummary(userId);
        List<HistoryDTO> recent = loadMoreHistory(userId, LocalDateTime.now(), 5); // You can adjust 5 to whatever feels
                                                                                   // best

        return new ActivityOverviewDTO(today, weekly, recent);
    }

    public List<HistoryDTO> loadMoreHistory(Long userId, LocalDateTime after, int limit) {
        Timestamp afterTimestamp = Timestamp.valueOf(after);
        Pageable pageable = PageRequest.of(0, limit);

        return historyRepository.findMoreHistory(userId, afterTimestamp, pageable).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // /
    // * Get total workout count for a user
    // */
    public int getTotalHistoryCountByUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return historyRepository.countByUser(user.get());
        }
        return -1;
    }

    // /
    // * Check and update workout count-based achievements
    // */
    private void checkWorkoutCountAchievementsForUser(Long userId) {
        int workoutCount = getTotalHistoryCountByUser(userId);

        // Check for 10 workouts achievement
        if (workoutCount >= 10) {
            Optional<Achievement> achievement = achievementService.getAchievementByTitle("10 Workouts");
            if (achievement.isPresent()) {
                userAchievementService.completeAchievement(userId, achievement.get().getAchievementId());
            }
        }
    }

    /**
     * Get total calories burned by a user
     */
    public int getTotalCaloriesBurnedByUser(Long userId) {
        return historyRepository.sumCaloriesBurnedByUserId(userId);
    }

    /**
     * Get total minutes spent by a user
     */
    public int getTotalDurationByUser(Long userId) {
        return historyRepository.sumTimeExercisedByUserId(userId);
    }

    public HistoryDTO convertToDTO(History history) {
        HistoryDTO dto = new HistoryDTO();
        dto.setHistoryId(history.getHistoryId());
        dto.setName(history.getWorkout().getName());
        dto.setCaloriesBurned(history.getCaloriesBurned());
        dto.setDurationInMinutes(history.getDurationInMinutes());
        dto.setWorkoutDateTime(history.getWorkoutDateTime());

        WorkoutDTO workoutDTO = workoutService.convertToDTO(history.getWorkout());

        dto.setWorkout(workoutDTO);
        return dto;
    }
}