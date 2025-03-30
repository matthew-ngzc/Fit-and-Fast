package com.fastnfit.app.service;

import com.fastnfit.app.dto.CycleInfoDTO;
import com.fastnfit.app.dto.CycleUpdateDTO;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.UserDetails;
import com.fastnfit.app.repository.HistoryRepository;
import com.fastnfit.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CalendarService {

    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;

    @Autowired
    public CalendarService(HistoryRepository historyRepository,
                           UserRepository userRepository,
                           RecommendationService recommendationService) {
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
    }

    // 1. Get workout dates for a month
    public List<LocalDate> getWorkoutDatesForMonth(Long userId, int year, int month) {
        User user = userRepository.findById(userId).orElseThrow();

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1);

        Timestamp startTimestamp = Timestamp.valueOf(start.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(end.atStartOfDay());

        return historyRepository.findByUserAndWorkoutDateTimeBetween(user, startTimestamp, endTimestamp)
                .stream()
                .map(h -> h.getWorkoutDateTime().toLocalDateTime().toLocalDate())
                .distinct()
                .collect(Collectors.toList());
    }

    // // 2. Predict next cycle
    // public CyclePredictionResponseDTO predictNextCycle(Long userId, CyclePredictionDTO dto) {
    //     LocalDate nextStart = dto.getLastPeriodStartDate().plusDays(dto.getCycleLength());
    //     LocalDate nextEnd = nextStart.plusDays(dto.getPeriodLength() - 1);

    //     CyclePredictionResponseDTO response = new CyclePredictionResponseDTO();
    //     response.setNextPeriodStart(nextStart);
    //     response.setNextPeriodEnd(nextEnd);

    //     return response;
    // }

    public void updateCycleInfo(Long userId, CycleUpdateDTO dto) {
        User user = userRepository.findById(userId).orElseThrow();
        UserDetails details = user.getUserDetails();
    
        details.setCycleLength(dto.getCycleLength());
        details.setPeriodLength(dto.getPeriodLength());
        details.setLastPeriodStartDate(dto.getLastPeriodStartDate());
    
        userRepository.save(user);
    }
    

    // 3. Get full cycle info
    public CycleInfoDTO getCycleInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        UserDetails details = user.getUserDetails();

        int cycleLength = Optional.ofNullable(details.getCycleLength()).orElse(28); //default is 28 if not set yet
        int periodLength = Optional.ofNullable(details.getPeriodLength()).orElse(5); //default is 5 if not set yet
        LocalDate lastPeriodStart = Optional.ofNullable(details.getLastPeriodStartDate()).orElse(LocalDate.now().minusDays(20)); // fallback default

        // if (details.getDob() != null) {
        //     lastPeriodStart = details.getDob();
        // }

        LocalDate lastPeriodEnd = lastPeriodStart.plusDays(periodLength - 1);
        LocalDate nextPeriodStart = lastPeriodStart.plusDays(cycleLength);
        long daysUntilNext = ChronoUnit.DAYS.between(LocalDate.now(), nextPeriodStart);

        String currentPhase = calculatePhase(lastPeriodStart, periodLength, cycleLength);

        CycleInfoDTO info = new CycleInfoDTO();
        info.setLastPeriodStartDate(lastPeriodStart);
        info.setLastPeriodEndDate(lastPeriodEnd);
        info.setCycleLength(cycleLength);
        info.setPeriodLength(periodLength);
        info.setNextPeriodStartDate(nextPeriodStart);
        info.setCurrentPhase(currentPhase);
        info.setDaysUntilNextPeriod((int) daysUntilNext);

        return info;
    }

    private String calculatePhase(LocalDate lastPeriodStart, int periodLength, int cycleLength) {
        LocalDate today = LocalDate.now();
        long daysSinceLastPeriod = ChronoUnit.DAYS.between(lastPeriodStart, today);

        if (daysSinceLastPeriod < periodLength) {
            return "Menstrual Phase";
        } else if (daysSinceLastPeriod < 14) {
            return "Follicular Phase";
        } else if (daysSinceLastPeriod < 16) {
            return "Ovulation Phase";
        } else {
            return "Luteal Phase";
        }
    }


}
