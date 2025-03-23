package com.fastnfit.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//retrieve 1 shot for activity tab, all info inside.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityOverviewDTO {
    private DailySummaryDTO today;
    private List<DailySummaryDTO> weekly;
    private List<HistoryDTO> recentWorkouts; //only loads the last 5 workouts, load incrementally as user scrolls
}
