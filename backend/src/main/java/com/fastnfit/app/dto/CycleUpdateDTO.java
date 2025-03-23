package com.fastnfit.app.dto;

import lombok.Data;
import java.time.LocalDate;

//for receiving data through the GET at calendar/update-cycle endpoint

@Data
public class CycleUpdateDTO {
    private int cycleLength;
    private int periodLength;
    private LocalDate lastPeriodStartDate;
}
