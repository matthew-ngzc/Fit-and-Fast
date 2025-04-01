"use client"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Award, Calendar, Clock, Flame, Home } from "lucide-react"
import confetti from "canvas-confetti"
import { useEffect } from "react"

interface WorkoutCompletionScreenProps {
  duration: string
  calories: string
  exerciseCount: number
  streakDays: number
  totalWorkouts: number
  totalDurationInMinutes: number
  totalCaloriesBurned: number
  onFinish: () => void
}

export function WorkoutCompletionScreen({
  duration,
  calories,
  exerciseCount,
  streakDays,
  totalWorkouts,
  totalDurationInMinutes,
  totalCaloriesBurned,
  onFinish,
}: WorkoutCompletionScreenProps) {
  useEffect(() => {
    confetti({
      particleCount: 100,
      spread: 70,
      origin: { y: 0.6 },
    })
  }, [])

  return (
    <div className="flex flex-col items-center gap-6 py-8">
      <div className="text-center space-y-2">
        <h1 className="text-3xl font-bold">Great job!</h1>
        <p className="text-muted-foreground">You've completed your workout</p>
      </div>

      <div className="w-32 h-32 rounded-full bg-pink-100 flex items-center justify-center">
        <Award className="h-16 w-16 text-primary" />
      </div>

      <div className="grid grid-cols-3 gap-4 w-full max-w-md">
        <Card className="bg-pink-100 border-none">
          <CardContent className="p-4 flex flex-col items-center justify-center text-center">
            <Clock className="h-5 w-5 text-primary mb-1" />
            <p className="text-sm font-medium">{duration}</p>
            <p className="text-xs text-muted-foreground">Duration</p>
          </CardContent>
        </Card>
        <Card className="bg-pink-100 border-none">
          <CardContent className="p-4 flex flex-col items-center justify-center text-center">
            <Flame className="h-5 w-5 text-primary mb-1" />
            <p className="text-sm font-medium">{calories}</p>
            <p className="text-xs text-muted-foreground">Calories</p>
          </CardContent>
        </Card>
        <Card className="bg-pink-100 border-none">
          <CardContent className="p-4 flex flex-col items-center justify-center text-center">
            <Calendar className="h-5 w-5 text-primary mb-1" />
            <p className="text-sm font-medium">{streakDays} days</p>
            <p className="text-xs text-muted-foreground">Streak</p>
          </CardContent>
        </Card>
      </div>

      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle>Workout Summary</CardTitle>
          <CardDescription>You completed {exerciseCount} exercises</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-2">
            <div className="flex justify-between items-center">
              <span className="text-sm">Streak</span>
              <span className="font-medium">{streakDays} days</span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-sm">Total Workouts</span>
              <span className="font-medium">{totalWorkouts}</span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-sm">Total Calories Burned</span>
              <span className="font-medium">{totalCaloriesBurned}</span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-sm">Total Exercise Duration</span>
              <span className="font-medium">{totalDurationInMinutes}</span>
            </div>
          </div>
        </CardContent>
        <CardFooter className="flex justify-center">
          <Button onClick={onFinish} className="gap-2">
            <Home className="h-4 w-4" />
            Return Home
          </Button>
        </CardFooter>
      </Card>
    </div>
  )
}

