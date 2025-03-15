"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Badge } from "@/components/ui/badge"
import { Progress } from "@/components/ui/progress"
import { Switch } from "@/components/ui/switch"
import { EditIcon, LogOutIcon, SettingsIcon, TrophyIcon, CheckIcon, XIcon } from "lucide-react"
import Link from "next/link"

export default function ProfilePage() {
  const [isEditing, setIsEditing] = useState(false)

  // Sample user data - in a real app this would come from a database
  const userData = {
    name: "Sarah Anderson",
    email: "sarah@example.com",
    height: "165",
    weight: "58",
    birthdate: "1990-05-15",
    goal: "Stay fit",
    workoutsPerWeek: "5 days",
  }

  return (
    <div className="container px-4 py-6 md:py-10 pb-20 max-w-5xl mx-auto">
      <div className="flex flex-col gap-6">
        <section className="space-y-6">
          <div className="flex flex-col md:flex-row gap-4 items-center">
            <Avatar className="w-20 h-20">
              <AvatarImage src="/placeholder.svg?height=80&width=80" alt="User" />
              <AvatarFallback>SA</AvatarFallback>
            </Avatar>
            <div className="flex-1 text-center md:text-left">
              <h1 className="text-2xl font-bold tracking-tight">Sarah Anderson</h1>
            </div>
            <Button variant="outline" size="sm" className="gap-2">
              <SettingsIcon className="h-4 w-4" />
              <span className="hidden md:inline">Settings</span>
            </Button>
          </div>

          <Tabs defaultValue="profile" className="w-full">
            <TabsList className="grid w-full grid-cols-3">
              <TabsTrigger value="profile">Profile</TabsTrigger>
              <TabsTrigger value="achievements">Achievements</TabsTrigger>
              <TabsTrigger value="settings">Settings</TabsTrigger>
            </TabsList>

            <TabsContent value="profile" className="space-y-4">
              <Card>
                <CardHeader className="flex flex-row items-center justify-between">
                  <div>
                    <CardTitle>Personal Information</CardTitle>
                    <CardDescription>Your personal details</CardDescription>
                  </div>
                  {!isEditing ? (
                    <Button variant="outline" size="sm" onClick={() => setIsEditing(true)}>
                      <EditIcon className="h-4 w-4 mr-2" />
                      Edit
                    </Button>
                  ) : (
                    <div className="flex gap-2">
                      <Button variant="outline" size="sm" onClick={() => setIsEditing(false)}>
                        <XIcon className="h-4 w-4 mr-2" />
                        Cancel
                      </Button>
                      <Button size="sm" onClick={() => setIsEditing(false)}>
                        <CheckIcon className="h-4 w-4 mr-2" />
                        Save
                      </Button>
                    </div>
                  )}
                </CardHeader>
                <CardContent className="space-y-4">
                  {isEditing ? (
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-2">
                        <Label htmlFor="name">Full Name</Label>
                        <Input id="name" defaultValue={userData.name} />
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="email">Email</Label>
                        <Input id="email" defaultValue={userData.email} type="email" />
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="height">Height (cm)</Label>
                        <Input id="height" defaultValue={userData.height} type="number" />
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="weight">Weight (kg)</Label>
                        <Input id="weight" defaultValue={userData.weight} type="number" />
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="birthdate">Date of Birth</Label>
                        <Input id="birthdate" defaultValue={userData.birthdate} type="date" />
                      </div>
                    </div>
                  ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-1">
                        <p className="text-sm font-medium text-muted-foreground">Full Name</p>
                        <p>{userData.name}</p>
                      </div>
                      <div className="space-y-1">
                        <p className="text-sm font-medium text-muted-foreground">Email</p>
                        <p>{userData.email}</p>
                      </div>
                      <div className="space-y-1">
                        <p className="text-sm font-medium text-muted-foreground">Height</p>
                        <p>{userData.height} cm</p>
                      </div>
                      <div className="space-y-1">
                        <p className="text-sm font-medium text-muted-foreground">Weight</p>
                        <p>{userData.weight} kg</p>
                      </div>
                      <div className="space-y-1">
                        <p className="text-sm font-medium text-muted-foreground">Date of Birth</p>
                        <p>{userData.birthdate}</p>
                      </div>
                    </div>
                  )}
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="flex flex-row items-center justify-between">
                  <div>
                    <CardTitle>Fitness Goals</CardTitle>
                    <CardDescription>Your fitness goals and progress</CardDescription>
                  </div>
                  {!isEditing ? (
                    <Button variant="outline" size="sm" onClick={() => setIsEditing(true)}>
                      <EditIcon className="h-4 w-4 mr-2" />
                      Edit
                    </Button>
                  ) : null}
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="space-y-2">
                    <div className="flex justify-between">
                      <Label>Weekly Workout Goal</Label>
                      <span className="text-sm text-muted-foreground">4/5 days</span>
                    </div>
                    <Progress value={80} className="bg-muted h-2" />
                  </div>

                  {isEditing ? (
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-2">
                        <Label htmlFor="goal">Primary Goal</Label>
                        <select
                          id="goal"
                          className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                        >
                          <option selected={userData.goal === "Stay fit"}>Stay fit</option>
                          <option selected={userData.goal === "Lose weight"}>Lose weight</option>
                          <option selected={userData.goal === "Build strength"}>Build strength</option>
                          <option selected={userData.goal === "Improve flexibility"}>Improve flexibility</option>
                        </select>
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="workouts-per-week">Workouts Per Week</Label>
                        <select
                          id="workouts-per-week"
                          className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                        >
                          <option selected={userData.workoutsPerWeek === "3 days"}>3 days</option>
                          <option selected={userData.workoutsPerWeek === "4 days"}>4 days</option>
                          <option selected={userData.workoutsPerWeek === "5 days"}>5 days</option>
                          <option selected={userData.workoutsPerWeek === "6 days"}>6 days</option>
                          <option selected={userData.workoutsPerWeek === "7 days"}>7 days</option>
                        </select>
                      </div>
                    </div>
                  ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-1">
                        <p className="text-sm font-medium text-muted-foreground">Primary Goal</p>
                        <p>{userData.goal}</p>
                      </div>
                      <div className="space-y-1">
                        <p className="text-sm font-medium text-muted-foreground">Workouts Per Week</p>
                        <p>{userData.workoutsPerWeek}</p>
                      </div>
                    </div>
                  )}
                </CardContent>
                {isEditing && (
                  <CardFooter>
                    <Button onClick={() => setIsEditing(false)}>Update Goals</Button>
                  </CardFooter>
                )}
              </Card>
            </TabsContent>

            <TabsContent value="achievements" className="space-y-4">
              <Card>
                <CardHeader>
                  <CardTitle>Your Achievements</CardTitle>
                  <CardDescription>Track your progress and milestones</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                    <div className="flex flex-col items-center p-4 border rounded-lg">
                      <div className="bg-pink-100 rounded-full p-3 mb-2">
                        <TrophyIcon className="h-6 w-6 text-primary" />
                      </div>
                      <h3 className="font-medium text-center">5-Day Streak</h3>
                      <p className="text-xs text-muted-foreground text-center">Completed workouts 5 days in a row</p>
                    </div>
                    <div className="flex flex-col items-center p-4 border rounded-lg">
                      <div className="bg-pink-100 rounded-full p-3 mb-2">
                        <TrophyIcon className="h-6 w-6 text-primary" />
                      </div>
                      <h3 className="font-medium text-center">First Milestone</h3>
                      <p className="text-xs text-muted-foreground text-center">Completed 10 workouts</p>
                    </div>
                    <div className="flex flex-col items-center p-4 border rounded-lg bg-muted/50">
                      <div className="bg-muted rounded-full p-3 mb-2">
                        <TrophyIcon className="h-6 w-6 text-muted-foreground" />
                      </div>
                      <h3 className="font-medium text-center">30-Day Streak</h3>
                      <p className="text-xs text-muted-foreground text-center">Complete workouts for 30 days</p>
                      <Badge variant="outline" className="mt-2">
                        In Progress
                      </Badge>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </TabsContent>

            <TabsContent value="settings" className="space-y-4">
              <Card>
                <CardHeader>
                  <CardTitle>Notifications</CardTitle>
                  <CardDescription>Manage your notification preferences</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="flex items-center justify-between">
                    <div className="space-y-0.5">
                      <Label>Workout Reminders</Label>
                      <p className="text-sm text-muted-foreground">Receive daily workout reminders</p>
                    </div>
                    <Switch defaultChecked />
                  </div>
                  <div className="flex items-center justify-between">
                    <div className="space-y-0.5">
                      <Label>Cycle Notifications</Label>
                      <p className="text-sm text-muted-foreground">Get notified about your cycle phases</p>
                    </div>
                    <Switch defaultChecked />
                  </div>
                  <div className="flex items-center justify-between">
                    <div className="space-y-0.5">
                      <Label>Achievement Alerts</Label>
                      <p className="text-sm text-muted-foreground">Receive notifications when you earn achievements</p>
                    </div>
                    <Switch defaultChecked />
                  </div>
                  <div className="flex items-center justify-between">
                    <div className="space-y-0.5">
                      <Label>Weekly Reports</Label>
                      <p className="text-sm text-muted-foreground">Get weekly summaries of your progress</p>
                    </div>
                    <Switch />
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Account Settings</CardTitle>
                  <CardDescription>Manage your account preferences</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="password">Change Password</Label>
                    <Input id="password" type="password" placeholder="New password" />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="confirm-password">Confirm Password</Label>
                    <Input id="confirm-password" type="password" placeholder="Confirm new password" />
                  </div>
                  <Button className="w-full">Update Password</Button>

                  <div className="pt-4">
                    <Link href="/auth/login">
                      <Button variant="destructive" className="w-full">
                        <LogOutIcon className="h-4 w-4 mr-2" />
                        Sign Out
                      </Button>
                    </Link>
                  </div>
                </CardContent>
              </Card>
            </TabsContent>
          </Tabs>
        </section>
      </div>
    </div>
  )
}

