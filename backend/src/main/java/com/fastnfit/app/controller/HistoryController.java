// HistoryController.java
package com.fastnfit.app.controller;

import com.fastnfit.app.dto.HistoryDTO;
import com.fastnfit.app.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/history")
@CrossOrigin(origins = "*")
public class HistoryController {

    private final HistoryService historyService;

    @Autowired
    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<HistoryDTO>> getUserHistory(@PathVariable Long userId) {
        List<HistoryDTO> history = historyService.getUserHistory(userId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<HistoryDTO>> getUserHistoryBetweenDates(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<HistoryDTO> history = historyService.getUserHistoryBetweenDates(userId, startDate, endDate);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<HistoryDTO> createHistory(
            @PathVariable Long userId,
            @Valid @RequestBody HistoryDTO historyDTO) {
        HistoryDTO createdHistory = historyService.createHistory(userId, historyDTO);
        return new ResponseEntity<>(createdHistory, HttpStatus.CREATED);
    }
}