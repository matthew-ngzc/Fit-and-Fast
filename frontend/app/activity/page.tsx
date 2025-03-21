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
import data from "./data.json";

export default function ActivityPage() {
  // Set up state variables for today's calories and minutes
  const [todayCalories, setTodayCalories] = useState(0);
  const [todayMinutes, setTodayMinutes] = useState(0);

  // Use useEffect to load data when the component mounts
  useEffect(() => {
    async function loadActivityData() {
      try {
        // Set today's calories and minutes from the data
        setTodayCalories(data.todayCalories);
        setTodayMinutes(data.todayMinutes);
      } catch (error) {
        console.error("Failed to load activity data:", error);
      }
    }

    // Call the async function to load activity data
    loadActivityData();
  }, []); // Empty dependency array ensures this runs only once when the component mounts

  {/* API call to get user calories and minutes */}
  // Fetch data from the API when the component mounts
  // useEffect(() => {
  //   async function loadActivityData() {
  //     try {
  //       // Fetch the data from your API
  //       const response = await fetch("/api/activity");

  //       if (!response.ok) {
  //         throw new Error("Failed to fetch data");
  //       }

  //       const data = await response.json();

  //       // Set today's calories and minutes from the API response
  //       setTodayCalories(data.todayCalories);
  //       setTodayMinutes(data.todayMinutes);
  //     } catch (error) {
  //       console.error("Failed to load activity data:", error);
  //     }
  //   }
  //   // Call the async function to load activity data
  //   loadActivityData();
  // }, []);

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
              <ActivityLineChart />
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Recent Workouts</CardTitle>
              <CardDescription>Your workout history</CardDescription>
            </CardHeader>
            <CardContent>
              <ActivityLog />
            </CardContent>
          </Card>
        </section>
      </div>
    </div>
  );
}
