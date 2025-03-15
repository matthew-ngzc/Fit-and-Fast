"use client"

import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardFooter } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { DumbbellIcon, ArrowLeft } from "lucide-react"

export default function SignupPage() {
  return (
    <div className="container flex h-screen w-screen flex-col items-center justify-center">
      <div className="mx-auto flex w-full flex-col justify-center space-y-6 sm:w-[350px]">
        <div className="flex flex-col space-y-2 text-center">
          <Link href="/auth/login" className="flex items-center text-sm text-muted-foreground mb-2 hover:text-primary">
            <ArrowLeft className="h-4 w-4 mr-1" />
            Back to Login
          </Link>
          <div className="flex justify-center">
            <div className="bg-pink-100 p-2 rounded-full">
              <DumbbellIcon className="h-10 w-10 text-primary" />
            </div>
          </div>
          <h1 className="text-2xl font-semibold tracking-tight">Create an account</h1>
          <p className="text-sm text-muted-foreground">Enter your details to create your Fit&Fast account</p>
        </div>
        <Card>
          <CardContent className="pt-4">
            <div className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="name">Full Name</Label>
                <Input id="name" placeholder="Sarah Anderson" required />
              </div>
              <div className="space-y-2">
                <Label htmlFor="email">Email</Label>
                <Input id="email" placeholder="name@example.com" required type="email" />
              </div>
              <div className="space-y-2">
                <Label htmlFor="password">Password</Label>
                <Input id="password" required type="password" />
              </div>
              <div className="space-y-2">
                <Label htmlFor="confirm-password">Confirm Password</Label>
                <Input id="confirm-password" required type="password" />
              </div>
              <Button className="w-full" type="submit" onClick={() => (window.location.href = "/auth/questionnaire")}>
                Create Account
              </Button>
            </div>
          </CardContent>
          <CardFooter className="flex flex-col">
            <div className="mt-2 text-center text-sm text-muted-foreground">
              Already have an account?{" "}
              <Link href="/auth/login" className="text-primary underline-offset-4 hover:underline">
                Sign in
              </Link>
            </div>
          </CardFooter>
        </Card>
      </div>
    </div>
  )
}

