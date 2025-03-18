"use client"

import { useState, useEffect } from "react"
import Link from "next/link"
import Image from "next/image"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Progress } from "@/components/ui/progress"
import { Badge } from "@/components/ui/badge"
import { ArrowLeft, Clock, Flame, Play, Pause, SkipForward, Heart, Award, XIcon } from "lucide-react"
import { WorkoutCompletionScreen } from "@/components/workout-completion-screen"
import { AIChatButton } from "@/components/ai-chat-button"
import { fetchData } from "@/lib/data-module"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog"

export default function WorkoutPage({ params }: { params: { id: string } }) {
  const [workoutState, setWorkoutState] = useState<"pre" | "active" | "completed">("pre")
  const [currentExercise, setCurrentExercise] = useState(0)
  const [isPlaying, setIsPlaying] = useState(false)
  const [timeLeft, setTimeLeft] = useState(40) // Starting with exercise time
  const [isRest, setIsRest] = useState(false)
  const [workout, setWorkout] = useState<any>(null)
  const [loading, setLoading] = useState(true)
  const [showExitDialog, setShowExitDialog] = useState(false)

  useEffect(() => {
    async function loadWorkout() {
      try {
        const data = await fetchData()

        // Find the workout in all categories
        let foundWorkout = null
        for (const category in data.workouts) {
          const found = data.workouts[category].find((w: any) => w.id === params.id)
          if (found) {
            foundWorkout = found
            break
          }
        }

        setWorkout(foundWorkout)
      } catch (error) {
        console.error("Failed to load workout:", error)
      } finally {
        setLoading(false)
      }
    }

    loadWorkout()
  }, [params.id])

  // Timer effect for workout
  useEffect(() => {
    if (!isPlaying || !workout) return

    const timer = setInterval(() => {
      setTimeLeft((prev) => {
        if (prev <= 1) {
          // Time's up, switch between exercise and rest
          if (isRest) {
            // Rest is over, move to next exercise
            const nextExerciseIndex = currentExercise + 1
            setCurrentExercise(nextExerciseIndex)
            setIsRest(false)
            return 40 // Exercise duration
          } else {
            // Exercise is over
            // Check if this is the last exercise
            if (currentExercise >= workout.exercises.length - 1) {
              // Last exercise completed, end workout
              clearInterval(timer)
              setWorkoutState("completed")
              return 0
            } else {
              // Not the last exercise, start rest period
              setIsRest(true)
              return 20 // Rest duration
            }
          }
        }
        return prev - 1
      })
    }, 1000)

    return () => clearInterval(timer)
  }, [isPlaying, isRest, currentExercise, workout])

  const startWorkout = () => {
    setWorkoutState("active")
    setIsPlaying(true)
  }

  const togglePlayPause = () => {
    setIsPlaying(!isPlaying)
  }

  const skipExercise = () => {
    if (!workout) return

    if (isRest) {
      // If in rest, skip to the next exercise
      const nextExerciseIndex = currentExercise + 1
      setCurrentExercise(nextExerciseIndex)
      setIsRest(false)
      setTimeLeft(40)
    } else if (currentExercise < workout.exercises.length - 1) {
      // If not the last exercise, skip to rest
      setIsRest(true)
      setTimeLeft(20)
    } else {
      // If last exercise, complete workout
      setWorkoutState("completed")
    }
  }

  const exitWorkout = () => {
    setShowExitDialog(true)
  }

  const confirmExit = () => {
    setShowExitDialog(false)
    setWorkoutState("pre")
    setCurrentExercise(0)
    setIsRest(false)
    setTimeLeft(40)
    setIsPlaying(false)
  }

  const cancelExit = () => {
    setShowExitDialog(false)
  }

  if (loading) {
    return (
      <div className="container px-4 py-6 md:py-10 max-w-5xl mx-auto flex items-center justify-center min-h-[50vh]">
        <p>Loading workout...</p>
      </div>
    )
  }

  if (!workout) {
    return (
      <div className="container px-4 py-6 md:py-10 max-w-5xl mx-auto">
        <div className="flex items-center gap-2">
          <Link href="/" className="text-muted-foreground hover:text-foreground">
            <ArrowLeft className="h-5 w-5" />
          </Link>
          <h1 className="text-2xl font-bold">Workout Not Found</h1>
        </div>
        <p className="mt-4">Sorry, we couldn't find the workout you're looking for.</p>
      </div>
    )
  }

  // Get current exercise data
  const currentExerciseData = workout.exercises[currentExercise]

  // Get next exercise data (for rest periods)
  const nextExerciseIndex = currentExercise + 1
  const nextExerciseData = nextExerciseIndex < workout.exercises.length ? workout.exercises[nextExerciseIndex] : null

  // Determine which exercise to display
  const displayExerciseData = isRest && nextExerciseData ? nextExerciseData : currentExerciseData

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
              {workout.exercises.map((exercise: any, index: number) => (
                <div key={index} className="flex items-center gap-4 p-3 border rounded-lg">
                  <div className="bg-muted rounded-md h-12 w-12 flex items-center justify-center text-lg font-bold">
                    {index + 1}
                  </div>
                  <div className="flex-1">
                    <h3 className="font-medium">{exercise.name}</h3>
                    <p className="text-sm text-muted-foreground">
                      {exercise.duration}s exercise
                      {index < workout.exercises.length - 1 ? ` • ${exercise.rest}s rest` : ""}
                    </p>
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
            <Button
              variant="ghost"
              size="icon"
              className="text-muted-foreground hover:text-destructive"
              onClick={exitWorkout}
            >
              <XIcon className="h-5 w-5" />
            </Button>
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
              src={displayExerciseData.image || "/placeholder.svg"}
              alt={displayExerciseData.name}
              fill
              className="object-cover"
            />
          </div>

          <Card>
            <CardHeader>
              <CardTitle>{isRest ? `Coming up: ${nextExerciseData.name}` : currentExerciseData.name}</CardTitle>
              <CardDescription>
                {isRest ? nextExerciseData.description : currentExerciseData.description}
              </CardDescription>
            </CardHeader>
            <CardContent>
              {isRest ? (
                <div className="bg-muted p-3 rounded-lg">
                  <p className="text-sm font-medium">Get ready for next exercise:</p>
                  <p className="text-sm">{nextExerciseData.tips}</p>
                </div>
              ) : (
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

      <AlertDialog open={showExitDialog} onOpenChange={setShowExitDialog}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Exit Workout?</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to exit this workout? Your progress will not be saved.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel onClick={cancelExit}>Cancel</AlertDialogCancel>
            <AlertDialogAction onClick={confirmExit} className="bg-destructive text-destructive-foreground">
              Exit Workout
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}

