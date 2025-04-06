"use client";

import axios from "axios";
import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import Link from "next/link";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
  DumbbellIcon,
  HeartIcon,
  TrendingUpIcon,
  SpaceIcon as Yoga,
  Zap,
  Baby,
  Sparkles,
  Activity,
  UserIcon,
} from "lucide-react";
import { WorkoutCard } from "../../components/workout-card";
import "@/styles/homePage.css";
import config from "@/config";

interface Workout {
  workoutId: number;
  name: string;
  durationInMinutes: number;
  level: string;
  calories: number;
  image: string;
  category: string;
  description: string;
}

interface WorkoutsByCategory {
  [key: string]: Workout[];
}

interface WorkoutCategory {
  id: string;
  name: string;
  icon: string;
}

export default function HomePage() {
  const [data, setData] = useState<{
    workoutCategories: WorkoutCategory[];
    workouts: WorkoutsByCategory;
  } | null>(null);
  const [selectedTab, setSelectedTab] = useState<string>("yoga");
  const [loading, setLoading] = useState(true);
  const [streak, setStreak] = useState<number>(0);
  const [username, setUsername] = useState("");
  const [workoutsData, setWorkoutsData] = useState<Workout[]>([]);
  const [cycleData, setCycleData] = useState({ currentPhase: "" });
  const [recommendedWorkoutData, setRecommendedWorkoutData] = useState<{
    workoutId: number | null;
    title: string | null;
    description: string | null;
    level: string | null;
    category: string | null;
    calories: string | null;
  } | null>(null);

  const handleButtonClick = () => {
    // Check if recommendedWorkoutData is available and if workoutId is not null
    if (
      !recommendedWorkoutData ||
      !recommendedWorkoutData.workoutId ||
      !recommendedWorkoutData.category
    ) {
      return;
    }

    // Access the array of workouts for the category
    const workoutCategory = workouts[recommendedWorkoutData.category];

    if (workoutCategory && Array.isArray(workoutCategory)) {
      // Find the workout within the array using the workoutId
      const workout = workoutCategory.find(
        (w) => w.workoutId === recommendedWorkoutData.workoutId
      );

      if (workout) {
        // If workout is found, store it in localStorage
        localStorage.setItem("currentWorkout", JSON.stringify(workout));
      } else {
      }
    } else {
    }
  };

  useEffect(() => {
    async function fetchCycleData() {
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get(`${config.CALENDAR_URL}/cycle-info`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        setCycleData(response.data);
      } catch (error) {
        console.error("Error fetching cycle data:", error);
      }
    }

    fetchCycleData();
  }, []);

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

    async function fetchUsername() {
      try {
        const response = await axios.get(`${config.PROFILE_URL}/`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setUsername(response.data.username);
      } catch (error) {
        console.error("Error fetching username:", error);
      }
    }

    fetchStreak();
    fetchUsername();
  }, []);

  useEffect(() => {
    const token = localStorage.getItem("token");
    const today = new Date().toISOString().split("T")[0];

    const savedWorkout = localStorage.getItem("recommendedWorkoutData");
    const savedDate = localStorage.getItem("recommendedWorkoutDate");

    if (savedWorkout && savedDate === today) {
      setRecommendedWorkoutData(JSON.parse(savedWorkout));
      return;
    }

    async function fetchWorkoutRecommendation() {
      try {
        const response = await axios.get(`${config.HOME_URL}/recommendation`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        const workoutRecommendation = response.data;

        const workoutData = {
          workoutId: workoutRecommendation.workoutId || null,
          title: workoutRecommendation.title || null,
          description: workoutRecommendation.description || null,
          level: workoutRecommendation.level || null,
          category: workoutRecommendation.category || null,
          calories: workoutRecommendation.calories || null,
        };

        localStorage.setItem(
          "recommendedWorkoutData",
          JSON.stringify(workoutData)
        );
        localStorage.setItem("recommendedWorkoutDate", today);

        setRecommendedWorkoutData(workoutData);
      } catch (error) {
        console.error("Error fetching workout recommendation:", error);
      }
    }

    fetchWorkoutRecommendation();
  }, []);

  useEffect(() => {
    async function loadWorkouts() {
      try {
        const token = localStorage.getItem("token");

        if (!token) {
          console.error("No token found. Please log in.");
          return;
        }

        const response = await axios.get(`${config.HOME_URL}/workouts`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        setWorkoutsData(response.data);
        setLoading(false);
      } catch (error) {
        console.error("Error fetching workouts:", error);
        setLoading(false);
      }
    }

    loadWorkouts();
  }, []);

  useEffect(() => {
    async function loadData() {
      const workoutCategories: WorkoutCategory[] = [
        { id: "yoga", name: "Yoga", icon: "yoga" },
        { id: "hiit", name: "HIIT", icon: "zap" },
        { id: "strength", name: "Strength", icon: "dumbbell" },
        { id: "prenatal", name: "Prenatal", icon: "baby" },
        { id: "postnatal", name: "Postnatal", icon: "sparkles" },
        { id: "low-impact", name: "Low Impact", icon: "activity" },
        { id: "body-weight", name: "Body Weight", icon: "user" },
        { id: "others", name: "Others", icon: "heart" },
      ];

      setData({
        workoutCategories,
        workouts: workoutsData as unknown as WorkoutsByCategory,
      });
    }

    if (typeof window !== "undefined") {
      const savedTab = localStorage.getItem("selectedWorkoutTab");
      if (savedTab) {
        setSelectedTab(savedTab);
      }
    }

    loadData();
  }, [workoutsData]);

  const saveSelectedTab = (value: string) => {
    setSelectedTab(value);
    if (typeof window !== "undefined") {
      localStorage.setItem("selectedWorkoutTab", value);
    }
  };

  // Don't show full page loading, only render main UI structure
  const { workoutCategories = [], workouts = {} } = data || {};

  return (
    <div className="container px-4 py-6 md:py-10 pb-20 max-w-5xl mx-auto">
      <div className="flex flex-col gap-6">
        <section className="space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="welcome-heading">Welcome back, {username}!</h1>
              <p className="text-muted-foreground">
                Ready for your 7-minute workout today?
              </p>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <Card className="md:col-span-2">
              <CardHeader className="pb-2">
                <CardTitle>Today's Recommendation</CardTitle>
              </CardHeader>
              <CardContent>
                {cycleData.currentPhase === "Menstrual Phase" && (
                  <div className="bg-pink-50 border border-pink-200 rounded-lg p-3 mb-3 flex items-center gap-2">
                    <div className="bg-pink-100 rounded-full p-1.5">
                      <Activity className="h-4 w-4 text-pink-600" />
                    </div>
                    <div>
                      <p className="text-sm font-medium text-pink-700">
                        Menstrual Phase
                      </p>
                      <p className="text-xs text-pink-600">
                        Low impact exercise suggested
                      </p>
                    </div>
                  </div>
                )}
                <div className="bg-muted/50 rounded-lg p-4 flex flex-col md:flex-row gap-4 items-center">
                  <div className="bg-pink-100 rounded-full p-3">
                    <HeartIcon className="h-8 w-8 text-primary" />
                  </div>
                  <div className="flex-1 text-center md:text-left">
                    <h3 className="font-semibold text-lg">
                      {recommendedWorkoutData?.title}
                    </h3>
                    <p className="text-muted-foreground text-sm">
                      {recommendedWorkoutData?.description}
                    </p>
                  </div>
                  <Button asChild onClick={handleButtonClick}>
                    <Link
                      href={
                        recommendedWorkoutData?.workoutId
                          ? `/workout/${recommendedWorkoutData.workoutId}`
                          : "#"
                      }
                    >
                      Start Workout
                    </Link>
                  </Button>
                </div>
              </CardContent>
            </Card>

            <Card className="bg-gradient-to-br from-pink-100 to-pink-50 border-none shadow-md">
              <CardContent className="p-6 flex flex-col items-center justify-center text-center h-full">
                <div className="bg-white/80 rounded-full p-3 mb-3 shadow-sm">
                  <TrendingUpIcon className="h-8 w-8 text-primary" />
                </div>
                <p className="text-3xl font-bold text-primary">
                  {streak > 0 ? streak : "Start your streak!"}
                </p>
                <p className="text-sm font-medium text-muted-foreground">
                  Day Streak
                </p>
                <p className="text-xs mt-2">
                  {streak > 0 ? "Keep it up!" : "Let's get started!"}
                </p>
              </CardContent>
            </Card>
          </div>
        </section>

        <section className="space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-semibold bg-gradient-to-r from-primary to-pink-400 bg-clip-text text-transparent">
              Workout Categories
            </h2>
          </div>

          <Tabs
            value={selectedTab}
            onValueChange={saveSelectedTab}
            className="w-full"
          >
            <div className="bg-pink-100/50 p-2 rounded-lg mb-4">
              <TabsList className="grid grid-cols-2 md:grid-cols-4 h-auto bg-white/80 p-1 rounded-md shadow-sm">
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

            {/* Show loading spinner specifically in the workout categories section */}
            {loading ? (
              <div className="flex items-center justify-center py-12">
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
            ) : (
              workoutCategories.map((category) => (
                <TabsContent
                  key={category.id}
                  value={category.id}
                  className="mt-4"
                >
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {workouts[category.id]?.map((workout) => (
                      <WorkoutCard
                        key={workout.workoutId}
                        workoutData={workout}
                        href={`/workout/${workout.workoutId}`}
                      />
                    ))}
                  </div>
                </TabsContent>
              ))
            )}
          </Tabs>
        </section>
      </div>
    </div>
  );
}

function getIconComponent(iconName: string) {
  switch (iconName) {
    case "yoga":
      return <Yoga className="h-5 w-5" />;
    case "zap":
      return <Zap className="h-5 w-5" />;
    case "dumbbell":
      return <DumbbellIcon className="h-5 w-5" />;
    case "heart":
      return <HeartIcon className="h-5 w-5" />;
    case "baby":
      return <Baby className="h-5 w-5" />;
    case "sparkles":
      return <Sparkles className="h-5 w-5" />;
    case "activity":
      return <Activity className="h-5 w-5" />;
    case "user":
      return <UserIcon className="h-5 w-5" />;
    default:
      return <DumbbellIcon className="h-5 w-5" />;
  }
}
