import { promises as fs } from "fs"
import path from "path"

// Default data to use as fallback
const defaultData = {
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

// For server components using the file system
export async function getDataFromFileSystem() {
  try {
    const filePath = path.join(process.cwd(), "public", "data.json")
    const jsonData = await fs.readFile(filePath, "utf8")
    return JSON.parse(jsonData)
  } catch (error) {
    console.error("Error reading data file:", error)
    return defaultData
  }
}

