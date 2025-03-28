"use client";

import { useState, useEffect } from "react";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { FlameIcon, TimerIcon } from "lucide-react";
import { ActivityLineChart } from "@/components/activity-line-chart";
import { ActivityLog } from "@/components/activity-log";
import axios from "axios";
import config from "@/config";

interface TodayActivity {
  date: string;
  caloriesBurned: number;
  durationInMinutes: number;
}

interface WeeklyActivity {
  date: string;
  caloriesBurned: number;
  durationInMinutes: number;
}

interface WorkoutHistory {
  historyId: number;
  workoutDateTime: string;
  name: string;
  workout: WorkoutDTO;
  caloriesBurned: number;
  durationInMinutes: number;
}

interface WorkoutDTO {
  name: string;
  category: string;
}

interface ActivityData {
  today: TodayActivity;
  weekly: WeeklyActivity[];
  recentWorkouts: WorkoutHistory[];
}

export default function ActivityPage() {
  // Set up state variables for today's calories and minutes
  const [todayCalories, setTodayCalories] = useState(0);
  const [todayMinutes, setTodayMinutes] = useState(0);
  const [weeklyData, setWeeklyData] = useState<WeeklyActivity[]>([]);
  const [recentWorkouts, setRecentWorkouts] = useState<WorkoutHistory[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Use useEffect to load data when the component mounts
  useEffect(() => {
    async function loadActivityData() {
      try {
        const token = localStorage.getItem("token");

        if (!token) {
          setError("No authentication token found. Please log in.");
          setIsLoading(false);
          return;
        }

        setIsLoading(true);

        const response = await axios.get<ActivityData>(
          `${config.HISTORY_URL}/activity/overview`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        const { today, weekly, recentWorkouts } = response.data;

        const validWeeklyData: WeeklyActivity[] = weekly.map((item) => {
          return {
            date: item.date,
            caloriesBurned: item.caloriesBurned,
            durationInMinutes: item.durationInMinutes,
          };
        });

        setTodayCalories(today.caloriesBurned);
        setTodayMinutes(today.durationInMinutes);
        setWeeklyData(validWeeklyData);
        setRecentWorkouts(recentWorkouts);

        console.log(recentWorkouts);
      } catch (error) {
        if (axios.isAxiosError(error)) {
          setError(
            error.response?.data?.message ||
              "An error occurred while fetching activity data"
          );
          console.error("API error:", error);
        } else {
          setError("An unexpected error occurred");
          console.error("Unexpected error:", error);
        }
      } finally {
        setIsLoading(false);
      }
    }

    loadActivityData();
  }, []);

  return (
    <div className="container px-4 py-6 md:py-10 pb-20 max-w-5xl mx-auto">
      <div className="flex flex-col gap-6">
        <section>
          <h1 className="text-2xl font-bold tracking-tight mb-4">
            Your Activity
          </h1>

          <div className="grid grid-cols-2 gap-4 mb-6">
            <Card className="bg-pink-100 border-none">
              <CardContent className="p-4 flex flex-row items-center gap-4">
                <div className="bg-white/50 rounded-full p-2">
                  <FlameIcon className="h-6 w-6 text-primary" />
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">
                    Today's Calories
                  </p>
                  <p className="text-2xl font-bold">{todayCalories}</p>
                </div>
              </CardContent>
            </Card>
            <Card className="bg-pink-100 border-none">
              <CardContent className="p-4 flex flex-row items-center gap-4">
                <div className="bg-white/50 rounded-full p-2">
                  <TimerIcon className="h-6 w-6 text-primary" />
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">
                    Today's Minutes
                  </p>
                  <p className="text-2xl font-bold">{todayMinutes}</p>
                </div>
              </CardContent>
            </Card>
          </div>

          <Card className="mb-6">
            <CardHeader>
              <CardTitle>Weekly Progress</CardTitle>
              <CardDescription>
                Calories burned over the past 7 days
              </CardDescription>
            </CardHeader>
            <CardContent>
              <ActivityLineChart weeklyData={weeklyData} />
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Recent Workouts</CardTitle>
              <CardDescription>Your workout history</CardDescription>
            </CardHeader>
            <CardContent>
              <ActivityLog recentWorkouts={recentWorkouts} />
            </CardContent>
          </Card>
        </section>
      </div>
    </div>
  );
}
