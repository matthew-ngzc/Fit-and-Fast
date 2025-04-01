"use client"

import { Badge } from "@/components/ui/badge"
import { CalendarIcon, ClockIcon, FlameIcon } from "lucide-react"
import { useEffect, useState } from "react"

type WorkoutHistory = {
  historyId: number;
  workoutDateTime: string;
  name: string;
  workout: WorkoutDTO;
  caloriesBurned: number;
  durationInMinutes: number;
}

type WorkoutDTO = {
  name: string;
  category: string;
}

export function ActivityLog({ recentWorkouts }: { recentWorkouts: WorkoutHistory[] }) {
  const [activities, setActivities] = useState<WorkoutHistory[]>(recentWorkouts)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setActivities(recentWorkouts)
    console.log(activities);
  }, [recentWorkouts]) 

  if (loading) {
    return <div className="py-4 text-center">Loading activities...</div>
  }

  return (
    <div className="space-y-4">
      {activities.map((activity) => (
        <div key={activity.historyId} className="flex items-start gap-4 p-4 border rounded-lg">
          <div className="flex-1 space-y-1">
            <div className="flex items-center justify-between">
              <h4 className="font-medium">{activity.workout.name}</h4>
              <Badge variant="outline">{activity.workout.category}</Badge>
            </div>
            <div className="flex items-center text-sm text-muted-foreground">
              <CalendarIcon className="mr-1 h-3 w-3" />
              <span>{new Date(activity.workoutDateTime).toLocaleDateString()}</span>
            </div>
            <div className="flex items-center gap-4 text-sm text-muted-foreground">
              <div className="flex items-center">
                <ClockIcon className="mr-1 h-3 w-3" />
                <span>{activity.durationInMinutes} min</span>
              </div>
              <div className="flex items-center">
                <FlameIcon className="mr-1 h-3 w-3" />
                <span>{activity.caloriesBurned} cal</span>
              </div>
            </div>
          </div>
        </div>
      ))}
    </div>
  )
}
