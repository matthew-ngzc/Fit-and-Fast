"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import Image from "next/image";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { Badge } from "@/components/ui/badge";
import {
  ArrowLeft,
  Clock,
  Flame,
  Play,
  Pause,
  SkipForward,
  Heart,
  Award,
  XIcon,
} from "lucide-react";
import { WorkoutCompletionScreen } from "@/components/workout-completion-screen";
import { AIChatButton } from "@/components/ai-chat-button";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import workoutsData from "@/public/workouts.json";
import config from "@/config";
import axios from "axios";

interface WorkoutProgramType {
  workoutId: number;
  name: string;
  description: string;
  image: string;
  durationInMinutes: string;
  calories: string;
  level: string;
  category: string;
  exercises: { name: string; duration: number; rest: number }[];
  workoutExercise: { name: string; duration: number; rest: number }[];
}

interface ExerciseDetailType {
  name: string;
  description: string;
  image: string;
  duration: number;
  rest: number;
  tips: string;
}

interface Exercise {
  name: string;
  duration: number;
  rest: number;
}

type Workout = {
  description: string;
  tip: string;
  image: string;
};

type WorkoutsData = {
  workouts: Record<string, Workout>;
};

interface WorkoutCompletionData {
  totalWorkouts: number;
  totalDurationInMinutes: number;
  totalCaloriesBurned: number;
}

export default function WorkoutPage({ params }: { params: { id: string } }) {
  const [workoutState, setWorkoutState] = useState<
    "pre" | "active" | "completed"
  >("pre");
  const [currentExercise, setCurrentExercise] = useState(0);
  const [isPlaying, setIsPlaying] = useState(false);
  const [timeLeft, setTimeLeft] = useState(0);
  const [isRest, setIsRest] = useState(false);
  const [workout, setWorkout] = useState<WorkoutProgramType | null>(null);
  const [exerciseDetails, setExerciseDetails] = useState<ExerciseDetailType[]>(
    []
  );
  const [loading, setLoading] = useState(true);
  const [showExitDialog, setShowExitDialog] = useState(false);
  const [streak, setStreak] = useState<number>(0);
  const [workoutCompletionData, setWorkoutCompletionData] =
    useState<WorkoutCompletionData | null>(null);
  const [completionLoading, setCompletionLoading] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("token");

    async function fetchStreak() {
      try {
        const response = await axios.get(`${config.HOME_URL}/streak`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setStreak(response.data.days);
      } catch (error) {
        console.error("Error fetching streak:", error);
      }
    }

    fetchStreak();
  }, []);

  useEffect(() => {
    const storedWorkout = localStorage.getItem("currentWorkout");
    if (storedWorkout) {
      const workoutData: WorkoutProgramType = JSON.parse(storedWorkout);
      setWorkout(workoutData);
    }
  }, []);

  useEffect(() => {
    if (!workout) {
      return;
    }

    async function fetchWorkoutExercises() {
      try {
        if (params.id === "0") {
          if (!workout) {
            console.error("Workout or workout.exercises is undefined");
            setLoading(false);
            return;
          }

          // AI-generated exercises logic
          const aiGeneratedExercises = workout.workoutExercise.map(
            ({ name, duration, rest }: Exercise) => {
              const workoutDetails = (workoutsData as WorkoutsData).workouts[
                name
              ];
              return {
                name,
                description:
                  workoutDetails?.description || "No description available",
                image: workoutDetails?.image || "",
                duration,
                rest,
                tips: workoutDetails?.tip || "No tips available",
              };
            }
          );
          setExerciseDetails(aiGeneratedExercises);
          return;
        }

        // If id !== "0", make API call (existing logic)
        const token = localStorage.getItem("token");
        const response = await axios.get(
          `${config.WORKOUT_URL}/${params.id}/exercises`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );

        const formattedExercises = response.data.map(
          ({ name, duration, rest }: Exercise) => {
            const workoutDetails = (workoutsData as WorkoutsData).workouts[
              name
            ];
            return {
              name,
              description: workoutDetails?.description || "",
              image: workoutDetails?.image || "",
              duration,
              rest,
              tips: workoutDetails?.tip || "",
            };
          }
        );

        setExerciseDetails(formattedExercises);
      } catch (error) {
        console.error("Error fetching workout exercises:", error);
      } finally {
        setLoading(false);
      }
    }

    fetchWorkoutExercises();
  }, [workout, params.id]);

  // Initialize timeLeft with first exercise duration when exercise details are loaded
  useEffect(() => {
    if (exerciseDetails.length > 0) {
      setTimeLeft(exerciseDetails[0].duration);
    }
  }, [exerciseDetails]);

  const postWorkoutCompletion = async () => {
    setCompletionLoading(true);
    try {
      const token = localStorage.getItem("token");

      const requestBody = {
        workoutId: workout?.workoutId,
      };

      const response = await fetch(`${config.PROGRESS_URL}/complete`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(requestBody),
      });

      const data = await response.json();

      try {
        const streakResponse = await axios.get(`${config.HOME_URL}/streak`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setStreak(streakResponse.data.days);

        setWorkoutCompletionData(data);
      } catch (error) {
        console.error("Error fetching updated streak:", error);
        setWorkoutCompletionData(data);
      }
    } catch (error) {
      console.error("Error posting workout completion data:", error);
    } finally {
      setCompletionLoading(false);
    }
  };

  useEffect(() => {
    if (workoutState === "completed") {
      postWorkoutCompletion();
    }
  }, [workoutState]);

  useEffect(() => {
    if (!isPlaying || !workout) return;

    const timer = setInterval(() => {
      setTimeLeft((prev) => {
        if (prev <= 1) {
          if (isRest) {
            const nextExerciseIndex = currentExercise + 1;
            setCurrentExercise(nextExerciseIndex);
            setIsRest(false);
            return exerciseDetails[nextExerciseIndex]?.duration || 0;
          } else {
            if (currentExercise >= exerciseDetails.length - 1) {
              // Last exercise completed, end workout
              clearInterval(timer);
              setWorkoutState("completed");
              return 0;
            } else {
              setIsRest(true);
              return exerciseDetails[currentExercise]?.rest || 0;
            }
          }
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [isPlaying, isRest, currentExercise, workout, exerciseDetails.length]);

  const startWorkout = () => {
    setWorkoutState("active");
    setIsPlaying(true);
  };

  const togglePlayPause = () => {
    setIsPlaying(!isPlaying);
  };

  const skipExercise = () => {
    if (!workout) return;

    if (isRest) {
      const nextExerciseIndex = currentExercise + 1;
      setCurrentExercise(nextExerciseIndex);
      setIsRest(false);
      setTimeLeft(exerciseDetails[nextExerciseIndex]?.duration || 0);
    } else if (currentExercise < exerciseDetails.length - 1) {
      setIsRest(true);
      setTimeLeft(exerciseDetails[currentExercise]?.rest || 0);
    } else {
      setWorkoutState("completed");
    }
  };

  const exitWorkout = () => {
    setShowExitDialog(true);
  };

  const confirmExit = () => {
    setShowExitDialog(false);
    setWorkoutState("pre");
    setCurrentExercise(0);
    setIsRest(false);
    setTimeLeft(exerciseDetails[0]?.duration || 0);
    setIsPlaying(false);
  };

  const cancelExit = () => {
    setShowExitDialog(false);
  };

  if (!workout) {
    return (
      <div className="container px-4 py-6 md:py-10 max-w-5xl mx-auto">
        <div className="flex items-center gap-2">
          <Link
            href="/home"
            className="text-muted-foreground hover:text-foreground"
          >
            <ArrowLeft className="h-5 w-5" />
          </Link>
          <h1 className="text-2xl font-bold">Workout Not Found</h1>
        </div>
        <p className="mt-4">
          Sorry, we couldn't find the workout you're looking for.
        </p>
      </div>
    );
  }

  const currentExerciseData = exerciseDetails[currentExercise];
  const nextExerciseIndex = currentExercise + 1;
  const nextExerciseData =
    nextExerciseIndex < exerciseDetails.length
      ? exerciseDetails[nextExerciseIndex]
      : null;

  const displayExerciseData =
    isRest && nextExerciseData ? nextExerciseData : currentExerciseData;

  if (loading) {
    return (
      <div className="container px-4 py-6 md:py-10 pb-20 max-w-5xl mx-auto flex items-center justify-center min-h-[50vh]">
        <svg
          className="animate-spin h-8 w-8 text-pink-600"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
        >
          <circle
            className="opacity-25"
            cx="12"
            cy="12"
            r="10"
            stroke="currentColor"
            strokeWidth="4"
          />
          <path
            className="opacity-75"
            fill="currentColor"
            d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"
          />
        </svg>
      </div>
    );
  }

  return (
    <div className="container px-4 py-6 md:py-10 max-w-5xl mx-auto">
      {workoutState === "pre" && (
        <div className="flex flex-col gap-6">
          <div className="flex items-center gap-2">
            <Link
              href="/home"
              className="text-muted-foreground hover:text-foreground"
            >
              <ArrowLeft className="h-5 w-5" />
            </Link>
            <h1 className="text-2xl font-bold">{workout.name}</h1>
          </div>

          <div className="relative rounded-lg overflow-hidden h-48 md:h-64">
            <Image
              src={workout.image || "/placeholder.svg"}
              alt={workout.name}
              fill
              className="object-cover"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent flex items-end p-4">
              <div className="text-white">
                <h2 className="text-xl font-bold">{workout.name}</h2>
                <p className="text-sm opacity-90">{workout.description}</p>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <Card className="bg-pink-100 border-none">
              <CardContent className="p-4 flex flex-row items-center gap-3">
                <Clock className="h-5 w-5 text-primary" />
                <div>
                  <p className="text-sm font-medium">
                    {workout.durationInMinutes}
                  </p>
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
                  <p className="text-sm font-medium">
                    {workout.category.charAt(0).toUpperCase() +
                      workout.category.slice(1)}
                  </p>
                  <p className="text-xs text-muted-foreground">Category</p>
                </div>
              </CardContent>
            </Card>
          </div>

          <Card>
            <CardHeader>
              <CardTitle>Exercises</CardTitle>
              <CardDescription>
                This workout includes {exerciseDetails.length} exercises
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              {exerciseDetails.map(
                (exercise: ExerciseDetailType, index: number) => (
                  <div
                    key={index}
                    className="flex items-center gap-4 p-3 border rounded-lg"
                  >
                    <div className="bg-muted rounded-md h-12 w-12 flex items-center justify-center text-lg font-bold">
                      {index + 1}
                    </div>
                    <div className="flex-1">
                      <h3 className="font-medium">{exercise.name}</h3>
                      <p className="text-sm text-muted-foreground">
                        {exercise.duration}s exercise
                        {index < exerciseDetails.length - 1
                          ? ` • ${exercise.rest}s rest`
                          : ""}
                      </p>
                    </div>
                  </div>
                )
              )}
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
            <Link
              href="/home"
              className="text-muted-foreground hover:text-foreground"
            >
              <ArrowLeft className="h-5 w-5" />
            </Link>
            <h1 className="text-xl font-bold">{workout.name}</h1>
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
                <Badge variant={isRest ? "outline" : "default"}>
                  {isRest ? "REST" : "EXERCISE"}
                </Badge>
                <span className="text-2xl font-bold">{timeLeft}s</span>
              </div>
              <Progress
                value={
                  isRest
                    ? (timeLeft / (currentExerciseData?.rest || 1)) * 100
                    : (timeLeft / (currentExerciseData?.duration || 1)) * 100
                }
                className="h-2 mb-2"
              />
              <div className="text-sm text-muted-foreground text-right">
                Exercise {currentExercise + 1} of {exerciseDetails.length}
              </div>
            </CardContent>
          </Card>

          <div className="relative rounded-lg overflow-hidden h-64 md:h-80">
            <img
              src={displayExerciseData.image || "/placeholder.svg"}
              alt={displayExerciseData.name}
              className="object-contain w-full h-full"
              style={{ maxWidth: "100%", maxHeight: "100%" }}
            />
          </div>

          <Card>
            <CardHeader>
              <CardTitle>
                {isRest
                  ? `Coming up: ${nextExerciseData?.name}`
                  : currentExerciseData.name}
              </CardTitle>
              <CardDescription>
                {isRest
                  ? nextExerciseData?.description
                  : currentExerciseData.description}
              </CardDescription>
            </CardHeader>
            <CardContent>
              {isRest ? (
                <div className="bg-muted p-3 rounded-lg">
                  <p className="text-sm font-medium">
                    Get ready for next exercise:
                  </p>
                  <p className="text-sm">{nextExerciseData?.tips}</p>
                </div>
              ) : (
                <div className="bg-muted p-3 rounded-lg">
                  <p className="text-sm font-medium">Tip:</p>
                  <p className="text-sm">{currentExerciseData.tips}</p>
                </div>
              )}
            </CardContent>
            <CardFooter className="flex justify-center gap-4">
              <Button
                variant="outline"
                size="icon"
                className="h-12 w-12 rounded-full"
                onClick={togglePlayPause}
              >
                {isPlaying ? (
                  <Pause className="h-6 w-6" />
                ) : (
                  <Play className="h-6 w-6" />
                )}
              </Button>
              <Button
                variant="outline"
                size="icon"
                className="h-12 w-12 rounded-full"
                onClick={skipExercise}
              >
                <SkipForward className="h-6 w-6" />
              </Button>
            </CardFooter>
          </Card>
        </div>
      )}

      {workoutState === "completed" && (
        <>
          {completionLoading ? (
            <div className="container px-4 py-6 md:py-10 pb-20 max-w-5xl mx-auto flex items-center justify-center min-h-[50vh]">
              <svg
                className="animate-spin h-8 w-8 text-pink-600"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
              >
                <circle
                  className="opacity-25"
                  cx="12"
                  cy="12"
                  r="10"
                  stroke="currentColor"
                  strokeWidth="4"
                />
                <path
                  className="opacity-75"
                  fill="currentColor"
                  d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"
                />
              </svg>
              <span className="ml-2 text-lg font-medium">
                Updating your progress...
              </span>
            </div>
          ) : workoutCompletionData ? (
            <WorkoutCompletionScreen
              duration={workout.durationInMinutes}
              calories={workout.calories}
              exerciseCount={exerciseDetails.length}
              streakDays={streak}
              totalWorkouts={workoutCompletionData.totalWorkouts}
              totalDurationInMinutes={
                workoutCompletionData.totalDurationInMinutes
              }
              totalCaloriesBurned={workoutCompletionData.totalCaloriesBurned}
              onFinish={() => (window.location.href = "/home")}
            />
          ) : null}
        </>
      )}

      <AlertDialog open={showExitDialog} onOpenChange={setShowExitDialog}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Exit Workout?</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to exit this workout? Your progress will not
              be saved.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel onClick={cancelExit}>Cancel</AlertDialogCancel>
            <AlertDialogAction
              onClick={confirmExit}
              className="bg-destructive text-destructive-foreground"
            >
              Exit Workout
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
