import { promises as fs } from "fs"
import path from "path"

// For server components
export async function getData() {
  try {
    // Use ES modules syntax with fs/promises
    const dataPath = path.join(process.cwd(), "public", "data.json")
    const jsonData = await fs.readFile(dataPath, "utf8")
    return JSON.parse(jsonData)
  } catch (error) {
    console.error("Error reading data file:", error)
    // Return a default data structure to prevent crashes
    return {
      user: {
        name: "User",
        email: "user@example.com",
        height: "165",
        weight: "58",
        birthdate: "1990-05-15",
        goal: "Stay fit",
        workoutsPerWeek: "5 days",
        streak: 0,
        todayCalories: 0,
        todayMinutes: 0,
        cyclePhase: "Unknown",
      },
      recommendations: {
        title: "Workout",
        description: "Start your fitness journey",
        icon: "heart",
      },
      workoutCategories: [],
      workouts: {},
      activities: [],
      weeklyProgress: [],
      cycleData: {},
      streakDays: [],
      achievements: [],
    }
  }
}

// For client components
export async function getClientData() {
  try {
    // In client components, we need to use the full URL
    const res = await fetch("/data.json", {
      // Add cache: 'no-store' to prevent caching issues during development
      cache: "no-store",
    })

    if (!res.ok) {
      throw new Error("Failed to fetch data")
    }

    return res.json()
  } catch (error) {
    console.error("Error fetching client data:", error)
    // Return a default data structure to prevent crashes
    return {
      user: {
        name: "User",
        email: "user@example.com",
        height: "165",
        weight: "58",
        birthdate: "1990-05-15",
        goal: "Stay fit",
        workoutsPerWeek: "5 days",
        streak: 0,
        todayCalories: 0,
        todayMinutes: 0,
        cyclePhase: "Unknown",
      },
      recommendations: {
        title: "Workout",
        description: "Start your fitness journey",
        icon: "heart",
      },
      workoutCategories: [],
      workouts: {},
      activities: [],
      weeklyProgress: [],
      cycleData: {},
      streakDays: [],
      achievements: [],
    }
  }
}

// For getting a specific workout
export async function getWorkout(id: string) {
  try {
    // Use the appropriate function based on environment
    const data = typeof window === "undefined" ? await getData() : await getClientData()

    // Search through all workout categories
    for (const category in data.workouts) {
      const workout = data.workouts[category].find((w: any) => w.id === id)
      if (workout) {
        return workout
      }
    }

    return null
  } catch (error) {
    console.error("Error fetching workout:", error)
    return null
  }
}

