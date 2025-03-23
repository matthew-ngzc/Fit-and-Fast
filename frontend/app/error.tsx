"use client"

import { useEffect } from "react"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Home, RefreshCw } from "lucide-react"

export default function Error({
  error,
  reset,
}: {
  error: Error & { digest?: string }
  reset: () => void
}) {
  useEffect(() => {
    // Log the error to an error reporting service
    console.error(error)
  }, [error])

  return (
    <div className="container flex flex-col items-center justify-center min-h-[80vh] px-4 py-16 text-center">
      <div className="mb-8 text-8xl">
        <div className="relative inline-block">
          <span className="text-9xl">🥺</span>
          <span className="absolute top-0 right-0 text-5xl">💦</span>
        </div>
      </div>

      <h1 className="text-4xl font-bold tracking-tight mb-2 bg-gradient-to-r from-primary to-pink-400 bg-clip-text text-transparent">
        Oops! Something Went Wrong
      </h1>

      <p className="text-muted-foreground mb-8 max-w-md mx-auto">
        We're sorry, but we encountered an unexpected error. Don't worry, it's not your fault!
      </p>

      <div className="flex flex-col items-center gap-4">
        <div className="bg-pink-100 p-6 rounded-lg mb-6 max-w-md">
          <p className="text-sm italic">
            "Even the best workout routines have rest days. Let's take a breath and try again!"
          </p>
        </div>

        <div className="flex flex-wrap gap-4 justify-center">
          <Button onClick={reset} variant="outline" className="gap-2">
            <RefreshCw className="h-4 w-4" />
            Try Again
          </Button>

          <Link href="/">
            <Button className="gap-2">
              <Home className="h-4 w-4" />
              Return to Home
            </Button>
          </Link>
        </div>
      </div>
    </div>
  )
}

