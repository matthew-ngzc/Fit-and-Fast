"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Progress } from "@/components/ui/progress";
import { Switch } from "@/components/ui/switch";
import {
  EditIcon,
  LogOutIcon,
  SettingsIcon,
  TrophyIcon,
  CheckIcon,
  XIcon,
  User,
  UserCircle2,
  UserCheck,
} from "lucide-react";
import Link from "next/link";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  DialogClose,
} from "@/components/ui/dialog";

const fetchUserData = async () => {
  const response = await fetch("/api/user"); // Replace with your API endpoint
  if (!response.ok) {
    throw new Error("Failed to fetch user data");
  }
  return response.json();
};

export default function ProfilePage() {
  // Sample user data - in a real app this would come from a database
  const userData = {
    name: "Sarah Anderson",
    email: "sarah@gmail.com",
    height: "165",
    weight: "58",
    birthdate: "1990-05-15",
    goal: "Stay fit",
    workoutsPerWeek: "5 days",
    avatar: "/avatars/avatar.png",
  };

  // Achievements data (sample)
  const achievements = [
    {
      title: "5-Day Streak",
      description: "Completed workouts 5 days in a row",
      isCompleted: true,
    },
    {
      title: "First Milestone",
      description: "Completed 10 workouts",
      isCompleted: true,
    },
    {
      title: "30-Day Streak",
      description: "Complete workouts for 30 days",
      isCompleted: false,
    },
  ];

  const [isEditing, setIsEditing] = useState(false);
  const [selectedAvatar, setSelectedAvatar] = useState(userData.avatar);

  // Predefined avatars
  const avatars = [
    { id: 0, src: "/avatars/avatar.png", alt: "Default Avatar" },
    { id: 1, src: "/avatars/avatar1.png", alt: "Avatar 1" },
    { id: 2, src: "/avatars/avatar2.png", alt: "Avatar 2" },
    { id: 3, src: "/avatars/avatar3.png", alt: "Avatar 3" },
    { id: 4, src: "/avatars/avatar4.png", alt: "Avatar 4" },
    { id: 5, src: "/avatars/avatar5.png", alt: "Avatar 5" },
    { id: 6, src: "/avatars/avatar6.png", alt: "Avatar 6" },
    { id: 7, src: "/avatars/avatar7.png", alt: "Avatar 7" },
    { id: 8, src: "/avatars/avatar8.png", alt: "Avatar 8" },
    { id: 9, src: "/avatars/avatar9.png", alt: "Avatar 9" },
  ];

  {
    /* Fetch user data api */
  }
  {
    /* Get user data api */
  }
  {
    /* Update user data api */
  }
  {
    /* Update user avatar api */
  }
  {
    /* Update user primary goal api */
  }
  {
    /* Update user goal to exercise per week api */
  }
  {
    /* Get user total workout days for the week Mon-Fri*/
  }

  return (
    <div className="container px-4 py-6 md:py-10 pb-20 max-w-5xl mx-auto">
      <div className="flex flex-col gap-6">
        <section className="space-y-6">
          <div className="flex flex-col md:flex-row gap-4 items-center">
            <Dialog>
              <DialogTrigger asChild>
                <div className="relative cursor-pointer group">
                  <Avatar className="w-20 h-20 border">
                    <AvatarImage src={selectedAvatar} alt="User" />
                    <AvatarFallback>SA</AvatarFallback>
                  </Avatar>
                  <div className="absolute inset-0 bg-black/30 rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                    <EditIcon className="h-6 w-6 text-white" />
                  </div>
                </div>
              </DialogTrigger>
              <DialogContent className="sm:max-w-md">
                <DialogHeader>
                  <DialogTitle>Choose an avatar</DialogTitle>
                  <DialogDescription>
                    Select one of the predefined avatars for your profile.
                  </DialogDescription>
                </DialogHeader>
                <div className="grid grid-cols-3 gap-4 py-4">
                  {avatars.map((avatar) => (
                    <div
                      key={avatar.id}
                      className={`relative cursor-pointer rounded-lg p-2 transition-all ${
                        selectedAvatar === avatar.src
                          ? "bg-primary/10 ring-2 ring-primary"
                          : "hover:bg-muted"
                      }`}
                      onClick={() => setSelectedAvatar(avatar.src)}
                    >
                      <Avatar className="w-16 h-16 mx-auto">
                        <AvatarImage src={avatar.src} alt={avatar.alt} />
                        <AvatarFallback>
                          {avatar.id === 1 ? (
                            <User />
                          ) : avatar.id === 2 ? (
                            <UserCircle2 />
                          ) : (
                            <UserCheck />
                          )}
                        </AvatarFallback>
                      </Avatar>
                      {selectedAvatar === avatar.src && (
                        <div className="absolute top-1 right-1 bg-primary rounded-full p-0.5">
                          <CheckIcon className="h-3 w-3 text-primary-foreground" />
                        </div>
                      )}
                    </div>
                  ))}
                </div>
                <DialogClose asChild>
                  <Button type="button">Save Avatar</Button>
                </DialogClose>
              </DialogContent>
            </Dialog>
            <div className="flex-1 text-center md:text-left">
              <h1 className="text-2xl font-bold tracking-tight">
                Sarah Anderson
              </h1>
            </div>
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
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setIsEditing(true)}
                    >
                      <EditIcon className="h-4 w-4 mr-2" />
                      Edit
                    </Button>
                  ) : (
                    <div className="flex gap-2">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => setIsEditing(false)}
                      >
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
                        <Input
                          id="email"
                          defaultValue={userData.email}
                          type="email"
                        />
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="height">Height (cm)</Label>
                        <Input
                          id="height"
                          defaultValue={userData.height}
                          type="number"
                        />
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="weight">Weight (kg)</Label>
                        <Input
                          id="weight"
                          defaultValue={userData.weight}
                          type="number"
                        />
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="birthdate">Date of Birth</Label>
                        <Input
                          id="birthdate"
                          defaultValue={userData.birthdate}
                          type="date"
                        />
                      </div>
                    </div>
                  ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-1">
                        <p className="text-sm font-medium text-muted-foreground">
                          Full Name
                        </p>
                        <p>{userData.name}</p>
                      </div>
                      <div className="space-y-1">
                        <p className="text-sm font-medium text-muted-foreground">
                          Email
                        </p>
                        <p>{userData.email}</p>
                      </div>
                      <div className="space-y-1">
                        <p className="text-sm font-medium text-muted-foreground">
                          Height
                        </p>
                        <p>{userData.height} cm</p>
                      </div>
                      <div className="space-y-1">
                        <p className="text-sm font-medium text-muted-foreground">
                          Weight
                        </p>
                        <p>{userData.weight} kg</p>
                      </div>
                      <div className="space-y-1">
                        <p className="text-sm font-medium text-muted-foreground">
                          Date of Birth
                        </p>
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
                    <CardDescription>
                      Your fitness goals and progress
                    </CardDescription>
                  </div>
                  {!isEditing ? (
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setIsEditing(true)}
                    >
                      <EditIcon className="h-4 w-4 mr-2" />
                      Edit
                    </Button>
                  ) : null}
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="space-y-2">
                    <div className="flex justify-between">
                      <Label>Weekly Workout Goal</Label>
                      <span className="text-sm text-muted-foreground">
                        4/5 days
                      </span>
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
                          <option selected={userData.goal === "Stay fit"}>
                            Stay fit
                          </option>
                          <option selected={userData.goal === "Lose weight"}>
                            Lose weight
                          </option>
                          <option selected={userData.goal === "Build strength"}>
                            Build strength
                          </option>
                          <option
                            selected={userData.goal === "Improve flexibility"}
                          >
                            Improve flexibility
                          </option>
                        </select>
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="workouts-per-week">
                          Workouts Per Week
                        </Label>
                        <select
                          id="workouts-per-week"
                          className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                        >
                          <option
                            selected={userData.workoutsPerWeek === "3 days"}
                          >
                            3 days
                          </option>
                          <option
                            selected={userData.workoutsPerWeek === "4 days"}
                          >
                            4 days
                          </option>
                          <option
                            selected={userData.workoutsPerWeek === "5 days"}
                          >
                            5 days
                          </option>
                          <option
                            selected={userData.workoutsPerWeek === "6 days"}
                          >
                            6 days
                          </option>
                          <option
                            selected={userData.workoutsPerWeek === "7 days"}
                          >
                            7 days
                          </option>
                        </select>
                      </div>
                    </div>
                  ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-1">
                        <p className="text-sm font-medium text-muted-foreground">
                          Primary Goal
                        </p>
                        <p>{userData.goal}</p>
                      </div>
                      <div className="space-y-1">
                        <p className="text-sm font-medium text-muted-foreground">
                          Workouts Per Week
                        </p>
                        <p>{userData.workoutsPerWeek}</p>
                      </div>
                    </div>
                  )}
                </CardContent>
                {isEditing && (
                  <CardFooter>
                    <Button onClick={() => setIsEditing(false)}>
                      Update Goals
                    </Button>
                  </CardFooter>
                )}
              </Card>
            </TabsContent>

            <TabsContent value="achievements" className="space-y-4">
              <Card>
                <CardHeader>
                  <CardTitle>Your Achievements</CardTitle>
                  <CardDescription>
                    Track your progress and milestones
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                    {achievements.map((achievement, index) => (
                      <div
                        key={index}
                        className="flex flex-col items-center p-4 border rounded-lg"
                      >
                        <div
                          className={`bg-muted rounded-full p-3 mb-2`}
                        >
                          <TrophyIcon
                            className={`h-6 w-6 ${
                              achievement.isCompleted
                                ? "text-primary"
                                : "text-muted-foreground"
                            }`}
                          />
                        </div>
                        <h3 className="font-medium text-center">
                          {achievement.title}
                        </h3>
                        <p className="text-xs text-muted-foreground text-center">
                          {achievement.description}
                        </p>
                        {!achievement.isCompleted && (
                          <Badge variant="outline" className="mt-2">
                            In Progress
                          </Badge>
                        )}
                        {achievement.isCompleted && (
                          <CheckIcon className="mt-2 h-5 w-5 text-primary" />
                        )}
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            </TabsContent>

            <TabsContent value="settings" className="space-y-4">
              <Card>
                <CardHeader>
                  <CardTitle>Notifications</CardTitle>
                  <CardDescription>
                    Manage your notification preferences
                  </CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="flex items-center justify-between">
                    <div className="space-y-0.5">
                      <Label>Workout Reminders</Label>
                      <p className="text-sm text-muted-foreground">
                        Receive daily workout reminders
                      </p>
                    </div>
                    <Switch defaultChecked />
                  </div>
                  <div className="flex items-center justify-between">
                    <div className="space-y-0.5">
                      <Label>Cycle Notifications</Label>
                      <p className="text-sm text-muted-foreground">
                        Get notified about your cycle phases
                      </p>
                    </div>
                    <Switch defaultChecked />
                  </div>
                  <div className="flex items-center justify-between">
                    <div className="space-y-0.5">
                      <Label>Achievement Alerts</Label>
                      <p className="text-sm text-muted-foreground">
                        Receive notifications when you earn achievements
                      </p>
                    </div>
                    <Switch defaultChecked />
                  </div>
                  <div className="flex items-center justify-between">
                    <div className="space-y-0.5">
                      <Label>Weekly Reports</Label>
                      <p className="text-sm text-muted-foreground">
                        Get weekly summaries of your progress
                      </p>
                    </div>
                    <Switch />
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Account Settings</CardTitle>
                  <CardDescription>
                    Manage your account preferences
                  </CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="password">Change Password</Label>
                    <Input
                      id="password"
                      type="password"
                      placeholder="New password"
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="confirm-password">Confirm Password</Label>
                    <Input
                      id="confirm-password"
                      type="password"
                      placeholder="Confirm new password"
                    />
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
  );
}
