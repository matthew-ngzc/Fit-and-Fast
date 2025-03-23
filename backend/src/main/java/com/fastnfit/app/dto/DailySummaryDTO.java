package com.fastnfit.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO to represent calories and duration for a specific day.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailySummaryDTO {

    @JsonFormat(pattern = "yyyy-MM-dd") // ensures JSON output is string format
    private LocalDate date;

    private int caloriesBurned;
    private int durationInMinutes;
}
