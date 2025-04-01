"use client";

import type React from "react";

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
import config from "@/config";

export default function QuestionnairePage() {
  const [step, setStep] = useState(1);
  const totalSteps = 4;

  const [formData, setFormData] = useState({
    height: "",
    weight: "",
    birthdate: "",
    fitnessLevel: "",
    menstrualCramps: "",
    pregnancyStatus: "",
    cycleBasedRecommendations: "",
    workoutType: "",
    workoutDays: "",
    fitnessGoal: "",
    cycleLength: "",
    periodLength: "",
    lastPeriodDate: "",
  });

  const nextStep = () => {
    if (step < totalSteps) {
      if (isCurrentStepComplete()) {
        setStep(step + 1);
        window.scrollTo(0, 0);
      } else {
        alert("Please complete all fields before proceeding to the next step.");
      }
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
    isSelect = false
  ) => {
    const { name, value } = e.target;

    if (isSelect) {
      setFormData({
        ...formData,
        [name]: value,
      });
    } else {
      setFormData({
        ...formData,
        [name]: value,
      });
    }
  };

  const handleSubmit = async () => {
    try {
      const token = localStorage.getItem("token");

      if (!token) {
        alert("Unauthorised Action");
        window.location.href = "/auth/login";
        return;
      }

      const requestData = {
        height: Number.parseFloat(formData.height),
        weight: Number.parseFloat(formData.weight),
        dob: formData.birthdate,
        fitnessLevel: formData.fitnessLevel,
        menstrualCramps: formData.menstrualCramps === "yes",
        pregnancyStatus: formData.pregnancyStatus?.toUpperCase(),
        cycleBasedRecommendations: formData.cycleBasedRecommendations === "yes",
        workoutType: formData.workoutType,
        workoutDays: Number.parseInt(formData.workoutDays),
        workoutGoal: formData.fitnessGoal,
        cycleLength: Number.parseInt(formData.cycleLength),
        periodLength: Number.parseInt(formData.periodLength),
        lastPeriodDate: formData.lastPeriodDate,
      };

      const response = await fetch(`${config.USER_URL}/questionnaire`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(requestData),
      });

      if (response.status === 201) {
        window.location.href = "/home";
      } else {
        alert("Failed to submit form. Please try again.");
      }
    } catch (error) {
      alert(`Error submitting form`);
      console.error(error);
    }
  };

  const isStep1Complete = () => {
    return (
      formData.height.trim() !== "" &&
      formData.weight.trim() !== "" &&
      formData.birthdate.trim() !== "" &&
      formData.fitnessLevel.trim() !== ""
    );
  };

  const isStep2Complete = () => {
    return (
      formData.menstrualCramps.trim() !== "" &&
      formData.pregnancyStatus.trim() !== "" &&
      formData.cycleBasedRecommendations.trim() !== ""
    );
  };

  const isStep3Complete = () => {
    return (
      formData.cycleLength.trim() !== "" &&
      formData.periodLength.trim() !== "" &&
      formData.lastPeriodDate.trim() !== ""
    );
  };

  const isStep4Complete = () => {
    return (
      formData.workoutType.trim() !== "" &&
      formData.workoutDays.trim() !== "" &&
      formData.fitnessGoal.trim() !== ""
    );
  };

  const isCurrentStepComplete = () => {
    switch (step) {
      case 1:
        return isStep1Complete();
      case 2:
        return isStep2Complete();
      case 3:
        return isStep3Complete();
      case 4:
        return isStep4Complete();
      default:
        return false;
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
                    <SelectItem value="Beginner">Beginner</SelectItem>
                    <SelectItem value="Intermediate">Intermediate</SelectItem>
                    <SelectItem value="Advanced">Advanced</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </CardContent>
            <CardFooter>
              <Button
                className="w-full bg-primary hover:bg-primary/90"
                onClick={nextStep}
                disabled={!isStep1Complete()}
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
                disabled={!isStep2Complete()}
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
              <CardTitle>Cycle Information</CardTitle>
              <CardDescription>
                Help us adapt your workouts to your menstrual cycle
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-1">
                <p className="text-sm text-muted-foreground mb-4">
                  Fit&Fast helps track your menstrual cycle to recommend the
                  most effective workouts for each phase, optimizing your
                  fitness results and comfort.
                </p>

                <div className="space-y-2">
                  <Label htmlFor="cycleLength">Cycle Length (days)</Label>
                  <Select
                    value={formData.cycleLength}
                    onValueChange={(value) =>
                      setFormData({ ...formData, cycleLength: value })
                    }
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="Select cycle length" />
                    </SelectTrigger>
                    <SelectContent>
                      {Array.from({ length: 10 }, (_, i) => i + 21).map(
                        (days) => (
                          <SelectItem key={days} value={days.toString()}>
                            {days} days
                          </SelectItem>
                        )
                      )}
                    </SelectContent>
                  </Select>
                  <p className="text-xs text-muted-foreground">
                    Average time from the first day of one period to the first
                    day of the next
                  </p>
                </div>

                <div className="space-y-2 mt-4">
                  <Label htmlFor="periodLength">Period Length (days)</Label>
                  <Select
                    value={formData.periodLength}
                    onValueChange={(value) =>
                      setFormData({ ...formData, periodLength: value })
                    }
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="Select period length" />
                    </SelectTrigger>
                    <SelectContent>
                      {Array.from({ length: 10 }, (_, i) => i + 1).map(
                        (days) => (
                          <SelectItem key={days} value={days.toString()}>
                            {days} days
                          </SelectItem>
                        )
                      )}
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2 mt-4">
                  <Label htmlFor="lastPeriodDate">Last Period Start Date</Label>
                  <Input
                    id="lastPeriodDate"
                    name="lastPeriodDate"
                    type="date"
                    value={formData.lastPeriodDate}
                    onChange={handleChange}
                  />
                </div>
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
                disabled={!isStep3Complete()}
              >
                Continue
                <ArrowRight className="ml-2 h-4 w-4" />
              </Button>
            </CardFooter>
          </Card>
        )}

        {step === 4 && (
          <Card className="border-none shadow-md bg-gradient-to-b from-white to-pink-50">
            <CardHeader>
              <CardTitle>Workout Preferences</CardTitle>
              <CardDescription>
                Tell us how you like to exercise
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label>Which workout type do you prefer?</Label>
                <Select
                  name="workoutType"
                  value={formData.workoutType}
                  onValueChange={(value) =>
                    setFormData({
                      ...formData,
                      workoutType: value,
                    })
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select workout type" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="HIIT">HIIT</SelectItem>
                    <SelectItem value="strength">Strength</SelectItem>
                    <SelectItem value="yoga">Yoga</SelectItem>
                    <SelectItem value="low-impact">Low-Impact Workouts</SelectItem>
                    <SelectItem value="body-weight">Bodyweight workouts</SelectItem>
                    <SelectItem value="postnatal">
                      Postnatal Recovery
                    </SelectItem>
                    <SelectItem value="prenatal">Prenatal workouts</SelectItem>
                    <SelectItem value="others">Others</SelectItem>
                  </SelectContent>
                </Select>
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
              <Button
                className="flex-1 bg-primary hover:bg-primary/90"
                onClick={handleSubmit}
                disabled={!isStep4Complete()}
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
      return "Cycle Information";
    case 4:
      return "Workout Preferences";
    default:
      return "";
  }
}
