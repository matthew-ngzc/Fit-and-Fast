"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Calendar } from "@/components/ui/calendar"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { EditIcon } from "lucide-react"
import { CycleInfo } from "@/components/cycle-info"
import data from "./data.json" 

// Default cycle data to use as fallback
const defaultCycleData = {
  periodStart: new Date("2025-03-05"),
  periodEnd: new Date("2025-03-10"),
  nextPeriodStart: new Date("2025-04-02"),
  cycleLength: 28,
  periodLength: 5,
}

export default function CalendarPage() {
  const [date, setDate] = useState<Date | undefined>(new Date())
  const [cycleData, setCycleData] = useState<any>(defaultCycleData)
  const [streakDays, setStreakDays] = useState<Date[]>([])
  const [loading, setLoading] = useState(true)

  // api to get streak data 
  // api to get cycleLength, periodLength, periodStart, periodEnd, nextPeriodStart as cycle data
  // api to put cycleLength, periodLength, lastPeriodStartDate

  useEffect(() => {
    async function loadStreakData() {
      try {
        if (data.streakDays && Array.isArray(data.streakDays)) {
          setStreakDays(data.streakDays.map((dateStr: string) => new Date(dateStr)))
        }
      } catch (error) {
        console.error("Failed to load streak data:", error)
      }
    }
  
    async function loadCycleData() {
      try {
        if (data.cycleData) {
          const parsedCycleData = {
            ...data.cycleData,
            periodStart: new Date(data.cycleData.periodStart),
            periodEnd: new Date(data.cycleData.periodEnd),
            nextPeriodStart: new Date(data.cycleData.nextPeriodStart),
          }
          setCycleData(parsedCycleData)
        } else {
          setCycleData(defaultCycleData)
        }
      } catch (error) {
        console.error("Failed to load cycle data:", error)
        setCycleData(defaultCycleData)
      }
    }
  
    async function loadData() {
      await loadStreakData()
      await loadCycleData()
      setLoading(false)
    }
  
    loadData()
  }, [])

  {/* APIS to update */}
  // useEffect(() => {
  //   async function loadStreakData() {
  //     try {
  //       const response = await fetch("/data.json")
  //       const data = await response.json()
  //       if (data.streakDays && Array.isArray(data.streakDays)) {
  //         setStreakDays(data.streakDays.map((dateStr: string) => new Date(dateStr)))
  //       }
  //     } catch (error) {
  //       console.error("Failed to load streak data:", error)
  //     }
  //   }

  //   // API to get cycle data (cycleLength, periodLength, periodStart, periodEnd, nextPeriodStart)
  //   async function loadCycleData() {
  //     try {
  //       const response = await fetch("/api/cycle-data") // Placeholder API endpoint
  //       const data = await response.json()
  //       const parsedCycleData = {
  //         ...data,
  //         periodStart: new Date(data.periodStart),
  //         periodEnd: new Date(data.periodEnd),
  //         nextPeriodStart: new Date(data.nextPeriodStart),
  //       }
  //       setCycleData(parsedCycleData)
  //     } catch (error) {
  //       console.error("Failed to load cycle data:", error)
  //     }
  //   }

  //   // API to update cycle data (cycleLength, periodLength, lastPeriodStartDate)
  //   async function updateCycleData(updatedCycleData: any) {
  //     try {
  //       const response = await fetch("/api/update-cycle-data", {
  //         method: "PUT",
  //         headers: {
  //           "Content-Type": "application/json",
  //         },
  //         body: JSON.stringify(updatedCycleData),
  //       })
  //       if (!response.ok) {
  //         throw new Error("Failed to update cycle data")
  //       }
  //     } catch (error) {
  //       console.error("Error updating cycle data:", error)
  //     }
  //   }

  //   async function loadData() {
  //     await loadStreakData()
  //     await loadCycleData()
  //     setLoading(false)
  //   }

  //   loadData()
  // }, [])

  // Function to determine if a date has a workout streak
  const hasStreak = (date: Date) => {
    return streakDays.some(
      (streakDay) =>
        streakDay.getDate() === date.getDate() &&
        streakDay.getMonth() === date.getMonth() &&
        streakDay.getFullYear() === date.getFullYear(),
    )
  }

  if (loading) {
    return (
      <div className="container px-4 py-6 md:py-10 pb-20 max-w-5xl mx-auto flex items-center justify-center min-h-[50vh]">
        <p>Loading calendar data...</p>
      </div>
    )
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

              {/* To modify according to how the backend build it */}
              <Card>
                <CardHeader>
                  <CardTitle>Workout Recommendations</CardTitle>
                  <CardDescription>Based on your cycle phase</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="p-3 bg-muted rounded-lg">
                    <h3 className="font-medium">Follicular Phase</h3>
                    <p className="text-sm text-muted-foreground">High-intensity workouts are ideal during this phase</p>
                  </div>
                  <Button className="w-full">View Recommended Workouts</Button>
                </CardContent>
              </Card>
            </div>
          </div>
        </section>
      </div>
    </div>
  )
}

function Label({ htmlFor, children }: { htmlFor: string; children: React.ReactNode }) {
  return (
    <label
      htmlFor={htmlFor}
      className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
    >
      {children}
    </label>
  )
}

function Input({ id, ...props }: React.InputHTMLAttributes<HTMLInputElement> & { id: string }) {
  return (
    <input
      id={id}
      className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
      {...props}
    />
  )
}

