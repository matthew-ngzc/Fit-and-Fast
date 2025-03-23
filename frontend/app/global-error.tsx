"use client"

import { useEffect } from "react"
import { Button } from "@/components/ui/button"
import { RefreshCw } from "lucide-react"
import { Inter } from "next/font/google"
import { ThemeProvider } from "@/components/theme-provider"

const inter = Inter({ subsets: ["latin"] })

export default function GlobalError({
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
    <html lang="en">
      <body className={inter.className}>
        <ThemeProvider attribute="class" defaultTheme="light" enableSystem disableTransitionOnChange>
          <div className="flex min-h-screen flex-col items-center justify-center text-center px-4">
            <div className="mb-8 text-8xl">
              <div className="relative inline-block">
                <span className="text-9xl">😭</span>
                <span className="absolute top-0 right-0 text-5xl">💦</span>
                <span className="absolute bottom-0 left-0 text-5xl">💦</span>
              </div>
            </div>

            <h1 className="text-4xl font-bold tracking-tight mb-2 bg-gradient-to-r from-primary to-pink-400 bg-clip-text text-transparent">
              Critical Error
            </h1>

            <p className="text-muted-foreground mb-8 max-w-md mx-auto">
              We're really sorry, but something went seriously wrong with the application.
            </p>

            <div className="flex flex-col items-center gap-4">
              <div className="bg-pink-100 p-6 rounded-lg mb-6 max-w-md">
                <p className="text-sm italic">
                  "Even the strongest workout routines sometimes need a restart. Let's give it another try!"
                </p>
              </div>

              <Button onClick={reset} className="gap-2">
                <RefreshCw className="h-4 w-4" />
                Restart Application
              </Button>
            </div>
          </div>
        </ThemeProvider>
      </body>
    </html>
  )
}