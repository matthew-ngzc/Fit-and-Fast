"use client";

import type React from "react";

import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Calendar } from "@/components/ui/calendar";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { EditIcon } from "lucide-react";
import { CycleInfo } from "@/components/cycle-info";
import config from "@/config";

export default function CalendarPage() {
  const [date, setDate] = useState<Date | undefined>(new Date());
  const [cycleData, setCycleData] = useState<any>();
  const [streakDays, setStreakDays] = useState<Date[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchCycleData() {
      try {
        const token = localStorage.getItem("token");
        const response = await fetch(`${config.CALENDAR_URL}/cycle-info`, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          throw new Error("Failed to fetch cycle data");
        }

        const data = await response.json();
        const parsedCycleData = {
          ...data,
          lastPeriodStartDate: new Date(data.lastPeriodStartDate),
          lastPeriodEndDate: new Date(data.lastPeriodEndDate),
          nextPeriodStartDate: new Date(data.nextPeriodStartDate),
        };

        setCycleData(parsedCycleData);
      } catch (error) {
        console.error("Error fetching cycle data:", error);
      } finally {
        setLoading(false);
      }
    }

    fetchCycleData();
  }, []);

  useEffect(() => {
    async function loadStreakData() {
      try {
        const currentDate = new Date();
        const year = currentDate.getFullYear();
        const month = currentDate.getMonth() + 1;

        const token = localStorage.getItem("token");

        const response = await fetch(
          `${config.CALENDAR_URL}/workout-dates?year=${year}&month=${month}`,
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (!response.ok) {
          throw new Error("Failed to fetch workout dates");
        }

        const data = await response.json();

        if (Array.isArray(data)) {
          const convertedDates = data.map(
            (dateStr: string) => new Date(dateStr)
          );

          setStreakDays(convertedDates);
        } else {
          console.error(
            "Unexpected data format, expected an array of date strings"
          );
        }
      } catch (error) {
        console.error("Failed to load streak data:", error);
      }
    }

    loadStreakData();
  }, []);

  const hasStreak = (date: Date) => {
    return streakDays.some(
      (streakDay) =>
        streakDay.getDate() === date.getDate() &&
        streakDay.getMonth() === date.getMonth() &&
        streakDay.getFullYear() === date.getFullYear()
    );
  };

  if (loading) {
    return (
      <div className="container px-4 py-6 md:py-10 pb-20 max-w-5xl mx-auto flex items-center justify-center min-h-[50vh]">
        <p>Loading calendar data...</p>
      </div>
    );
  }

  return (
    <div className="container px-4 py-6 md:py-10 pb-20 max-w-5xl mx-auto">
      <div className="flex flex-col gap-6">
        <section>
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <Card className="lg:col-span-2">
              <CardHeader>
                <CardTitle>Your Calendar</CardTitle>
                <CardDescription>Track your workout streaks</CardDescription>
              </CardHeader>
              <CardContent>
                <Calendar
                  mode="single"
                  selected={date}
                  onSelect={setDate}
                  className="rounded-md border"
                  modifiers={{
                    streak: (date) => hasStreak(date),
                  }}
                  modifiersClassNames={{
                    streak: "border-2 border-pink-600",
                  }}
                />
              </CardContent>
              <CardFooter className="flex justify-between text-sm text-muted-foreground">
                <div className="flex items-center">
                  <div className="w-3 h-3 rounded-full border-2 border-pink-600 mr-2"></div>
                  <span>Workout Days</span>
                </div>
              </CardFooter>
            </Card>

            <div className="space-y-6">
              {cycleData && <CycleInfo cycleData={cycleData} />}
            </div>
          </div>
        </section>
      </div>
    </div>
  );
}

function Label({
  htmlFor,
  children,
}: {
  htmlFor: string;
  children: React.ReactNode;
}) {
  return (
    <label
      htmlFor={htmlFor}
      className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
    >
      {children}
    </label>
  );
}

function Input({
  id,
  ...props
}: React.InputHTMLAttributes<HTMLInputElement> & { id: string }) {
  return (
    <input
      id={id}
      className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
      {...props}
    />
  );
}
