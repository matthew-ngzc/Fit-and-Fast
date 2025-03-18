"use client"

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import {
  DumbbellIcon,
  FlameIcon,
  HeartIcon,
  TrendingUpIcon,
  SpaceIcon as Yoga,
  Zap,
  Baby,
  Sparkles,
} from "lucide-react"
import { WorkoutCard } from "@/components/workout-card"
import { getData } from "@/lib/data-module"

export default function HomePage() {
  const [data, setData] = useState<any>(null)
  const [selectedTab, setSelectedTab] = useState<string>("yoga")
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    async function loadData() {
      try {
        const fetchedData = await getData()
        setData(fetchedData)
      } catch (error) {
        console.error("Failed to load data:", error)
      } finally {
        setLoading(false)
      }
    }

    // Get the saved tab from localStorage
    if (typeof window !== "undefined") {
      const savedTab = localStorage.getItem("selectedWorkoutTab")
      if (savedTab) {
        setSelectedTab(savedTab)
      }
    }

    loadData()
  }, [])

  const saveSelectedTab = (value: string) => {
    setSelectedTab(value)
    if (typeof window !== "undefined") {
      localStorage.setItem("selectedWorkoutTab", value)
    }
  }

  if (loading || !data) {
    return (
      <div className="container px-4 py-6 md:py-10 pb-20 max-w-5xl mx-auto flex items-center justify-center min-h-[50vh]">
        <p>Loading...</p>
      </div>
    )
  }

  const { user, recommendations, workoutCategories, workouts } = data

  return (
    <div className="container px-4 py-6 md:py-10 pb-20 max-w-5xl mx-auto">
      <div className="flex flex-col gap-6">
        <section className="space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-bold tracking-tight">Welcome back, {user.name.split(" ")[0]}!</h1>
              <p className="text-muted-foreground">Ready for your 7-minute workout today?</p>
            </div>
            <Button size="sm" className="hidden md:flex">
              View All
            </Button>
          </div>

          <Card>
            <CardHeader className="pb-2">
              <CardTitle>Today's Recommendation</CardTitle>
              <CardDescription>Based on your cycle and preferences</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="bg-muted/50 rounded-lg p-4 flex flex-col md:flex-row gap-4 items-center">
                <div className="bg-pink-100 rounded-full p-3">
                  <HeartIcon className="h-8 w-8 text-primary" />
                </div>
                <div className="flex-1 text-center md:text-left">
                  <h3 className="font-semibold text-lg">{recommendations.title}</h3>
                  <p className="text-muted-foreground text-sm">{recommendations.description}</p>
                </div>
                <Button>Start Workout</Button>
              </div>
            </CardContent>
          </Card>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <Card className="bg-pink-100 border-none">
              <CardContent className="p-4 flex flex-col items-center justify-center text-center">
                <FlameIcon className="h-6 w-6 text-primary mb-2" />
                <p className="text-sm font-medium">{user.todayCalories}</p>
                <p className="text-xs text-muted-foreground">Calories</p>
              </CardContent>
            </Card>
            <Card className="bg-pink-100 border-none">
              <CardContent className="p-4 flex flex-col items-center justify-center text-center">
                <DumbbellIcon className="h-6 w-6 text-primary mb-2" />
                <p className="text-sm font-medium">{user.todayMinutes}</p>
                <p className="text-xs text-muted-foreground">Minutes</p>
              </CardContent>
            </Card>
            <Card className="bg-pink-100 border-none">
              <CardContent className="p-4 flex flex-col items-center justify-center text-center">
                <TrendingUpIcon className="h-6 w-6 text-primary mb-2" />
                <p className="text-sm font-medium">{user.streak}</p>
                <p className="text-xs text-muted-foreground">Day Streak</p>
              </CardContent>
            </Card>
            <Card className="bg-pink-100 border-none">
              <CardContent className="p-4 flex flex-col items-center justify-center text-center">
                <HeartIcon className="h-6 w-6 text-primary mb-2" />
                <p className="text-sm font-medium">{user.cyclePhase}</p>
                <p className="text-xs text-muted-foreground">Cycle Phase</p>
              </CardContent>
            </Card>
          </div>
        </section>

        <section className="space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-semibold">Workout Categories</h2>
          </div>

          <Tabs value={selectedTab} onValueChange={saveSelectedTab} className="w-full">
            <div className="bg-pink-100/50 p-2 rounded-lg mb-4">
              <TabsList className="grid grid-cols-3 md:grid-cols-6 h-auto bg-white/80 p-1 rounded-md">
                {workoutCategories.map((category) => (
                  <TabsTrigger
                    key={category.id}
                    value={category.id}
                    className="py-3 data-[state=active]:bg-pink-100 data-[state=active]:text-primary"
                  >
                    <div className="flex flex-col items-center gap-1">
                      {getIconComponent(category.icon)}
                      <span className="text-xs">{category.name}</span>
                    </div>
                  </TabsTrigger>
                ))}
              </TabsList>
            </div>

            {workoutCategories.map((category) => (
              <TabsContent key={category.id} value={category.id} className="mt-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {workouts[category.id]?.map((workout) => (
                    <WorkoutCard
                      key={workout.id}
                      title={workout.title}
                      duration={workout.duration}
                      level={workout.level}
                      calories={workout.calories}
                      image={workout.image}
                      href={`/workout/${workout.id}`}
                    />
                  ))}
                </div>
              </TabsContent>
            ))}
          </Tabs>
        </section>
      </div>
    </div>
  )
}

// Helper function to get the icon component based on the icon name
function getIconComponent(iconName: string) {
  switch (iconName) {
    case "yoga":
      return <Yoga className="h-5 w-5" />
    case "zap":
      return <Zap className="h-5 w-5" />
    case "dumbbell":
      return <DumbbellIcon className="h-5 w-5" />
    case "heart":
      return <HeartIcon className="h-5 w-5" />
    case "baby":
      return <Baby className="h-5 w-5" />
    case "sparkles":
      return <Sparkles className="h-5 w-5" />
    default:
      return <DumbbellIcon className="h-5 w-5" />
  }
}

