// This is a simple data module that works in both client and server components

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
  cycleData: {
    periodStart: "2025-03-05",
    periodEnd: "2025-03-10",
    nextPeriodStart: "2025-04-02",
    cycleLength: 28,
    periodLength: 5,
  },
  streakDays: [],
  achievements: [],
}

// For server components
export async function getData() {
  try {
    if (typeof window === "undefined") {
      // Server-side: use the API route
      const baseUrl = process.env.VERCEL_URL ? `https://${process.env.VERCEL_URL}` : "http://localhost:3000"

      const res = await fetch(`${baseUrl}/api/data`, {
        cache: "no-store",
      })

      if (!res.ok) {
        throw new Error(`Failed to fetch data: ${res.status}`)
      }

      const data = await res.json()

      // Ensure cycleData exists
      if (!data.cycleData) {
        data.cycleData = defaultData.cycleData
      }

      return data
    } else {
      // Client-side: use fetch
      return fetchData()
    }
  } catch (error) {
    console.error("Error getting data:", error)
    return defaultData
  }
}

// For client components
export async function fetchData() {
  try {
    const res = await fetch("/api/data", {
      cache: "no-store",
    })

    if (!res.ok) {
      throw new Error(`Failed to fetch data: ${res.status}`)
    }

    const data = await res.json()

    // Ensure cycleData exists
    if (!data.cycleData) {
      data.cycleData = defaultData.cycleData
    }

    return data
  } catch (error) {
    console.error("Error fetching client data:", error)
    return defaultData
  }
}

// For getting a specific workout
export async function getWorkout(id: string) {
  try {
    // Use the appropriate function based on environment
    const data = typeof window === "undefined" ? await getData() : await fetchData()

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

