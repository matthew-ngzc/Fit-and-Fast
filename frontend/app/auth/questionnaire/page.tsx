"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Progress } from "@/components/ui/progress"
import { Textarea } from "@/components/ui/textarea"
import { DumbbellIcon, ArrowLeft, ArrowRight } from "lucide-react"

export default function QuestionnairePage() {
  const [step, setStep] = useState(1)
  const totalSteps = 3

  const nextStep = () => {
    if (step < totalSteps) {
      setStep(step + 1)
      window.scrollTo(0, 0)
    }
  }

  const prevStep = () => {
    if (step > 1) {
      setStep(step - 1)
      window.scrollTo(0, 0)
    }
  }

  return (
    <div className="container flex min-h-screen w-screen flex-col items-center justify-center py-10">
      <div className="mx-auto flex w-full flex-col justify-center space-y-6 max-w-md">
        <div className="flex flex-col space-y-2 text-center">
          <div className="flex justify-center">
            <div className="bg-pink-100 p-2 rounded-full">
              <DumbbellIcon className="h-10 w-10 text-primary" />
            </div>
          </div>
          <h1 className="text-2xl font-semibold tracking-tight">Let's personalize your experience</h1>
          <p className="text-sm text-muted-foreground">Tell us about yourself so we can customize your workouts</p>
        </div>

        <div className="space-y-2">
          <div className="flex justify-between text-sm">
            <span>
              Step {step} of {totalSteps}
            </span>
            <span>{getStepTitle(step)}</span>
          </div>
          <Progress value={(step / totalSteps) * 100} className="h-2" />
        </div>

        {step === 1 && (
          <Card>
            <CardHeader>
              <CardTitle>Basic Information</CardTitle>
              <CardDescription>Tell us about yourself</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="username">Username</Label>
                <Input id="username" placeholder="FitSarah" />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="height">Height (cm)</Label>
                  <Input id="height" type="number" placeholder="165" />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="weight">Weight (kg)</Label>
                  <Input id="weight" type="number" placeholder="60" />
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="birthdate">Date of Birth</Label>
                <Input id="birthdate" type="date" />
              </div>

              <div className="space-y-2">
                <Label>Fitness Level</Label>
                <RadioGroup defaultValue="beginner">
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="beginner" id="beginner" />
                    <Label htmlFor="beginner">Beginner</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="intermediate" id="intermediate" />
                    <Label htmlFor="intermediate">Intermediate</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="advanced" id="advanced" />
                    <Label htmlFor="advanced">Advanced</Label>
                  </div>
                </RadioGroup>
              </div>
            </CardContent>
            <CardFooter>
              <Button className="w-full" onClick={nextStep}>
                Continue
              </Button>
            </CardFooter>
          </Card>
        )}

        {step === 2 && (
          <Card>
            <CardHeader>
              <CardTitle>Women's Health</CardTitle>
              <CardDescription>Help us tailor workouts to your specific needs</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label>Do you experience menstrual cramps or period-related discomfort?</Label>
                <RadioGroup defaultValue="no">
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="yes" id="cramps-yes" />
                    <Label htmlFor="cramps-yes">Yes</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="no" id="cramps-no" />
                    <Label htmlFor="cramps-no">No</Label>
                  </div>
                </RadioGroup>
              </div>

              <div className="space-y-2">
                <Label>Are you currently pregnant or postpartum?</Label>
                <RadioGroup defaultValue="no">
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="pregnant" id="pregnant" />
                    <Label htmlFor="pregnant">Yes, pregnant</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="postpartum" id="postpartum" />
                    <Label htmlFor="postpartum">Yes, postpartum</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="no" id="not-pregnant" />
                    <Label htmlFor="not-pregnant">No</Label>
                  </div>
                </RadioGroup>
              </div>

              <div className="space-y-2">
                <Label>Would you like cycle-based workout recommendations?</Label>
                <RadioGroup defaultValue="yes">
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="yes" id="cycle-yes" />
                    <Label htmlFor="cycle-yes">Yes</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="no" id="cycle-no" />
                    <Label htmlFor="cycle-no">No</Label>
                  </div>
                </RadioGroup>
                <p className="text-xs text-muted-foreground">
                  This helps us adjust workouts based on your menstrual cycle phase
                </p>
              </div>

              <div className="space-y-2">
                <Label htmlFor="injuries">Do you have any injuries or physical conditions we should consider?</Label>
                <Textarea
                  id="injuries"
                  placeholder="E.g., lower back pain, knee injury, etc."
                  className="resize-none"
                />
              </div>
            </CardContent>
            <CardFooter className="flex justify-between">
              <Button variant="outline" onClick={prevStep}>
                <ArrowLeft className="mr-2 h-4 w-4" />
                Back
              </Button>
              <Button onClick={nextStep}>
                Continue
                <ArrowRight className="ml-2 h-4 w-4" />
              </Button>
            </CardFooter>
          </Card>
        )}

        {step === 3 && (
          <Card>
            <CardHeader>
              <CardTitle>Workout Preferences</CardTitle>
              <CardDescription>Tell us how you like to exercise</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label>Do you prefer high-energy workouts or low-impact workouts?</Label>
                <RadioGroup defaultValue="high-energy">
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="high-energy" id="high-energy" />
                    <Label htmlFor="high-energy">High-Energy (HIIT, Strength)</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="low-impact" id="low-impact" />
                    <Label htmlFor="low-impact">Low-Impact (Yoga, Stretching)</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="both" id="both" />
                    <Label htmlFor="both">Both</Label>
                  </div>
                </RadioGroup>
              </div>

              <div className="space-y-2">
                <Label>How many days per week do you plan to work out?</Label>
                <RadioGroup defaultValue="3-4">
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="1-2" id="days-1-2" />
                    <Label htmlFor="days-1-2">1-2 days</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="3-4" id="days-3-4" />
                    <Label htmlFor="days-3-4">3-4 days</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="5+" id="days-5-plus" />
                    <Label htmlFor="days-5-plus">5+ days</Label>
                  </div>
                </RadioGroup>
              </div>

              <div className="space-y-2">
                <Label>What is your primary fitness goal?</Label>
                <Select defaultValue="general">
                  <SelectTrigger>
                    <SelectValue placeholder="Select your primary goal" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="weight-loss">Weight Loss</SelectItem>
                    <SelectItem value="strength">Strength Building</SelectItem>
                    <SelectItem value="flexibility">Flexibility</SelectItem>
                    <SelectItem value="stress-relief">Stress Relief</SelectItem>
                    <SelectItem value="general">General Fitness</SelectItem>
                    <SelectItem value="post-pregnancy">Post-Pregnancy Recovery</SelectItem>
                    <SelectItem value="other">Other</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="other-goal">If "Other", please specify:</Label>
                <Input id="other-goal" placeholder="Your specific fitness goal" />
              </div>
            </CardContent>
            <CardFooter className="flex justify-between">
              <Button variant="outline" onClick={prevStep}>
                <ArrowLeft className="mr-2 h-4 w-4" />
                Back
              </Button>
              <Button onClick={() => (window.location.href = "/")}>Complete</Button>
            </CardFooter>
          </Card>
        )}
      </div>
    </div>
  )
}

function getStepTitle(step: number): string {
  switch (step) {
    case 1:
      return "Basic Information"
    case 2:
      return "Women's Health"
    case 3:
      return "Workout Preferences"
    default:
      return ""
  }
}

