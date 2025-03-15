"use client"

import type React from "react"

import { useState } from "react"
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

export default function CalendarPage() {
  const [date, setDate] = useState<Date | undefined>(new Date())

  // Example cycle data - in a real app this would come from a database
  const cycleData = {
    periodStart: new Date(2025, 2, 5),
    periodEnd: new Date(2025, 2, 10),
    nextPeriodStart: new Date(2025, 3, 2),
    cycleLength: 28,
    periodLength: 5,
  }

  // Example streak data - in a real app this would come from a database
  const streakDays = [
    new Date(2025, 2, 1),
    new Date(2025, 2, 2),
    new Date(2025, 2, 3),
    new Date(2025, 2, 4),
    new Date(2025, 2, 5),
  ]

  // Function to determine if a date is in the period
  const isInPeriod = (date: Date) => {
    const d = new Date(date)
    return d >= cycleData.periodStart && d <= cycleData.periodEnd
  }

  // Function to determine if a date has a workout streak
  const hasStreak = (date: Date) => {
    return streakDays.some(
      (streakDay) =>
        streakDay.getDate() === date.getDate() &&
        streakDay.getMonth() === date.getMonth() &&
        streakDay.getFullYear() === date.getFullYear(),
    )
  }

  return (
    <div className="container px-4 py-6 md:py-10 pb-20 max-w-5xl mx-auto">
      <div className="flex flex-col gap-6">
        <section>
          <div className="flex items-center justify-between mb-4">
            <h1 className="text-2xl font-bold tracking-tight">Calendar</h1>
            <Dialog>
              <DialogTrigger asChild>
                <Button variant="outline" size="sm">
                  <EditIcon className="h-4 w-4 mr-2" />
                  Edit Period
                </Button>
              </DialogTrigger>
              <DialogContent>
                <DialogHeader>
                  <DialogTitle>Edit Period Information</DialogTitle>
                  <DialogDescription>Update your period details to get more accurate predictions</DialogDescription>
                </DialogHeader>
                <div className="grid gap-4 py-4">
                  <div className="grid grid-cols-2 gap-4">
                    <div className="grid gap-2">
                      <Label htmlFor="cycle-length">Cycle Length</Label>
                      <Input id="cycle-length" defaultValue="28" />
                    </div>
                    <div className="grid gap-2">
                      <Label htmlFor="period-length">Period Length</Label>
                      <Input id="period-length" defaultValue="5" />
                    </div>
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="last-period">Last Period Start Date</Label>
                    <div className="flex gap-2">
                      <Input id="last-period" defaultValue="2025-03-05" type="date" />
                    </div>
                  </div>
                </div>
                <DialogFooter>
                  <Button type="submit">Save changes</Button>
                </DialogFooter>
              </DialogContent>
            </Dialog>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <Card className="lg:col-span-2">
              <CardHeader>
                <CardTitle>Your Calendar</CardTitle>
                <CardDescription>Track your period and workout streaks</CardDescription>
              </CardHeader>
              <CardContent>
                <Calendar
                  mode="single"
                  selected={date}
                  onSelect={setDate}
                  className="rounded-md border"
                  modifiers={{
                    period: (date) => isInPeriod(date),
                    streak: (date) => hasStreak(date),
                  }}
                  modifiersClassNames={{
                    period: "bg-pink-100 text-primary rounded-md",
                    streak: "border-2 border-green-600",
                  }}
                />
              </CardContent>
              <CardFooter className="flex justify-between text-sm text-muted-foreground">
                <div className="flex items-center">
                  <div className="w-3 h-3 rounded-full bg-primary mr-2"></div>
                  <span>Period</span>
                </div>
                <div className="flex items-center">
                  <div className="w-3 h-3 rounded-full border-2 border-green-600 mr-2"></div>
                  <span>Workout Streak</span>
                </div>
              </CardFooter>
            </Card>

            <div className="space-y-6">
              <CycleInfo cycleData={cycleData} />

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

