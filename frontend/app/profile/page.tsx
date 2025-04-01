"use client";

import type React from "react";

import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

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
  TrophyIcon,
  CheckIcon,
  XIcon,
  User,
  UserCircle2,
  UserCheck,
} from "lucide-react";

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  DialogClose,
  DialogFooter,
} from "@/components/ui/dialog";
import { useRouter } from "next/navigation";
import config from "@/config";
import axios from "axios";

export default function ProfilePage() {
  const router = useRouter();
  const token = localStorage.getItem("token");
  const [userData, setUserData] = useState({
    username: "",
    email: "",
    height: "",
    weight: "",
    dob: "",
    workoutDays: "",
    workoutGoal: "",
    avatar: "",
  });

  useEffect(() => {
    if (!token) {
      console.error("No token found. Please log in.");
      return;
    }

    const fetchUserData = async () => {
      try {
        const response = await axios.get(`${config.PROFILE_URL}/`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setUserData(response.data);
      } catch (error) {
        console.error("Failed to fetch user data:", error);
      }
    };
    fetchUserData();
  }, [token]);

  const [userFormData, setUserFormData] = useState({
    username: userData.username || "",
    email: userData.email || "",
    height: userData.height || "",
    weight: userData.weight || "",
    dob: userData.dob ? new Date(userData.dob).toISOString().split("T")[0] : "",
  });

  const [userGoalData, setUserGoalData] = useState({
    workoutGoal: userData.workoutGoal || "",
    workoutDaysPerWeekGoal: userData.workoutDays || 0,
  });

  useEffect(() => {
    if (userData) {
      setUserFormData({
        username: userData.username || "",
        email: userData.email || "",
        height: userData.height || "",
        weight: userData.weight || "",
        dob: userData.dob
          ? new Date(userData.dob).toISOString().split("T")[0]
          : "",
      });

      setUserGoalData({
        workoutGoal: userData.workoutGoal || "",
        workoutDaysPerWeekGoal: userData.workoutDays ? userData.workoutDays : 0,
      });
    }
  }, [userData]);

  const handleSignOut = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userId");

    router.push("/auth/login");
  };

  const handleUserFormInputChange = (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    const { id, value } = e.target;
    setUserFormData((prevData) => ({
      ...prevData,
      [id]: value,
    }));
  };

  const handleUpdateUserPersonalDetails = async () => {
    try {
      const response = await fetch(`${config.PROFILE_URL}/`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(userFormData),
      });

      if (response.ok) {
        alert("User details updated successfully");
        setIsEditingUserDetails(false);

        const fetchUserData = async () => {
          try {
            const response = await axios.get(`${config.PROFILE_URL}/`, {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            });
            setUserData(response.data);
          } catch (error) {
            console.error("Failed to fetch user data:", error);
          }
        };

        fetchUserData();
      } else {
        alert("Failed to update user details");
      }
    } catch (error) {
      console.error("Error updating user details:", error);
      alert("An error occurred while updating the details");
    }
  };

  const handleUpdateUserGoal = async () => {
    try {
      const response = await fetch(`${config.PROFILE_URL}/goals`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(userGoalData),
      });

      if (response.ok) {
        alert("User goals updated successfully");
        setIsEditing(false);

        const fetchUserData = async () => {
          try {
            const response = await axios.get(`${config.PROFILE_URL}/`, {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            });
            setUserData(response.data);
          } catch (error) {
            console.error("Failed to fetch user goals:", error);
          }
        };

        fetchUserData();
      } else {
        alert("Failed to update user goals");
      }
    } catch (error) {
      console.error("Error updating user goals:", error);
      alert("An error occurred while updating the goals");
    }
  };

  const goalDisplayMap: { [key: string]: string } = {
    GENERAL: "General Fitness",
    WEIGHT_LOSS: "Weight Loss",
    STRENGTH_BUILDING: "Strength Building",
    FLEXIBILITY: "Flexibility",
    STRESS_RELIEF: "Stress Relief",
    PRENATAL: "Prenatal",
    POST_PREGNANCY_RECOVERY: "Post-Pregnancy Recovery",
  };

  const [isEditing, setIsEditing] = useState(false);
  const [isEditingUserDetails, setIsEditingUserDetails] = useState(false);
  const [selectedAvatar, setSelectedAvatar] = useState(userData.avatar);

  const [cycleData, setCycleData] = useState({
    cycleLength: 28,
    periodLength: 5,
    lastPeriodStartDate: new Date(),
  });

  const [periodDialogOpen, setPeriodDialogOpen] = useState(false);

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

  const saveAvatar = async () => {
    if (!selectedAvatar) return;

    const token = localStorage.getItem("token");

    if (!token) {
      console.error("No token found! Please log in.");
      return;
    }

    try {
      const response = await axios.put(
        `${config.PROFILE_URL}/avatar`,

        { avatarLink: selectedAvatar },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response.status === 200) {
        const fetchUserData = async () => {
          try {
            const response = await axios.get(`${config.PROFILE_URL}/`, {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            });
            setUserData(response.data);
          } catch (error) {
            console.error("Failed to fetch user goals:", error);
          }
        };

        fetchUserData();
      }
    } catch (err) {
      console.error("Error saving avatar:", err);
    }
  };

  const [totalWorkouts, setTotalWorkouts] = useState("");

  useEffect(() => {
    const fetchTotalWorkouts = async () => {
      const token = localStorage.getItem("token");

      if (!token) {
        console.error("No token found! Please log in.");
        return;
      }

      try {
        const response = await axios.get(
          `${config.PROFILE_URL}/weekly-workouts`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (response.status === 200) {
          setTotalWorkouts(response.data.totalWorkouts);
        }
      } catch (error) {
        console.error("Error fetching weekly workouts:", error);
      }
    };

    fetchTotalWorkouts();
  }, []);

  const [achievements, setAchievements] = useState<
    { title: string; description: string; completed: boolean }[]
  >([]);

  useEffect(() => {
    const fetchAchievements = async () => {
      const token = localStorage.getItem("token");

      if (!token) {
        console.error("No token found! Please log in.");
        return;
      }

      try {
        const response = await axios.get(`${config.PROFILE_URL}/achievements`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (response.status === 200) {
          setAchievements(response.data);
        }
      } catch (error) {
        console.error("Error fetching achievements:", error);
      }
    };

    fetchAchievements();
  }, []);

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
      }
    }

    fetchCycleData();
  }, []);

  const [updatedCycleData, setUpdatedCycleData] = useState({
    cycleLength: cycleData.cycleLength || "",
    periodLength: cycleData.periodLength || "",
    lastPeriodStartDate: cycleData.lastPeriodStartDate
      ? cycleData.lastPeriodStartDate.toISOString().split("T")[0]
      : "",
  });

  async function updateCycleData() {
    try {
      const token = localStorage.getItem("token"); // Retrieve token from local storage
      const filteredData = {
        cycleLength: updatedCycleData.cycleLength,
        periodLength: updatedCycleData.periodLength,
        lastPeriodStartDate: updatedCycleData.lastPeriodStartDate,
      };

      const response = await fetch(`${config.CALENDAR_URL}/update-cycle`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`, // Include token in request
        },
        body: JSON.stringify(filteredData),
      });

      if (!response.ok) {
        throw new Error("Failed to update cycle data");
      }
      setPeriodDialogOpen(false);

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
        }
      }

      fetchCycleData();
    } catch (error) {
      console.error("Error updating cycle data:", error);
    }
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
                    <AvatarImage src={userData.avatar} alt="User" />
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
                  <Button type="button" onClick={saveAvatar}>
                    Save Avatar
                  </Button>
                </DialogClose>
              </DialogContent>
            </Dialog>
            <div className="flex-1 text-center md:text-left">
              <h1 className="text-2xl font-bold tracking-tight">
                {userData.username}
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
                  {!isEditingUserDetails ? (
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setIsEditingUserDetails(true)}
                    >
                      <EditIcon className="h-4 w-4 mr-2" />
                      Edit
                    </Button>
                  ) : (
                    <div className="flex gap-2">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => setIsEditingUserDetails(false)}
                      >
                        <XIcon className="h-4 w-4 mr-2" />
                        Cancel
                      </Button>
                      <Button
                        size="sm"
                        onClick={handleUpdateUserPersonalDetails}
                      >
                        <CheckIcon className="h-4 w-4 mr-2" />
                        Save
                      </Button>
                    </div>
                  )}
                </CardHeader>
                <CardContent className="space-y-4">
                  {isEditingUserDetails ? (
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-2">
                        <Label htmlFor="name">username</Label>
                        <Input
                          id="username"
                          value={userFormData.username}
                          onChange={handleUserFormInputChange}
                        />
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="email">Email</Label>
                        <Input
                          id="email"
                          type="email"
                          value={userFormData.email}
                          onChange={handleUserFormInputChange}
                        />
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="height">Height (cm)</Label>
                        <Input
                          type="number"
                          value={userFormData.height}
                          onChange={handleUserFormInputChange}
                        />
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="weight">Weight (kg)</Label>
                        <Input
                          id="weight"
                          type="number"
                          value={userFormData.weight}
                          onChange={handleUserFormInputChange}
                        />
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="birthdate">Date of Birth</Label>
                        <Input
                          id="dob"
                          type="date"
                          value={userFormData.dob}
                          onChange={handleUserFormInputChange}
                        />
                      </div>
                    </div>
                  ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-1">
                        <p className="text-sm font-medium text-muted-foreground">
                          Username
                        </p>
                        <p>{userData.username}</p>
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
                        <p>
                          {userData.dob
                            ? new Date(userData.dob).toISOString().split("T")[0]
                            : "N/A"}
                        </p>
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
                      <Button size="sm" onClick={handleUpdateUserGoal}>
                        <CheckIcon className="h-4 w-4 mr-2" />
                        Save
                      </Button>
                    </div>
                  )}
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="space-y-2">
                    <div className="flex justify-between">
                      <Label>Weekly Workout Goal</Label>
                      <span className="text-sm text-muted-foreground">
                        {totalWorkouts}/{userData.workoutDays} days
                      </span>
                    </div>
                    <Progress
                      value={
                        userData.workoutDays
                          ? Math.min(
                              (Number(totalWorkouts) /
                                Number(userData.workoutDays)) *
                                100,
                              100
                            )
                          : 0
                      }
                      className="bg-muted h-2"
                    />
                  </div>

                  {isEditing ? (
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-2">
                        <Label htmlFor="goal">Primary Goal</Label>
                        <Select
                          value={goalDisplayMap[userGoalData.workoutGoal] || ""}
                          onValueChange={(value) => {
                            // Find the key in goalDisplayMap that matches the selected value
                            const goalKey = Object.keys(goalDisplayMap).find(
                              (key) => goalDisplayMap[key] === value
                            );

                            setUserGoalData((prevData) => ({
                              ...prevData,
                              workoutGoal: goalKey || "", // Store the backend key
                            }));
                          }}
                        >
                          <SelectTrigger className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2">
                            <SelectValue placeholder="Select your primary goal" />
                          </SelectTrigger>
                          <SelectContent>
                            {Object.entries(goalDisplayMap).map(
                              ([key, value]) => (
                                <SelectItem key={key} value={value}>
                                  {value}
                                </SelectItem>
                              )
                            )}
                          </SelectContent>
                        </Select>
                      </div>

                      <div className="space-y-2">
                        <Label htmlFor="workouts-per-week">
                          Workouts Per Week
                        </Label>
                        <Select
                          value={String(userGoalData.workoutDaysPerWeekGoal)}
                          onValueChange={(value) => {
                            setUserGoalData((prevData) => ({
                              ...prevData,
                              workoutDaysPerWeekGoal: Number(value),
                            }));
                          }}
                        >
                          <SelectTrigger className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2">
                            <SelectValue placeholder="Select number of days" />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="3">3 days</SelectItem>
                            <SelectItem value="4">4 days</SelectItem>
                            <SelectItem value="5">5 days</SelectItem>
                            <SelectItem value="6">6 days</SelectItem>
                            <SelectItem value="7">7 days</SelectItem>
                          </SelectContent>
                        </Select>
                      </div>
                    </div>
                  ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-1">
                        <p className="text-sm font-medium text-muted-foreground">
                          Primary Goal
                        </p>
                        <p>
                          {goalDisplayMap[userData.workoutGoal] ||
                            userData.workoutGoal}
                        </p>
                      </div>
                      <div className="space-y-1">
                        <p className="text-sm font-medium text-muted-foreground">
                          Workouts Per Week
                        </p>
                        <p>{userData.workoutDays} days</p>
                      </div>
                    </div>
                  )}
                </CardContent>
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
                    {achievements?.length > 0 ? (
                      achievements.map((achievement, index) => (
                        <div
                          key={index}
                          className="flex flex-col items-center p-4 border rounded-lg"
                        >
                          <div className="bg-muted rounded-full p-3 mb-2">
                            <TrophyIcon
                              className={`h-6 w-6 ${
                                achievement.completed
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
                          {!achievement.completed && (
                            <Badge variant="outline" className="mt-2">
                              In Progress
                            </Badge>
                          )}
                          {achievement.completed && (
                            <CheckIcon className="mt-2 h-5 w-5 text-primary" />
                          )}
                        </div>
                      ))
                    ) : (
                      <p className="text-center text-muted-foreground">
                        No achievements found.
                      </p>
                    )}
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
                <CardHeader className="flex flex-row items-center justify-between">
                  <div>
                    <CardTitle>Period Information</CardTitle>
                    <CardDescription>
                      Manage your period cycle information
                    </CardDescription>
                  </div>
                  <Dialog
                    open={periodDialogOpen}
                    onOpenChange={setPeriodDialogOpen}
                  >
                    <DialogTrigger asChild>
                      <Button variant="outline" size="sm">
                        <EditIcon className="h-4 w-4 mr-2" />
                        Edit Period
                      </Button>
                    </DialogTrigger>
                    <DialogContent>
                      <DialogHeader>
                        <DialogTitle>Edit Period Information</DialogTitle>
                        <DialogDescription>
                          Update your period details to get more accurate
                          predictions
                        </DialogDescription>
                      </DialogHeader>
                      <div className="grid gap-4 py-4">
                        <div className="grid grid-cols-2 gap-4">
                          <div className="grid gap-2">
                            <Label htmlFor="cycle-length">Cycle Length</Label>
                            <Input
                              id="cycle-length"
                              defaultValue={updatedCycleData.cycleLength.toString()}
                              onChange={(e) =>
                                setUpdatedCycleData((prev) => ({
                                  ...prev,
                                  cycleLength: Number.parseInt(e.target.value),
                                }))
                              }
                            />
                          </div>
                          <div className="grid gap-2">
                            <Label htmlFor="period-length">Period Length</Label>
                            <Input
                              id="period-length"
                              defaultValue={updatedCycleData.periodLength.toString()}
                              onChange={(e) =>
                                setUpdatedCycleData((prev) => ({
                                  ...prev,
                                  periodLength: Number.parseInt(e.target.value),
                                }))
                              }
                            />
                          </div>
                        </div>
                        <div className="grid gap-2">
                          <Label htmlFor="last-period">
                            Last Period Start Date
                          </Label>
                          <div className="flex gap-2">
                            <Input
                              id="last-period"
                              defaultValue={
                                updatedCycleData.lastPeriodStartDate
                              }
                              type="date"
                              onChange={(e) =>
                                setUpdatedCycleData((prev) => ({
                                  ...prev,
                                  lastPeriodStartDate: e.target.value,
                                }))
                              }
                            />
                          </div>
                        </div>
                      </div>
                      <DialogFooter>
                        <Button type="submit" onClick={() => updateCycleData()}>
                          Save changes
                        </Button>
                      </DialogFooter>
                    </DialogContent>
                  </Dialog>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div className="space-y-1">
                      <p className="text-sm font-medium text-muted-foreground">
                        Cycle Length
                      </p>
                      <p>{cycleData?.cycleLength ?? "N/A"} days</p>
                    </div>
                    <div className="space-y-1">
                      <p className="text-sm font-medium text-muted-foreground">
                        Period Length
                      </p>
                      <p>{cycleData?.periodLength ?? "N/A"} days</p>
                    </div>
                    <div className="space-y-1">
                      <p className="text-sm font-medium text-muted-foreground">
                        Last Period Start Date
                      </p>
                      <p>
                        {cycleData?.lastPeriodStartDate
                          ? cycleData.lastPeriodStartDate
                              .toISOString()
                              .split("T")[0]
                          : "N/A"}
                      </p>
                    </div>
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
                  <div className="pt-4">
                    <Button
                      variant="destructive"
                      className="w-full"
                      onClick={handleSignOut}
                    >
                      <LogOutIcon className="h-4 w-4 mr-2" />
                      Sign Out
                    </Button>
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
