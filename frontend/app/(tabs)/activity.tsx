import React from "react";
import { View, Text, FlatList, StyleSheet, ScrollView } from "react-native";
import { Colors } from "../theme"; // Import your color theme

// Dummy data for workout statistics
const workoutStatistics = {
  totalWorkouts: 25,
  totalCaloriesBurned: 1200,
  totalWorkoutTime: "15h 30m",
  currentStreak: "5 days",
};

// Dummy data for workout logs
const workoutLogs = [
  { id: "1", date: "2023-10-01", type: "Yoga", duration: "30m", caloriesBurned: 150 },
  { id: "2", date: "2023-10-02", type: "Cardio", duration: "45m", caloriesBurned: 300 },
  { id: "3", date: "2023-10-03", type: "Strength", duration: "1h", caloriesBurned: 450 },
  { id: "4", date: "2023-10-04", type: "Yoga", duration: "30m", caloriesBurned: 150 },
  { id: "5", date: "2023-10-05", type: "Cardio", duration: "45m", caloriesBurned: 300 },
];

const ActivityScreen: React.FC = () => {
  // Render workout log item
  const renderWorkoutLog = ({ item }) => (
    <View style={styles.workoutLogItem}>
      <Text style={styles.workoutLogDate}>{item.date}</Text>
      <View style={styles.workoutLogDetails}>
        <Text style={styles.workoutLogType}>{item.type}</Text>
        <Text style={styles.workoutLogDuration}>{item.duration}</Text>
        <Text style={styles.workoutLogCalories}>{item.caloriesBurned} kcal</Text>
      </View>
    </View>
  );

  return (
    <ScrollView style={styles.container}>
      {/* Workout Statistics */}
      <Text style={styles.sectionTitle}>Workout Statistics</Text>
      <View style={styles.statsContainer}>
        <View style={styles.statItem}>
          <Text style={styles.statValue}>{workoutStatistics.totalWorkouts}</Text>
          <Text style={styles.statLabel}>Total Workouts</Text>
        </View>
        <View style={styles.statItem}>
          <Text style={styles.statValue}>{workoutStatistics.totalCaloriesBurned}</Text>
          <Text style={styles.statLabel}>Calories Burned</Text>
        </View>
        <View style={styles.statItem}>
          <Text style={styles.statValue}>{workoutStatistics.totalWorkoutTime}</Text>
          <Text style={styles.statLabel}>Total Time</Text>
        </View>
        <View style={styles.statItem}>
          <Text style={styles.statValue}>{workoutStatistics.currentStreak}</Text>
          <Text style={styles.statLabel}>Current Streak</Text>
        </View>
      </View>

      {/* Workout Logs */}
      <Text style={styles.sectionTitle}>Workout Logs</Text>
      <FlatList
        data={workoutLogs}
        renderItem={renderWorkoutLog}
        keyExtractor={(item) => item.id}
        scrollEnabled={false}
        contentContainerStyle={styles.workoutLogsContainer}
      />
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.background,
    padding: 16,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: "bold",
    color: Colors.text,
    marginBottom: 16,
  },
  statsContainer: {
    flexDirection: "row",
    flexWrap: "wrap",
    justifyContent: "space-between",
    marginBottom: 24,
  },
  statItem: {
    width: "48%", // Two items per row
    backgroundColor: Colors.secondary,
    padding: 16,
    borderRadius: 8,
    marginBottom: 16,
    alignItems: "center",
    elevation: 2,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
  },
  statValue: {
    fontSize: 20,
    fontWeight: "bold",
    color: Colors.primary,
  },
  statLabel: {
    fontSize: 14,
    color: Colors.text,
    marginTop: 4,
  },
  workoutLogsContainer: {
    paddingBottom: 16,
  },
  workoutLogItem: {
    backgroundColor: Colors.secondary,
    padding: 16,
    borderRadius: 8,
    marginBottom: 8,
    elevation: 2,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
  },
  workoutLogDate: {
    fontSize: 14,
    color: Colors.inactive,
    marginBottom: 8,
  },
  workoutLogDetails: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },
  workoutLogType: {
    fontSize: 16,
    fontWeight: "bold",
    color: Colors.text,
  },
  workoutLogDuration: {
    fontSize: 14,
    color: Colors.inactive,
  },
  workoutLogCalories: {
    fontSize: 14,
    color: Colors.primary,
  },
});

export default ActivityScreen;