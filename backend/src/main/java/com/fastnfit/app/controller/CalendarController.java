package com.fastnfit.app.controller;

import com.fastnfit.app.dto.CycleInfoDTO;
import com.fastnfit.app.dto.CycleUpdateDTO;
import com.fastnfit.app.dto.WorkoutDTO;
import com.fastnfit.app.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final CalendarService calendarService;
    private final AuthUtils authUtils;

    @Autowired
    public CalendarController(CalendarService calendarService, AuthUtils authUtils) {
        this.calendarService = calendarService;
        this.authUtils = authUtils;
    }

    // 1. Get workout dates for a specific month
    @GetMapping("/workout-dates")
    public ResponseEntity<List<LocalDate>> getWorkoutDatesForMonth(
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        Long userId = authUtils.getCurrentUserId();
        List<LocalDate> dates = calendarService.getWorkoutDatesForMonth(userId, year, month);
        return ResponseEntity.ok(dates);
    }


    // // 2. Submit cycle info and predict next month's period
    // @PostMapping("/predict")
    // public ResponseEntity<CyclePredictionResponseDTO> predictCycle(@RequestBody CyclePredictionDTO dto) {
    //     Long userId = authUtils.getCurrentUserId();
    //     return ResponseEntity.ok(calendarService.predictNextCycle(userId, dto));
    // }

    //update cycle
    @PutMapping("/update-cycle")
    public ResponseEntity<Void> updateCycleInfo(@RequestBody CycleUpdateDTO dto) {
        Long userId = authUtils.getCurrentUserId();
        calendarService.updateCycleInfo(userId, dto);
        return ResponseEntity.ok().build();
    }


    // 3. Get full cycle info for display
    @GetMapping("/cycle-info")
    public ResponseEntity<CycleInfoDTO> getCycleInfo() {
        Long userId = authUtils.getCurrentUserId();
        CycleInfoDTO info = calendarService.getCycleInfo(userId);
        return ResponseEntity.ok(info);
    }
}
