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

    // Load Activity Data API call logic
      /* one GET call to /api/history/activity/overview that returns all three elements required
       *  for the activity overview.
       * First: "today": object containing date (not displayed), caloriesBurned: used in setTodayCalories,
       * durationInMinutes: used in setTodayMinutes
       * 
       * Second: weekly, which is for the graph, contains date, caloriesBurned and durationInMinutes.
       * We use date and caloriesBurned on each point on the graph plot, durationInMinds is for future potential use cases
       * 
       * Third: recent workouts. We fetch the 5 most recent workouts, array of objects that contain
       * every bit of data required for each workout. historyId: unique ID for each workout completed,
       * one history is a full, completed unique exercise session. workoutId is one workout session that can be repeated or
       * used by multiple people, for example squats, RDLs, leg press is id 1, shoulder press, pullups is id 2, etc.
       * also received is every other parameter such as name, workout details, calories burned, duration in minutes.
      */

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

  // Load more data API call logic
      /* one GET call to /api/history/load-more that returns
       * parameters:
       * 1. after (DateTime): date and time of bottommost displayed historyId
       * 2. limit (Integer): number of results to return, optional parameter, default 5
       * returned data: more histories. same format as third response from Load Activty Data API Call
      */

  {/* API call to load more data after user scrolls */}
  // async function loadMoreData() => {
  //    const response = await fetch("/api/history/load-more?after={after}&limit={limit}");
  // }
  //
  //
  //
  //
  //
  //
  //
  //
  //

  // OPTIONAL API CALL: Load data between dates API call logic
  // GET /api/history/user/{userId}/date-range?startDate={startDate}&endDate={endDate}
  // startDate, endDate are Strings in the format yyyy-MM-dd (might change to DateTime)
  // We may or may not implement this. Uncertain currently.

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
          {/* potential logic for tracking scrolling past the bottom
            * for loading more workouts than just the 5 displayed 
            function: loadMoreData */}
        </section>
      </div>
    </div>
  );
}
