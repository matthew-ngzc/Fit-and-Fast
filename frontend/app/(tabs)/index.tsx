import React, { useState } from "react";
import {
  View,
  Text,
  TouchableOpacity,
  FlatList,
  StyleSheet,
  ScrollView,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { Colors } from "../theme"; // Import your color theme

interface Exercise {
  id: string;
  name: string;
  level: string;
}

interface RecommendedWorkout {
  id: string;
  name: string;
  routines: string[];
}

interface ExerciseItem {
  id: string;
  name: string;
  level: string;
}

// Dummy data for exercises
const exercises = {
  All: [
    {
      id: "1",
      name: "Morning Stretch",
      category: "Warm Up",
      level: "Beginner",
    },
    {
      id: "2",
      name: "Sun Salutation",
      category: "Yoga",
      level: "Intermediate",
    },
    { id: "3", name: "Bicep Curls", category: "Biceps", level: "Advanced" },
    {
      id: "4",
      name: "Pelvic Floor Exercises",
      category: "Pre-Pregnancy",
      level: "Beginner",
    },
    {
      id: "5",
      name: "Postpartum Core Workout",
      category: "Post-Pregnancy",
      level: "Intermediate",
    },
    {
      id: "6",
      name: "Hormonal Balance Yoga",
      category: "Others",
      level: "Advanced",
    },
  ],
  "Warm Up": [
    { id: "1", name: "Morning Stretch", level: "Beginner" },
    { id: "7", name: "Dynamic Warm-Up", level: "Intermediate" },
  ],
  Yoga: [
    { id: "2", name: "Sun Salutation", level: "Intermediate" },
    { id: "8", name: "Restorative Yoga", level: "Beginner" },
  ],
  Biceps: [
    { id: "3", name: "Bicep Curls", level: "Advanced" },
    { id: "9", name: "Hammer Curls", level: "Intermediate" },
  ],
  "Pre-Pregnancy": [
    { id: "4", name: "Pelvic Floor Exercises", level: "Beginner" },
    { id: "10", name: "Low-Impact Cardio", level: "Intermediate" },
  ],
  "Post-Pregnancy": [
    { id: "5", name: "Postpartum Core Workout", level: "Intermediate" },
    { id: "11", name: "Gentle Stretching", level: "Beginner" },
  ],
  Others: [
    { id: "6", name: "Hormonal Balance Yoga", level: "Advanced" },
    { id: "12", name: "Menstrual Relief Stretches", level: "Beginner" },
  ],
};

const recommendedWorkouts = [
  {
    id: "1",
    name: "Lose Weight",
    routines: ["Beginner"],
  },
  {
    id: "2",
    name: "Build Strength",
    routines: ["Intermediate"],
  },
];

const HomeScreen: React.FC = () => {
  const [selectedCategory, setSelectedCategory] = useState("All");

  // Render exercise item
  const renderExerciseItem = ({ item }: { item: ExerciseItem }) => (
    <TouchableOpacity style={styles.exerciseItem}>
      <View style={styles.exerciseDetails}>
        <Text style={styles.exerciseName}>{item.name}</Text>
        <Text style={styles.exerciseLevel}>{item.level}</Text>
      </View>
      <Ionicons name="chevron-forward" size={24} color={Colors.text} />
    </TouchableOpacity>
  );

  const renderRecommendedWorkout = ({ item }: { item: RecommendedWorkout }) => (
    <View style={styles.recommendedWorkout}>
      <Text style={styles.recommendedWorkoutTitle}>{item.name}</Text>
      <View style={styles.routineContainer}>
        {item.routines.map((routine, index) => (
          <TouchableOpacity key={index} style={styles.routineButton}>
            <Text style={styles.routineText}>{routine}</Text>
          </TouchableOpacity>
        ))}
      </View>
    </View>
  );

  const userGoals = [
    "Lose Weight",
    "Build Muscle",
    "Increase Stamina",
    "Improve Flexibility",
  ];

  const renderUserGoal = ({ item }: { item: string }) => (
    <TouchableOpacity style={styles.userGoalButton}>
      <Text style={styles.userGoalText}>{item}</Text>
    </TouchableOpacity>
  );

  const handleEditGoals = () => {
    console.log("Edit goals clicked!");
    // You can open a modal, navigate to another screen, or let users pick new goals
  };
  
  return (
    <ScrollView style={styles.container}>
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Welcome Back, Jane!</Text>
        <Text style={styles.headerSubtitle}>
          "Your journey to a healthier you starts today."
        </Text>
      </View>

      {/* User Goals Section */}
      <View style={styles.userGoalsHeader}>
        <Text style={styles.sectionTitle}>Your Goals</Text>
        <TouchableOpacity onPress={handleEditGoals}>
          <Ionicons name="pencil-outline" size={20} color={Colors.text} />
        </TouchableOpacity>
      </View>

      <FlatList
        data={userGoals}
        renderItem={renderUserGoal}
        keyExtractor={(item) => item}
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.userGoalsContainer}
      />

      {/* Recommended Workouts of the Day */}
      <Text style={styles.sectionTitle}>Recommended Workouts</Text>
      <FlatList
        data={recommendedWorkouts}
        renderItem={renderRecommendedWorkout}
        keyExtractor={(item) => item.id}
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.recommendedWorkoutsContainer}
      />

      {/* Exercise Categories */}
      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        style={styles.categoryContainer}
      >
        {[
          "All",
          "Warm Up",
          "Yoga",
          "Biceps",
          "Pre-Pregnancy",
          "Post-Pregnancy",
          "Others",
        ].map((category) => (
          <TouchableOpacity
            key={category}
            style={[
              styles.categoryButton,
              selectedCategory === category && styles.selectedCategoryButton,
            ]}
            onPress={() => setSelectedCategory(category)}
          >
            <Text
              style={[
                styles.categoryText,
                selectedCategory === category && styles.selectedCategoryText,
              ]}
            >
              {category}
            </Text>
          </TouchableOpacity>
        ))}
      </ScrollView>

      {/* All Exercises */}
      <FlatList<ExerciseItem>
        data={exercises[selectedCategory]}
        renderItem={renderExerciseItem}
        keyExtractor={(item) => item.id}
        scrollEnabled={false}
        contentContainerStyle={styles.exerciseList}
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
  header: {
    marginBottom: 24,
  },
  headerTitle: {
    fontSize: 24,
    fontWeight: "bold",
    color: Colors.text,
  },
  headerSubtitle: {
    fontSize: 14,
    color: Colors.inactive,
    marginTop: 4,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: "bold",
    color: Colors.text,
    marginBottom: 16,
  },
  recommendedWorkoutsContainer: {
    paddingBottom: 16,
  },
  recommendedWorkout: {
    width: 200,
    backgroundColor: Colors.secondary,
    borderRadius: 8,
    padding: 16,
    marginRight: 16,
    elevation: 2,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
  },
  recommendedWorkoutTitle: {
    fontSize: 16,
    fontWeight: "bold",
    color: Colors.text,
    marginBottom: 8,
  },
  routineContainer: {
    flexDirection: "row",
    flexWrap: "wrap",
  },
  routineButton: {
    backgroundColor: Colors.primary,
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 4,
    marginRight: 8,
    marginBottom: 8,
  },
  routineText: {
    fontSize: 12,
    color: Colors.secondary,
  },
  categoryContainer: {
    marginBottom: 24,
  },
  categoryButton: {
    paddingHorizontal: 16,
    paddingVertical: 8,
    borderRadius: 20,
    backgroundColor: Colors.secondary,
    marginRight: 8,
  },
  selectedCategoryButton: {
    backgroundColor: Colors.primary,
  },
  categoryText: {
    fontSize: 14,
    color: Colors.text,
  },
  selectedCategoryText: {
    color: Colors.secondary,
    fontWeight: "bold",
  },
  exerciseList: {
    paddingBottom: 16,
  },
  exerciseItem: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
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
  exerciseDetails: {
    flex: 1,
  },
  exerciseName: {
    fontSize: 16,
    color: Colors.text,
  },
  exerciseLevel: {
    fontSize: 12,
    color: Colors.inactive,
    marginTop: 4,
  },
  userGoalsContainer: {
    flexDirection: "row",
    paddingBottom: 16,
  },
  userGoalButton: {
    paddingHorizontal: 16,
    paddingVertical: 8,
    borderRadius: 20,
    backgroundColor: Colors.primary,
    marginRight: 8,
  },
  userGoalText: {
    fontSize: 14,
    color: Colors.secondary,
    fontWeight: "bold",
  },
  userGoalsHeader: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    marginBottom: 8,
  },
});

export default HomeScreen;
