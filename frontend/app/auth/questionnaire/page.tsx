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
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Progress } from "@/components/ui/progress";
import { ArrowLeft, ArrowRight } from "lucide-react";

export default function QuestionnairePage() {
  const [step, setStep] = useState(1);
  const totalSteps = 3;

  const [formData, setFormData] = useState({
    username: "",
    height: "",
    weight: "",
    birthdate: "",
    fitnessLevel: "beginner",
    menstrualCramps: "no",
    pregnancyStatus: "no",
    cycleBasedRecommendations: "yes",
    workoutType: "high-energy",
    workoutDays: "3",
    fitnessGoal: "general",
  });

  const nextStep = () => {
    if (step < totalSteps) {
      setStep(step + 1);
      window.scrollTo(0, 0);
    }
  };

  const prevStep = () => {
    if (step > 1) {
      setStep(step - 1);
      window.scrollTo(0, 0);
    }
  };

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>,
    isSelect: boolean = false
  ) => {
    const { name, value } = e.target;

    if (isSelect) {
      // Handle the change for Select components
      setFormData({
        ...formData,
        [name]: value,
      });
    } else {
      // Handle regular input changes
      setFormData({
        ...formData,
        [name]: value,
      });
    }
  };

  const handleSubmit = async () => {
    // Here, you can send the data to your API
    try {
      const response = await fetch("/api/submit", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        throw new Error("Failed to submit form");
      }

      // Handle success
      alert("Form submitted successfully!");
      window.location.href = "/";
    } catch (error) {
      // Handle error
      console.error(error);
      alert("Error submitting form. Please try again.");
    }
  };

  return (
    <div className="container flex min-h-screen w-screen flex-col items-center justify-center py-10">
      <div className="mx-auto flex w-full flex-col justify-center space-y-6 max-w-md">
        <div className="flex flex-col space-y-2 text-center">
          <div className="flex justify-center">
            <div className="bg-pink-100 p-2 rounded-full">
              <img src="/icon.png" alt="Fit&Fast" className="h-7 w-7" />
            </div>
          </div>
          <h1 className="text-2xl font-semibold tracking-tight">
            Let's personalize your experience
          </h1>
          <p className="text-sm text-muted-foreground">
            Tell us about yourself so we can customize your workouts
          </p>
        </div>

        <div className="space-y-2">
          <div className="flex justify-between text-sm">
            <span>
              Step {step} of {totalSteps}
            </span>
            <span>{getStepTitle(step)}</span>
          </div>
          <Progress
            value={(step / totalSteps) * 100}
            className="h-2 transition-all duration-500 ease-in-out"
          />
        </div>

        {step === 1 && (
          <Card className="border-none shadow-md bg-gradient-to-b from-white to-pink-50">
            <CardHeader>
              <CardTitle>Basic Information</CardTitle>
              <CardDescription>Tell us about yourself</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="username">Username</Label>
                <Input
                  id="username"
                  name="username"
                  value={formData.username}
                  onChange={handleChange}
                  placeholder="Alice"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="height">Height (cm)</Label>
                  <Input
                    id="height"
                    name="height"
                    type="number"
                    value={formData.height}
                    onChange={handleChange}
                    placeholder="165"
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="weight">Weight (kg)</Label>
                  <Input
                    id="weight"
                    name="weight"
                    type="number"
                    value={formData.weight}
                    onChange={handleChange}
                    placeholder="60"
                  />
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="birthdate">Date of Birth</Label>
                <Input
                  id="birthdate"
                  name="birthdate"
                  type="date"
                  value={formData.birthdate}
                  onChange={handleChange}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="fitness-level">Fitness Level</Label>
                <Select
                  value={formData.fitnessLevel}
                  onValueChange={(value) =>
                    setFormData({ ...formData, fitnessLevel: value })
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select your fitness level" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="beginner">Beginner</SelectItem>
                    <SelectItem value="intermediate">Intermediate</SelectItem>
                    <SelectItem value="advanced">Advanced</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </CardContent>
            <CardFooter>
              <Button
                className="w-full bg-primary hover:bg-primary/90"
                onClick={nextStep}
              >
                Continue
                <ArrowRight className="ml-2 h-4 w-4" />
              </Button>
            </CardFooter>
          </Card>
        )}

        {step === 2 && (
          <Card className="border-none shadow-md bg-gradient-to-b from-white to-pink-50">
            <CardHeader>
              <CardTitle>Women's Health</CardTitle>
              <CardDescription>
                Help us tailor workouts to your specific needs
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label>
                  Do you experience menstrual cramps or period-related
                  discomfort?
                </Label>
                <RadioGroup
                  name="menstrualCramps"
                  value={formData.menstrualCramps}
                  onValueChange={(value) =>
                    setFormData({
                      ...formData,
                      menstrualCramps: value,
                    })
                  }
                  defaultValue="no"
                >
                  <div className="flex items-center space-x-2 p-2 rounded-md hover:bg-pink-50 transition-colors">
                    <RadioGroupItem value="yes" id="cramps-yes" />
                    <Label htmlFor="cramps-yes">Yes</Label>
                  </div>
                  <div className="flex items-center space-x-2 p-2 rounded-md hover:bg-pink-50 transition-colors">
                    <RadioGroupItem value="no" id="cramps-no" />
                    <Label htmlFor="cramps-no">No</Label>
                  </div>
                </RadioGroup>
              </div>

              <div className="space-y-2">
                <Label>Are you currently pregnant or postpartum?</Label>
                <RadioGroup
                  name="pregnancyStatus"
                  value={formData.pregnancyStatus}
                  onValueChange={(value) =>
                    setFormData({
                      ...formData,
                      pregnancyStatus: value,
                    })
                  }
                  defaultValue="no"
                >
                  <div className="flex items-center space-x-2 p-2 rounded-md hover:bg-pink-50 transition-colors">
                    <RadioGroupItem value="pregnant" id="pregnant" />
                    <Label htmlFor="pregnant">Yes, pregnant</Label>
                  </div>
                  <div className="flex items-center space-x-2 p-2 rounded-md hover:bg-pink-50 transition-colors">
                    <RadioGroupItem value="postpartum" id="postpartum" />
                    <Label htmlFor="postpartum">Yes, postpartum</Label>
                  </div>
                  <div className="flex items-center space-x-2 p-2 rounded-md hover:bg-pink-50 transition-colors">
                    <RadioGroupItem value="no" id="not-pregnant" />
                    <Label htmlFor="not-pregnant">No</Label>
                  </div>
                </RadioGroup>
              </div>

              <div className="space-y-2">
                <Label>
                  Would you like cycle-based workout recommendations?
                </Label>
                <RadioGroup
                  name="cycleBasedRecommendations"
                  value={formData.cycleBasedRecommendations}
                  onValueChange={(value) =>
                    setFormData({
                      ...formData,
                      cycleBasedRecommendations: value,
                    })
                  }
                  defaultValue="yes"
                >
                  <div className="flex items-center space-x-2 p-2 rounded-md hover:bg-pink-50 transition-colors">
                    <RadioGroupItem value="yes" id="cycle-yes" />
                    <Label htmlFor="cycle-yes">Yes</Label>
                  </div>
                  <div className="flex items-center space-x-2 p-2 rounded-md hover:bg-pink-50 transition-colors">
                    <RadioGroupItem value="no" id="cycle-no" />
                    <Label htmlFor="cycle-no">No</Label>
                  </div>
                </RadioGroup>
                <p className="text-xs text-muted-foreground">
                  This helps us adjust workouts based on your menstrual cycle
                  phase
                </p>
              </div>
            </CardContent>
            <CardFooter className="flex justify-between gap-4">
              <Button variant="outline" className="flex-1" onClick={prevStep}>
                <ArrowLeft className="mr-2 h-4 w-4" />
                Back
              </Button>
              <Button
                className="flex-1 bg-primary hover:bg-primary/90"
                onClick={nextStep}
              >
                Continue
                <ArrowRight className="ml-2 h-4 w-4" />
              </Button>
            </CardFooter>
          </Card>
        )}

        {step === 3 && (
          <Card className="border-none shadow-md bg-gradient-to-b from-white to-pink-50">
            <CardHeader>
              <CardTitle>Workout Preferences</CardTitle>
              <CardDescription>
                Tell us how you like to exercise
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label>
                  Do you prefer high-energy workouts or low-impact workouts?
                </Label>
                <RadioGroup
                  name="workoutType"
                  value={formData.workoutType}
                  onValueChange={(value) =>
                    setFormData({
                      ...formData,
                      workoutType: value,
                    })
                  }
                  defaultValue="high-energy"
                >
                  <div className="flex items-center space-x-2 p-2 rounded-md hover:bg-pink-50 transition-colors">
                    <RadioGroupItem value="high-energy" id="high-energy" />
                    <Label htmlFor="high-energy">
                      High-Energy (HIIT, Strength)
                    </Label>
                  </div>
                  <div className="flex items-center space-x-2 p-2 rounded-md hover:bg-pink-50 transition-colors">
                    <RadioGroupItem value="low-impact" id="low-impact" />
                    <Label htmlFor="low-impact">
                      Low-Impact (Yoga, Stretching)
                    </Label>
                  </div>
                  <div className="flex items-center space-x-2 p-2 rounded-md hover:bg-pink-50 transition-colors">
                    <RadioGroupItem value="both" id="both" />
                    <Label htmlFor="both">Both</Label>
                  </div>
                </RadioGroup>
              </div>

              <div className="space-y-2">
                <Label htmlFor="workout-days">
                  How many days per week do you plan to work out?
                </Label>
                <Select
                  name="workoutDays"
                  value={formData.workoutDays}
                  onValueChange={(value) =>
                    setFormData({
                      ...formData,
                      workoutDays: value,
                    })
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select workout frequency" />
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

              <div className="space-y-2">
                <Label>What is your primary fitness goal?</Label>
                <Select
                  value={formData.fitnessGoal}
                  onValueChange={(value) =>
                    setFormData({ ...formData, fitnessGoal: value })
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select your primary goal" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="general">General Fitness</SelectItem>
                    <SelectItem value="weight-loss">Weight Loss</SelectItem>
                    <SelectItem value="strength">Strength Building</SelectItem>
                    <SelectItem value="flexibility">Flexibility</SelectItem>
                    <SelectItem value="stress-relief">Stress Relief</SelectItem>
                    <SelectItem value="prenatal">Prenatal</SelectItem>
                    <SelectItem value="post-pregnancy">
                      Post-Pregnancy Recovery
                    </SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </CardContent>
            <CardFooter className="flex justify-between gap-4">
              <Button variant="outline" className="flex-1" onClick={prevStep}>
                <ArrowLeft className="mr-2 h-4 w-4" />
                Back
              </Button>
              {/* <Button
                className="flex-1 bg-primary hover:bg-primary/90"
                onClick={handleSubmit}
              >
                Complete
              </Button> */}
              <Button
                className="flex-1 bg-primary hover:bg-primary/90"
                onClick={() => (window.location.href = "/")}
              >
                Complete
              </Button>
            </CardFooter>
          </Card>
        )}
      </div>
    </div>
  );
}

function getStepTitle(step: number): string {
  switch (step) {
    case 1:
      return "Basic Information";
    case 2:
      return "Women's Health";
    case 3:
      return "Workout Preferences";
    default:
      return "";
  }
}
