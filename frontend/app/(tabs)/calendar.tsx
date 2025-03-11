import React from "react";
import { View, Text, TouchableOpacity, StyleSheet } from "react-native";
import { Calendar, CalendarProps } from "react-native-calendars";
import { Colors } from "../theme"; // Import your color theme

const CalendarScreen: React.FC = () => {
  // Example data for the ladies' cycle period and exercise streak
  const ladiesCyclePeriod = "Oct 1 - Oct 7";
  const exerciseStreak = "5 days";

  return (
    <View style={styles.container}>
      {/* Calendar */}
      <Calendar
        style={styles.calendar}
        theme={{
          backgroundColor: Colors.background,
          calendarBackground: Colors.background,
          textSectionTitleColor: Colors.text,
          selectedDayBackgroundColor: Colors.primary,
          selectedDayTextColor: Colors.secondary,
          todayTextColor: Colors.primary,
          dayTextColor: Colors.text,
          arrowColor: Colors.primary,
        }}
        markedDates={{
          "2025-03-01": { startingDay: true, color: Colors.primary },
          "2025-03-02": { color: Colors.primary },
          "2025-03-03": { color: Colors.primary },
          "2025-03-04": { color: Colors.primary },
          "2025-03-05": { color: Colors.primary },
          "2025-03-06": { color: Colors.primary },
          "2025-03-07": { endingDay: true, color: Colors.primary },
        }}
      />
  
      {/* Edit Button */}
      <TouchableOpacity style={styles.editButton}>
        <Text style={styles.editButtonText}>Edit</Text>
      </TouchableOpacity>
  
      {/* Two Small Containers */}
      <View style={styles.rowContainer}>
        {/* Ladies' Cycle Period */}
        <View style={styles.infoContainer}>
          <Text style={styles.infoTitle}>Cycle Period</Text>
          <Text style={styles.infoText}>{ladiesCyclePeriod}</Text>
        </View>
  
        {/* Exercise Streak */}
        <View style={styles.infoContainer}>
          <Text style={styles.infoTitle}>Exercise Streak</Text>
          <Text style={styles.infoText}>{exerciseStreak}</Text>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.background,
    padding: 16,
  },
  calendar: {
    borderRadius: 10,
    elevation: 4, // Shadow for Android
    shadowColor: "#000", // Shadow for iOS
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
  },
  editButton: {
    marginTop: 16,
    backgroundColor: Colors.primary,
    paddingVertical: 12,
    borderRadius: 8,
    alignItems: "center",
  },
  editButtonText: {
    color: Colors.secondary,
    fontSize: 16,
    fontWeight: "bold",
  },
  rowContainer: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginTop: 24,
  },
  infoContainer: {
    flex: 1,
    backgroundColor: Colors.secondary,
    padding: 16,
    borderRadius: 8,
    marginHorizontal: 8,
    alignItems: "center",
    elevation: 2, // Shadow for Android
    shadowColor: "#000", // Shadow for iOS
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
  },
  infoTitle: {
    color: Colors.text,
    fontSize: 14,
    fontWeight: "bold",
    marginBottom: 8,
  },
  infoText: {
    color: Colors.text,
    fontSize: 16,
  },
});

export default CalendarScreen;