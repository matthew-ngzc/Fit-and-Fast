// HistoryService.java
package com.fastnfit.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fastnfit.app.dto.HistoryDTO;
import com.fastnfit.app.dto.WorkoutDTO;
import com.fastnfit.app.model.History;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.Workout;
import com.fastnfit.app.repository.HistoryRepository;
import com.fastnfit.app.repository.UserRepository;
import com.fastnfit.app.repository.WorkoutRepository;

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

    @Autowired
    public HistoryService(HistoryRepository historyRepository,
                        UserRepository userRepository,
                        WorkoutRepository workoutRepository,
                        WorkoutService workoutService,
                        UserStreakService userStreakService) {
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
        this.workoutRepository = workoutRepository;
        this.workoutService=workoutService;
        this.userStreakService=userStreakService;
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
        
        return historyRepository.findByUserAndWorkoutDateBetween(user, startDate, endDate).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public HistoryDTO createHistory(Long userId, HistoryDTO historyDTO) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        History history = new History();
        history.setUser(user);
        history.setWorkoutDate(historyDTO.getWorkoutDate());
        history.setName(historyDTO.getName());
        
        Optional<Workout> workout=workoutRepository.findById(historyDTO.getWorkout().getWorkoutId());
        if (workout.isPresent()) {
            history.setWorkout(workout.get());
        }

        history.setCaloriesBurned(historyDTO.getCaloriesBurned());
        history.setDurationInMinutes(historyDTO.getDurationInMinutes());
        
        History savedHistory = historyRepository.save(history);
        return convertToDTO(savedHistory);
    }

    public HistoryDTO recordWorkoutCompletion(Long userId,WorkoutDTO workout){
        Calendar currentUtilCalendar = Calendar.getInstance();
        HistoryDTO dto = new HistoryDTO();
        dto.setCaloriesBurned(workout.getCalories());
        dto.setWorkout(workout);
        dto.setWorkoutDate(currentUtilCalendar.getTime());
        dto.setDurationInMinutes(workout.getDurationInMinutes());
        
        HistoryDTO result=createHistory(userId, dto);
        userStreakService.updateStreak(userId);
        return result;
    }

    public HistoryDTO convertToDTO(History history) {
        HistoryDTO dto = new HistoryDTO();
        dto.setHistoryId(history.getHistoryId());
        dto.setWorkoutDate(history.getWorkoutDate());
        dto.setName(history.getName());
        dto.setCaloriesBurned(history.getCaloriesBurned());
        dto.setDurationInMinutes(history.getDurationInMinutes());
        
        WorkoutDTO workoutDTO=workoutService.convertToDTO(history.getWorkout());
        
        dto.setWorkout(workoutDTO);
        return dto;
    }
}
