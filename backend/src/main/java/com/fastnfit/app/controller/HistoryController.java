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
    private final AuthUtils authUtils;

    @Autowired
    public HistoryController(HistoryService historyService,AuthUtils authUtils) {
        this.historyService = historyService;
        this.authUtils=authUtils;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<HistoryDTO>> getUserHistory() {
        Long userId = authUtils.getCurrentUserId();
        List<HistoryDTO> history = historyService.getUserHistory(userId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<HistoryDTO>> getUserHistoryBetweenDates(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        Long userId = authUtils.getCurrentUserId();
        List<HistoryDTO> history = historyService.getUserHistoryBetweenDates(userId, startDate, endDate);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<HistoryDTO> createHistory(
            @Valid @RequestBody HistoryDTO historyDTO) {
        Long userId = authUtils.getCurrentUserId();
        HistoryDTO createdHistory = historyService.createHistory(userId, historyDTO);
        return new ResponseEntity<>(createdHistory, HttpStatus.CREATED);
    }
}