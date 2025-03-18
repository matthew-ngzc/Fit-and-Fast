// This is an alternative approach for server components that doesn't rely on file system access

export async function getServerData() {
  try {
    // Use the public URL path for server components too
    const res = await fetch(new URL("/data.json", process.env.VERCEL_URL || "http://localhost:3000"), {
      cache: "no-store",
    })

    if (!res.ok) {
      throw new Error("Failed to fetch data")
    }

    return res.json()
  } catch (error) {
    console.error("Error fetching server data:", error)
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

