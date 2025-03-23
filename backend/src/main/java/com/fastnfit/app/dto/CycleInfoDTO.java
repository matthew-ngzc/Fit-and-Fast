package com.fastnfit.app.dto;

import lombok.Data;
import java.time.LocalDate;

//for UI display and tracking

@Data
public class CycleInfoDTO {
    private LocalDate lastPeriodStartDate;
    private LocalDate lastPeriodEndDate;
    private int cycleLength;
    private int periodLength;
    private LocalDate nextPeriodStartDate;
    private String currentPhase; // e.g., "Follicular Phase"
    private int daysUntilNextPeriod;
}
