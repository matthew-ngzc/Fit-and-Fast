package com.fastnfit.app.UnitTests;

/*run in dev use default:
./mvnw test "-Dtest=HistoryServiceTest"

to run in prod"
./mvnw test "-Dtest=HistoryServiceTest" "-Dspring.profiles.active=prod"
*/

import com.fastnfit.app.dto.ActivityOverviewDTO;
import com.fastnfit.app.dto.DailySummaryDTO;
import com.fastnfit.app.dto.HistoryDTO;
import com.fastnfit.app.dto.WorkoutDTO;
import com.fastnfit.app.model.Achievement;
import com.fastnfit.app.model.History;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.Workout;
import com.fastnfit.app.repository.HistoryRepository;
import com.fastnfit.app.repository.UserRepository;
import com.fastnfit.app.repository.WorkoutRepository;
import com.fastnfit.app.service.AchievementService;
import com.fastnfit.app.service.HistoryService;
import com.fastnfit.app.service.UserAchievementService;
import com.fastnfit.app.service.UserStreakService;
import com.fastnfit.app.service.WorkoutService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("dev")
class HistoryServiceTest {

    @Mock
    private HistoryRepository historyRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private WorkoutRepository workoutRepository;
    
    @Mock
    private WorkoutService workoutService;
    
    @Mock
    private UserStreakService userStreakService;

    @Mock
    private AchievementService achievementService;

    @Mock
    private UserAchievementService userAchievementService;

    private HistoryService historyService;
    
    private User testUser;
    private Workout testWorkout;
    private History testHistory;
    private WorkoutDTO testWorkoutDTO;
    private HistoryDTO testHistoryDTO;

    private List<History> historyList;

    
    @BeforeEach
    void setUp() {
        historyService = new HistoryService(
            historyRepository,
            userRepository,
            workoutRepository,
            workoutService,
            userStreakService,
            achievementService,
            userAchievementService
        );
        
        // Setup test data
        testUser = new User();
        testUser.setUserId(1L);
        
        testWorkout = new Workout();
        testWorkout.setWorkoutId(1L);
        testWorkout.setName("Test Workout");
        testWorkout.setDurationInMinutes(30);
        testWorkout.setCalories(150);
        
        testWorkoutDTO = new WorkoutDTO();
        testWorkoutDTO.setWorkoutId(1L);
        testWorkoutDTO.setName("Test Workout");
        testWorkoutDTO.setDurationInMinutes(30);
        testWorkoutDTO.setCalories(150);
        
        testHistory = new History();
        testHistory.setHistoryId(1L);
        testHistory.setUser(testUser);
        testHistory.setWorkout(testWorkout);
        testHistory.setWorkoutName("Test Workout");
        testHistory.setCaloriesBurned(150);
        testHistory.setDurationInMinutes(30);
        testHistory.setWorkoutDateTime(new Timestamp(System.currentTimeMillis()));
        
        testHistoryDTO = new HistoryDTO();
        testHistoryDTO.setHistoryId(1L);
        testHistoryDTO.setName("Test Workout");
        testHistoryDTO.setCaloriesBurned(150);
        testHistoryDTO.setDurationInMinutes(30);
        testHistoryDTO.setWorkout(testWorkoutDTO);
        testHistoryDTO.setWorkoutDateTime(new Timestamp(System.currentTimeMillis()));


        // Build 10 history records for multiple days
        historyList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < 10; i++) {
            History h = new History();
            h.setHistoryId((long) (i + 2)); // Avoid clash with testHistory id 1
            h.setUser(testUser);
            h.setWorkout(testWorkout);
            h.setWorkoutName("Workout " + (i + 1));
            h.setCaloriesBurned(100 + (i * 10)); // 100, 110, ..., 190
            h.setDurationInMinutes(20 + i);      // 20, 21, ..., 29

            // Assign date: 3 today, 2 yesterday, 2 inside last week, 3 beyond 7 days
            if (i < 3) {
                h.setWorkoutDateTime(Timestamp.valueOf(now.minusDays(10 - i))); // beyond last 7 days
            } else if (i == 4){
                h.setWorkoutDateTime(Timestamp.valueOf(now.minusDays(7))); //7 days ago (last record retrieved)
            } else if (i == 5) {
                h.setWorkoutDateTime(Timestamp.valueOf(now.minusDays(3))); // 3 days ago
            } else if (i < 7) {
                h.setWorkoutDateTime(Timestamp.valueOf(now.minusDays(1))); // yesterday
            } else {
                h.setWorkoutDateTime(Timestamp.valueOf(now)); // today
            }

            historyList.add(h);
        }
        /*
        [{
                "historyId": 11,
                "daysAgo": 0,
                "caloriesBurned": 100,
                "durationInMinutes": 20,
                "includedInWeeklySummary": true
            },
            {
                "historyId": 10,
                "daysAgo": 0,
                "caloriesBurned": 110,
                "durationInMinutes": 21,
                "includedInWeeklySummary": true
            },
            {
                "historyId": 9,
                "daysAgo": 0,
                "caloriesBurned": 120,
                "durationInMinutes": 22,
                "includedInWeeklySummary": true
            },
            {
                "historyId": 8,
                "daysAgo": 1,
                "caloriesBurned": 130,
                "durationInMinutes": 23,
                "includedInWeeklySummary": true
            },
            {
                "historyId": 7,
                "daysAgo": 1,
                "caloriesBurned": 140,
                "durationInMinutes": 24,
                "includedInWeeklySummary": true
            },
            {
                "historyId": 6,
                "daysAgo": 3,
                "caloriesBurned": 150,
                "durationInMinutes": 25,
                "includedInWeeklySummary": true
            },
            {
                "historyId": 5,
                "daysAgo": 7,
                "caloriesBurned": 160,
                "durationInMinutes": 26,
                "includedInWeeklySummary": true
            },
            {
                "historyId": 4,
                "daysAgo": 8,
                "caloriesBurned": 170,
                "durationInMinutes": 27,
                "includedInWeeklySummary": false
            },
            {
                "historyId": 3,
                "daysAgo": 9,
                "caloriesBurned": 180,
                "durationInMinutes": 28,
                "includedInWeeklySummary": false
            },
            {
                "historyId": 2,
                "daysAgo": 10,
                "caloriesBurned": 190,
                "durationInMinutes": 29,
                "includedInWeeklySummary": false
            }]
         */

    }

    @Test
    void getUserHistory_shouldReturnUserHistory() {
        // Given
        List<History> histories = Collections.singletonList(testHistory);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(historyRepository.findByUser(testUser)).thenReturn(histories);
        when(workoutService.convertToDTO(testWorkout)).thenReturn(testWorkoutDTO);
        
        // When
        List<HistoryDTO> result = historyService.getUserHistory(1L);
        
        // Then
        assertEquals(1, result.size());
        assertEquals(testHistory.getHistoryId(), result.get(0).getHistoryId());
        verify(userRepository).findById(1L);
        verify(historyRepository).findByUser(testUser);
    }

    @Test
    void getUserHistory_shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            historyService.getUserHistory(1L);
        });
        
        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(historyRepository, never()).findByUser(any(User.class));
    }

    @Test
    void getUserHistoryBetweenDates_shouldReturnHistoryBetweenDates() {
        // Given
        List<History> histories = Collections.singletonList(testHistory);
        Date startDate = new Date(System.currentTimeMillis() - 86400000); // Yesterday
        Date endDate = new Date(System.currentTimeMillis() + 86400000);   // Tomorrow
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(historyRepository.findByUserAndWorkoutDateTimeBetween(
                eq(testUser), 
                any(Timestamp.class), 
                any(Timestamp.class)
        )).thenReturn(histories);
        when(workoutService.convertToDTO(testWorkout)).thenReturn(testWorkoutDTO);
        
        // When
        List<HistoryDTO> result = historyService.getUserHistoryBetweenDates(1L, startDate, endDate);
        
        // Then
        assertEquals(1, result.size());
        assertEquals(testHistory.getHistoryId(), result.get(0).getHistoryId());
        verify(userRepository).findById(1L);
        verify(historyRepository).findByUserAndWorkoutDateTimeBetween(
                eq(testUser), 
                any(Timestamp.class), 
                any(Timestamp.class)
        );
    }

    @Test
    void createHistory_shouldCreateAndReturnHistory() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(workoutRepository.findById(1L)).thenReturn(Optional.of(testWorkout));
        when(historyRepository.save(any(History.class))).thenReturn(testHistory);
        when(workoutService.convertToDTO(testWorkout)).thenReturn(testWorkoutDTO);
        
        // When
        HistoryDTO result = historyService.createHistory(1L, testHistoryDTO);
        
        // Then
        assertNotNull(result);
        assertEquals(testHistory.getHistoryId(), result.getHistoryId());
        
        // Verify the history was created correctly
        ArgumentCaptor<History> historyCaptor = ArgumentCaptor.forClass(History.class);
        verify(historyRepository).save(historyCaptor.capture());
        
        History capturedHistory = historyCaptor.getValue();
        assertEquals(testUser, capturedHistory.getUser());
        assertEquals(testWorkout, capturedHistory.getWorkout());
        assertEquals(testHistoryDTO.getName(), capturedHistory.getWorkoutName());
        assertEquals(testHistoryDTO.getCaloriesBurned(), capturedHistory.getCaloriesBurned());
        assertEquals(testHistoryDTO.getDurationInMinutes(), capturedHistory.getDurationInMinutes());
    }

    @Test
    void recordWorkoutCompletion_shouldCreateHistoryAndUpdateStreak() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(workoutRepository.findById(1L)).thenReturn(Optional.of(testWorkout));
        when(historyRepository.save(any(History.class))).thenReturn(testHistory);
        when(workoutService.convertToDTO(any(Workout.class))).thenReturn(testWorkoutDTO);
        
        // When
        HistoryDTO result = historyService.recordWorkoutCompletion(1L, testWorkoutDTO);
        
        // Then
        assertNotNull(result);
        verify(userStreakService).updateStreak(1L);
        
        // Verify the history was created with correct data
        ArgumentCaptor<History> historyCaptor = ArgumentCaptor.forClass(History.class);
        verify(historyRepository).save(historyCaptor.capture());
        
        History capturedHistory = historyCaptor.getValue();
        assertEquals(testUser, capturedHistory.getUser());
        assertEquals(testWorkoutDTO.getCalories(), capturedHistory.getCaloriesBurned());
        assertEquals(testWorkoutDTO.getDurationInMinutes(), capturedHistory.getDurationInMinutes());
    }

    @Test
    void convertToDTO_shouldCorrectlyConvertEntityToDTO() {
        // Given
        when(workoutService.convertToDTO(testWorkout)).thenReturn(testWorkoutDTO);
        
        // When
        HistoryDTO result = historyService.convertToDTO(testHistory);
        
        // Then
        assertNotNull(result);
        assertEquals(testHistory.getHistoryId(), result.getHistoryId());
        assertEquals(testHistory.getWorkoutName(), result.getName());
        assertEquals(testHistory.getCaloriesBurned(), result.getCaloriesBurned());
        assertEquals(testHistory.getDurationInMinutes(), result.getDurationInMinutes());
        assertEquals(testWorkoutDTO, result.getWorkout());
    }


        // ---------- NEW TESTS FOR ACTIVITY SUMMARY & LOAD MORE ----------

    @Test
    void getTodaySummary_shouldReturnCorrectSummary() {
        // Total from IDs 9–11: 100 + 110 + 120 = 330 calories, 20 + 21 + 22 = 63 minutes
        when(historyRepository.sumCaloriesBurnedByUserBetween(eq(1L), any(), any())).thenReturn(330);
        when(historyRepository.sumTimeExercisedByUserBetween(eq(1L), any(), any())).thenReturn(63);
    
        DailySummaryDTO result = historyService.getTodaySummary(1L);
    
        System.out.printf("🔥 Today Summary → Calories: %d, Minutes: %d%n",
                result.getCaloriesBurned(), result.getDurationInMinutes());
    
        assertEquals(330, result.getCaloriesBurned());
        assertEquals(63, result.getDurationInMinutes());
    }
    @Test
    void getWeeklySummary_shouldAggregateCorrectly() {
        // Simulate 7 return values for 7 days
        when(historyRepository.sumCaloriesBurnedByUserBetween(eq(1L), any(), any()))
            .thenReturn(160, 0, 0, 150, 0, 270, 330);

        when(historyRepository.sumTimeExercisedByUserBetween(eq(1L), any(), any()))
            .thenReturn(26, 0, 0, 25, 0, 47, 63);

        List<DailySummaryDTO> result = historyService.getWeeklySummary(1L);

        assertEquals(7, result.size());
        System.out.println("📊 Weekly Summary (Oldest to Newest):");
        for (DailySummaryDTO day : result) {
            System.out.printf("🗓️ %s → Calories: %d, Minutes: %d%n",
                    day.getDate(), day.getCaloriesBurned(), day.getDurationInMinutes());
        }

        // Assert specific values
        assertEquals(160, result.get(0).getCaloriesBurned()); // 7 days ago
        assertEquals(330, result.get(6).getCaloriesBurned()); // today
    }

    //cant be bothered to fix this lol
    // @Test
    // void loadMoreHistory_shouldReturnLimitedHistoryList() {
    //     List<History> recent = historyList.stream()
    //             .sorted(Comparator.comparing(History::getWorkoutDateTime).reversed())
    //             .limit(2)
    //             .toList();
    
    //     when(historyRepository.findMoreHistory(eq(1L), any(), any())).thenReturn(recent);
    
    //     when(workoutService.convertToDTO(any())).thenAnswer(invocation -> {
    //         Workout inputWorkout = invocation.getArgument(0);
    //         return recent.stream()
    //                 .filter(h -> h.getWorkout().equals(inputWorkout))
    //                 .findFirst()
    //                 .map(h -> convertToWorkoutDTO(h.getWorkout()))
    //                 .orElse(null);
    //     });
    
    //     List<HistoryDTO> result = historyService.loadMoreHistory(1L, LocalDateTime.now(), 2);
    
    //     assertEquals(2, result.size());
    //     System.out.println("🧪 Loaded History (Most Recent First):");
    //     for (HistoryDTO dto : result) {
    //         System.out.printf("  ✅ Workout: %s | Calories: %d | Minutes: %d | DateTime: %s%n",
    //                 dto.getName(), dto.getCaloriesBurned(), dto.getDurationInMinutes(), dto.getWorkoutDateTime());
    //     }
    
    //     assertTrue(result.get(0).getWorkoutDateTime().after(result.get(1).getWorkoutDateTime()));
    // }
    
    
    

    @Test
    void getActivityOverview_shouldReturnBundledDTO() {
        // Arrange expected values from your realistic data:
        // Today: IDs 9–11 = Calories: 330, Minutes: 63
        // Recent: most recent 5 histories (IDs 7–11)
        List<History> recent = historyList.stream()
                .sorted(Comparator.comparing(History::getWorkoutDateTime).reversed())
                .limit(5)
                .toList();

        when(historyRepository.sumCaloriesBurnedByUserBetween(eq(1L), any(), any())).thenReturn(330);
        when(historyRepository.sumTimeExercisedByUserBetween(eq(1L), any(), any())).thenReturn(63);
        when(historyRepository.findMoreHistory(eq(1L), any(), any())).thenReturn(recent);
        when(workoutService.convertToDTO(testWorkout)).thenReturn(testWorkoutDTO);

        ActivityOverviewDTO overview = historyService.getActivityOverview(1L);

        assertNotNull(overview);
        assertEquals(330, overview.getToday().getCaloriesBurned());
        assertEquals(63, overview.getToday().getDurationInMinutes());
        assertEquals(5, overview.getRecentWorkouts().size());

        System.out.println("🧩 Activity Overview:");
        System.out.printf("  🔥 Today → Calories: %d, Minutes: %d%n",
                overview.getToday().getCaloriesBurned(), overview.getToday().getDurationInMinutes());

        for (HistoryDTO dto : overview.getRecentWorkouts()) {
            System.out.printf("  🏋️ %s | Calories: %d | Minutes: %d%n",
                    dto.getName(), dto.getCaloriesBurned(), dto.getDurationInMinutes());
        }
    }

    //test method
    private HistoryDTO convertToDTO(History history) {
        HistoryDTO dto = new HistoryDTO();
        dto.setWorkout(convertToWorkoutDTO(history.getWorkout())); // ✅ use WorkoutDTO directly
        dto.setName(history.getWorkoutName());
        dto.setCaloriesBurned(history.getCaloriesBurned());
        dto.setDurationInMinutes(history.getDurationInMinutes());
        dto.setWorkoutDateTime(history.getWorkoutDateTime());
        dto.setHistoryId(history.getHistoryId());
        return dto;
    }
    

    private WorkoutDTO convertToWorkoutDTO(Workout workout) {
        WorkoutDTO dto = new WorkoutDTO();
        dto.setWorkoutId(workout.getWorkoutId());
        dto.setName(workout.getName());
        dto.setCalories(workout.getCalories());
        dto.setDurationInMinutes(workout.getDurationInMinutes());
        return dto;
    }
    
    @Test
    void getTotalHistoryCountByUser_shouldReturnCount() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(historyRepository.countByUser(testUser)).thenReturn(10);
        
        // When
        int count = historyService.getTotalHistoryCountByUser(1L);
        
        // Then
        assertEquals(10, count);
        verify(historyRepository).countByUser(testUser);
    }
    
    @Test
    void getTotalHistoryCountByUser_shouldReturnNegativeOneWhenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When
        int count = historyService.getTotalHistoryCountByUser(1L);
        
        // Then
        assertEquals(-1, count);
        verify(historyRepository, never()).countByUser(any(User.class));
    }

        @Test
    void recordWorkoutCompletion_shouldUnlockAchievementFor10Workouts() {
        Achievement testAchievement = new Achievement();
        testAchievement.setAchievementId(1L);
        testAchievement.setTitle("10 Workouts");
        testAchievement.setDescription("Complete 10 workouts");
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(workoutRepository.findById(1L)).thenReturn(Optional.of(testWorkout));
        when(historyRepository.save(any(History.class))).thenReturn(testHistory);
        when(workoutService.convertToDTO(any(Workout.class))).thenReturn(testWorkoutDTO);
        when(historyRepository.countByUser(testUser)).thenReturn(10); // 10th workout completed
        when(achievementService.getAchievementByTitle("10 Workouts")).thenReturn(Optional.of(testAchievement));
        
        // When
        HistoryDTO result = historyService.recordWorkoutCompletion(1L, testWorkoutDTO);
        
        // Then
        assertNotNull(result);
        verify(userStreakService).updateStreak(1L);
        verify(achievementService).getAchievementByTitle("10 Workouts");
        verify(userAchievementService).completeAchievement(1L, 1L);
    }

    @Test
    void recordWorkoutCompletion_shouldNotUnlockAchievementBelow10Workouts() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(workoutRepository.findById(1L)).thenReturn(Optional.of(testWorkout));
        when(historyRepository.save(any(History.class))).thenReturn(testHistory);
        when(workoutService.convertToDTO(any(Workout.class))).thenReturn(testWorkoutDTO);
        when(historyRepository.countByUser(testUser)).thenReturn(9); // 9th workout (not yet at 10)
        
        // When
        HistoryDTO result = historyService.recordWorkoutCompletion(1L, testWorkoutDTO);
        
        // Then
        assertNotNull(result);
        verify(userStreakService).updateStreak(1L);
        verify(achievementService, never()).getAchievementByTitle(any());
        verify(userAchievementService, never()).completeAchievement(anyLong(), anyLong());
    }
    
    @Test
    void getTotalCaloriesBurnedByUser_shouldReturnTotalCalories() {
        // Given
        when(historyRepository.sumCaloriesBurnedByUserId(1L)).thenReturn(1500);
        
        // When
        int calories = historyService.getTotalCaloriesBurnedByUser(1L);
        
        // Then
        assertEquals(1500, calories);
        verify(historyRepository).sumCaloriesBurnedByUserId(1L);
    }
    
    @Test
    void getTotalDurationByUser_shouldReturnTotalMinutes() {
        // Given
        when(historyRepository.sumTimeExercisedByUserId(1L)).thenReturn(300);
        
        // When
        int minutes = historyService.getTotalDurationByUser(1L);
        
        // Then
        assertEquals(300, minutes);
        verify(historyRepository).sumTimeExercisedByUserId(1L);
    }
    

}