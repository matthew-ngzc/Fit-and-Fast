"use client";

import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Home } from "lucide-react"

export default function NotFound() {
  return (
    <div className="container flex flex-col items-center justify-center min-h-[80vh] px-4 py-16 text-center">
      <div className="mb-8 text-9xl">😢</div>

      <h1 className="text-4xl font-bold tracking-tight mb-2 bg-gradient-to-r from-primary to-pink-400 bg-clip-text text-transparent">
        Page Not Found
      </h1>

      <p className="text-muted-foreground mb-8 max-w-md mx-auto">
        Oops! We couldn't find the page you're looking for. It might have been moved or doesn't exist.
      </p>

      <div className="flex flex-col items-center gap-4">
        <div className="bg-pink-100 p-6 rounded-lg mb-6 max-w-md">
          <p className="text-sm italic">
            "I searched high and low for your workout, but it seems to have stretched too far away!"
          </p>
        </div>

        <Link href="/">
          <Button className="gap-2">
            <Home className="h-4 w-4" />
            Return to Home
          </Button>
        </Link>
      </div>
    </div>
  )
}