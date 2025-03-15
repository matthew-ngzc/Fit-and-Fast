"use client"

import { useState } from "react"
import Link from "next/link"
import Image from "next/image"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Progress } from "@/components/ui/progress"
import { Badge } from "@/components/ui/badge"
import { ArrowLeft, Clock, Flame, Play, Pause, SkipForward, Heart, Award } from "lucide-react"
import { WorkoutCompletionScreen } from "@/components/workout-completion-screen"
import { AIChatButton } from "@/components/ai-chat-button"

export default function WorkoutPage({ params }: { params: { id: string } }) {
  const [workoutState, setWorkoutState] = useState<"pre" | "active" | "completed">("pre")
  const [currentExercise, setCurrentExercise] = useState(0)
  const [isPlaying, setIsPlaying] = useState(false)
  const [timeLeft, setTimeLeft] = useState(40) // Starting with exercise time
  const [isRest, setIsRest] = useState(false)

  // Sample workout data - in a real app this would come from an API or database
  const workout = {
    id: params.id,
    title: "Morning Energy Boost",
    description: "A quick 7-minute workout to energize your morning",
    level: "Beginner",
    duration: "7 min",
    calories: "90",
    category: "HIIT",
    image: "/placeholder.svg?height=300&width=600",
    exercises: [
      {
        name: "Jumping Jacks",
        description:
          "Stand with your feet together and arms at your sides, then jump up with your feet apart and hands overhead.",
        duration: 40,
        rest: 20,
        image: "/placeholder.svg?height=200&width=300",
        tips: "Keep your knees slightly bent to reduce impact.",
      },
      {
        name: "Push-ups",
        description:
          "Start in a plank position with hands shoulder-width apart, lower your body until your chest nearly touches the floor, then push back up.",
        duration: 40,
        rest: 20,
        image: "/placeholder.svg?height=200&width=300",
        tips: "Modify by doing push-ups on your knees if needed.",
      },
      {
        name: "Squats",
        description:
          "Stand with feet shoulder-width apart, lower your body by bending your knees and pushing your hips back, then return to standing.",
        duration: 40,
        rest: 20,
        image: "/placeholder.svg?height=200&width=300",
        tips: "Keep your weight in your heels and chest up.",
      },
      {
        name: "Plank",
        description: "Hold a push-up position with your body in a straight line from head to heels.",
        duration: 40,
        rest: 20,
        image: "/placeholder.svg?height=200&width=300",
        tips: "Engage your core and keep your hips from sagging.",
      },
      {
        name: "Mountain Climbers",
        description:
          "Start in a plank position and alternate bringing each knee toward your chest in a running motion.",
        duration: 40,
        rest: 20,
        image: "/placeholder.svg?height=200&width=300",
        tips: "Keep your hips level and move at a controlled pace.",
      },
    ],
  }

  const startWorkout = () => {
    setWorkoutState("active")
    setIsPlaying(true)
  }

  const togglePlayPause = () => {
    setIsPlaying(!isPlaying)
  }

  const skipExercise = () => {
    if (currentExercise < workout.exercises.length - 1) {
      setCurrentExercise(currentExercise + 1)
      setTimeLeft(40)
      setIsRest(false)
    } else if (!isRest) {
      setIsRest(true)
      setTimeLeft(20)
    } else {
      setWorkoutState("completed")
    }
  }

  const currentExerciseData = workout.exercises[currentExercise]

  return (
    <div className="container px-4 py-6 md:py-10 max-w-5xl mx-auto">
      {workoutState === "pre" && (
        <div className="flex flex-col gap-6">
          <div className="flex items-center gap-2">
            <Link href="/" className="text-muted-foreground hover:text-foreground">
              <ArrowLeft className="h-5 w-5" />
            </Link>
            <h1 className="text-2xl font-bold">{workout.title}</h1>
          </div>

          <div className="relative rounded-lg overflow-hidden h-48 md:h-64">
            <Image src={workout.image || "/placeholder.svg"} alt={workout.title} fill className="object-cover" />
            <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent flex items-end p-4">
              <div className="text-white">
                <h2 className="text-xl font-bold">{workout.title}</h2>
                <p className="text-sm opacity-90">{workout.description}</p>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <Card className="bg-pink-100 border-none">
              <CardContent className="p-4 flex flex-row items-center gap-3">
                <Clock className="h-5 w-5 text-primary" />
                <div>
                  <p className="text-sm font-medium">{workout.duration}</p>
                  <p className="text-xs text-muted-foreground">Duration</p>
                </div>
              </CardContent>
            </Card>
            <Card className="bg-pink-100 border-none">
              <CardContent className="p-4 flex flex-row items-center gap-3">
                <Flame className="h-5 w-5 text-primary" />
                <div>
                  <p className="text-sm font-medium">{workout.calories}</p>
                  <p className="text-xs text-muted-foreground">Calories</p>
                </div>
              </CardContent>
            </Card>
            <Card className="bg-pink-100 border-none">
              <CardContent className="p-4 flex flex-row items-center gap-3">
                <Award className="h-5 w-5 text-primary" />
                <div>
                  <p className="text-sm font-medium">{workout.level}</p>
                  <p className="text-xs text-muted-foreground">Level</p>
                </div>
              </CardContent>
            </Card>
            <Card className="bg-pink-100 border-none">
              <CardContent className="p-4 flex flex-row items-center gap-3">
                <Heart className="h-5 w-5 text-primary" />
                <div>
                  <p className="text-sm font-medium">{workout.category}</p>
                  <p className="text-xs text-muted-foreground">Category</p>
                </div>
              </CardContent>
            </Card>
          </div>

          <Card>
            <CardHeader>
              <CardTitle>Exercises</CardTitle>
              <CardDescription>This workout includes {workout.exercises.length} exercises</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              {workout.exercises.map((exercise, index) => (
                <div key={index} className="flex items-center gap-4 p-3 border rounded-lg">
                  <div className="bg-muted rounded-md h-12 w-12 flex items-center justify-center text-lg font-bold">
                    {index + 1}
                  </div>
                  <div className="flex-1">
                    <h3 className="font-medium">{exercise.name}</h3>
                    <p className="text-sm text-muted-foreground">40s exercise • 20s rest</p>
                  </div>
                </div>
              ))}
            </CardContent>
            <CardFooter className="flex justify-between">
              <AIChatButton />
              <Button onClick={startWorkout} className="gap-2">
                <Play className="h-4 w-4" />
                Start Workout
              </Button>
            </CardFooter>
          </Card>
        </div>
      )}

      {workoutState === "active" && (
        <div className="flex flex-col gap-6">
          <div className="flex items-center justify-between">
            <Link href="/" className="text-muted-foreground hover:text-foreground">
              <ArrowLeft className="h-5 w-5" />
            </Link>
            <h1 className="text-xl font-bold">{workout.title}</h1>
            <div className="w-5"></div> {/* Empty div for spacing */}
          </div>

          <Card className="border-none bg-pink-100">
            <CardContent className="p-4">
              <div className="flex justify-between items-center mb-2">
                <Badge variant={isRest ? "outline" : "default"}>{isRest ? "REST" : "EXERCISE"}</Badge>
                <span className="text-2xl font-bold">{timeLeft}s</span>
              </div>
              <Progress value={isRest ? (timeLeft / 20) * 100 : (timeLeft / 40) * 100} className="h-2 mb-2" />
              <div className="text-sm text-muted-foreground text-right">
                Exercise {currentExercise + 1} of {workout.exercises.length}
              </div>
            </CardContent>
          </Card>

          <div className="relative rounded-lg overflow-hidden h-64 md:h-80">
            <Image
              src={currentExerciseData.image || "/placeholder.svg"}
              alt={currentExerciseData.name}
              fill
              className="object-cover"
            />
          </div>

          <Card>
            <CardHeader>
              <CardTitle>{currentExerciseData.name}</CardTitle>
              <CardDescription>{isRest ? "Take a short break" : currentExerciseData.description}</CardDescription>
            </CardHeader>
            <CardContent>
              {!isRest && (
                <div className="bg-muted p-3 rounded-lg">
                  <p className="text-sm font-medium">Tip:</p>
                  <p className="text-sm">{currentExerciseData.tips}</p>
                </div>
              )}
            </CardContent>
            <CardFooter className="flex justify-center gap-4">
              <Button variant="outline" size="icon" className="h-12 w-12 rounded-full" onClick={togglePlayPause}>
                {isPlaying ? <Pause className="h-6 w-6" /> : <Play className="h-6 w-6" />}
              </Button>
              <Button variant="outline" size="icon" className="h-12 w-12 rounded-full" onClick={skipExercise}>
                <SkipForward className="h-6 w-6" />
              </Button>
            </CardFooter>
          </Card>
        </div>
      )}

      {workoutState === "completed" && (
        <WorkoutCompletionScreen
          duration={workout.duration}
          calories={workout.calories}
          exerciseCount={workout.exercises.length}
          streakDays={6}
          onFinish={() => (window.location.href = "/")}
        />
      )}
    </div>
  )
}

