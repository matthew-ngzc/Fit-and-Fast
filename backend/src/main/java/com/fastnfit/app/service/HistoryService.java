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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;

    @Autowired
    public HistoryService(HistoryRepository historyRepository,
                         UserRepository userRepository,
                         WorkoutRepository workoutRepository) {
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
        this.workoutRepository = workoutRepository;
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
        
        return historyRepository.findByUserAndRoutineDateBetween(user, startDate, endDate).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public HistoryDTO createHistory(Long userId, HistoryDTO historyDTO) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        History history = new History();
        history.setUser(user);
        history.setRoutineDate(historyDTO.getRoutineDate());
        history.setRoutineTime(historyDTO.getRoutineTime());
        history.setName(historyDTO.getName());
        
        List<Workout> workoutList = new ArrayList<>();
        if (historyDTO.getWorkoutList() != null) {
            workoutList = historyDTO.getWorkoutList().stream()
                .map(dto -> workoutRepository.findById(dto.getWorkoutId())
                    .orElseThrow(() -> new RuntimeException("Workout not found")))
                .collect(Collectors.toList());
        }
        history.setWorkoutList(workoutList);
        
        List<Workout> workoutDid = new ArrayList<>();
        if (historyDTO.getWorkoutDid() != null) {
            workoutDid = historyDTO.getWorkoutDid().stream()
                .map(dto -> workoutRepository.findById(dto.getWorkoutId())
                    .orElseThrow(() -> new RuntimeException("Workout not found")))
                .collect(Collectors.toList());
        }
        history.setWorkoutDid(workoutDid);
        
        History savedHistory = historyRepository.save(history);
        return convertToDTO(savedHistory);
    }

    private HistoryDTO convertToDTO(History history) {
        HistoryDTO dto = new HistoryDTO();
        dto.setHistoryId(history.getHistoryId());
        dto.setRoutineDate(history.getRoutineDate());
        dto.setRoutineTime(history.getRoutineTime());
        dto.setName(history.getName());
        
        List<WorkoutDTO> workoutListDTOs = history.getWorkoutList().stream()
            .map(workout -> {
                WorkoutDTO workoutDTO = new WorkoutDTO();
                workoutDTO.setWorkoutId(workout.getWorkoutId());
                workoutDTO.setCategory(workout.getCategory());
                workoutDTO.setName(workout.getName());
                workoutDTO.setDescription(workout.getDescription());
                workoutDTO.setLevel(workout.getLevel());
                workoutDTO.setCalories(workout.getCalories());
                return workoutDTO;
            })
            .collect(Collectors.toList());
        
        List<WorkoutDTO> workoutDidDTOs = history.getWorkoutDid().stream()
            .map(workout -> {
                WorkoutDTO workoutDTO = new WorkoutDTO();
                workoutDTO.setWorkoutId(workout.getWorkoutId());
                workoutDTO.setCategory(workout.getCategory());
                workoutDTO.setName(workout.getName());
                workoutDTO.setDescription(workout.getDescription());
                workoutDTO.setLevel(workout.getLevel());
                workoutDTO.setCalories(workout.getCalories());
                return workoutDTO;
            })
            .collect(Collectors.toList());
        
        dto.setWorkoutList(workoutListDTOs);
        dto.setWorkoutDid(workoutDidDTOs);
        return dto;
    }
}
