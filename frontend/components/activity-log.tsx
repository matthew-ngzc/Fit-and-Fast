"use client"

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Badge } from "@/components/ui/badge"
import { CalendarIcon, ClockIcon, FlameIcon } from "lucide-react"
import { useEffect, useState } from "react"

type Activity = {
  id: number
  title: string
  date: string
  duration: string
  calories: number
  category: string
}

const fakeActivities: Activity[] = [
  {
    id: 1,
    title: "Core Strength",
    date: "Today, 9:30 AM",
    duration: "7 min",
    calories: 90,
    category: "Strength",
  },
  {
    id: 2,
    title: "Full Body Tone",
    date: "Yesterday, 6:15 PM",
    duration: "7 min",
    calories: 120,
    category: "Cardio",
  },
  {
    id: 3,
    title: "Low Impact Cardio",
    date: "Mar 13, 8:00 AM",
    duration: "7 min",
    calories: 100,
    category: "Cardio",
  },
  {
    id: 4,
    title: "Stress Relief",
    date: "Mar 12, 7:45 PM",
    duration: "7 min",
    calories: 80,
    category: "Yoga",
  },
]

export function ActivityLog() {
  const [activities, setActivities] = useState<Activity[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    // Simulate loading of activity data
    setTimeout(() => {
      setActivities(fakeActivities) // Set the fake data
      setLoading(false)
    }, ) // Simulate a loading delay
  }, [])

  // api call to get workout logs
  // useEffect(() => {
  //   // API call to fetch workout logs
  //   const fetchActivities = async () => {
  //     try {
  //       setLoading(true)
  //       const response = await fetch("https://api.example.com/workout-logs") // Replace with your API URL
  //       if (!response.ok) {
  //         throw new Error("Failed to fetch activities")
  //       }
  //       const data = await response.json()
  //       setActivities(data) // Assuming the response is an array of activities
  //       setLoading(false)
  //     } catch (err) {
  //       setError("Failed to load activities")
  //       setLoading(false)
  //     }
  //   }

  //   fetchActivities()
  // }, [])

  if (loading) {
    return <div className="py-4 text-center">Loading activities...</div>
  }
  

  return (
    <div className="space-y-4">
      {activities.map((activity: any) => (
        <div key={activity.id} className="flex items-start gap-4 p-4 border rounded-lg">
          <Avatar className="h-10 w-10">
            <AvatarImage src="/placeholder.svg?height=40&width=40" alt={activity.title} />
            <AvatarFallback>WO</AvatarFallback>
          </Avatar>
          <div className="flex-1 space-y-1">
            <div className="flex items-center justify-between">
              <h4 className="font-medium">{activity.title}</h4>
              <Badge variant="outline">{activity.category}</Badge>
            </div>
            <div className="flex items-center text-sm text-muted-foreground">
              <CalendarIcon className="mr-1 h-3 w-3" />
              <span>{activity.date}</span>
            </div>
            <div className="flex items-center gap-4 text-sm text-muted-foreground">
              <div className="flex items-center">
                <ClockIcon className="mr-1 h-3 w-3" />
                <span>{activity.duration}</span>
              </div>
              <div className="flex items-center">
                <FlameIcon className="mr-1 h-3 w-3" />
                <span>{activity.calories} cal</span>
              </div>
            </div>
          </div>
        </div>
      ))}
    </div>
  )
}

