// lib/data.ts
import workoutsData from '../app/data.json';

export function getWorkoutData() {
  // Define the workout categories
  const workoutCategories = [
    { id: "yoga", name: "Yoga", icon: "yoga" },
    { id: "hiit", name: "HIIT", icon: "zap" },
    { id: "strength", name: "Strength", icon: "dumbbell" },
    { id: "prenatal", name: "Prenatal", icon: "baby" },
    { id: "postnatal", name: "Postnatal", icon: "baby" },
    { id: "others", name: "Others", icon: "sparkles" }
  ];
  
  // Organize workouts by category
  const workouts = workoutsData.reduce((acc, workout) => {
    // Convert category to lowercase for consistency
    const category = workout.category.toLowerCase();
    
    // If this category doesn't exist in the accumulator yet, initialize it
    if (!acc[category]) {
      acc[category] = [];
    }
    
    // Add the workout to its category
    acc[category].push(workout);
    
    return acc;
  }, {});
  
  return { workoutCategories, workouts };
}