import { getDataFromFileSystem } from "./server-fs"

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

// For server components
export async function getServerData() {
  if (typeof window === "undefined") {
    // We're on the server, use file system
    return getDataFromFileSystem()
  } else {
    // We're somehow in a client component that's calling this
    return fetchData()
  }
}

// For client components
export async function fetchData() {
  try {
    const res = await fetch("/data.json", {
      cache: "no-store",
      headers: {
        "Content-Type": "application/json",
      },
    })

    if (!res.ok) {
      throw new Error(`Failed to fetch data: ${res.status}`)
    }

    return res.json()
  } catch (error) {
    console.error("Error fetching client data:", error)
    return defaultData
  }
}

// For getting a specific workout
export async function getWorkout(id: string) {
  try {
    // Use the appropriate function based on environment
    const data = typeof window === "undefined" ? await getServerData() : await fetchData()

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

