import type React from "react"
import type { Metadata } from "next"
import { Inter } from "next/font/google"
import "./globals.css"
import { ThemeProvider } from "@/components/theme-provider"
import { MainNav } from "@/components/main-nav"
import { MobileNav } from "@/components/mobile-nav"

const inter = Inter({ subsets: ["latin"] })

export const metadata: Metadata = {
  title: "Fit&Fast - 7-Minute Workouts",
  description: "Effective 7-minute workouts designed for women",
    generator: 'v0.dev'
}

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode
}>) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <ThemeProvider attribute="class" defaultTheme="light" enableSystem disableTransitionOnChange>
          <div className="flex min-h-screen flex-col">
            <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
              <div className="container flex h-14 items-center">
                <MainNav />
                <div className="flex flex-1 items-center justify-end">{/* Header content */}</div>
              </div>
            </header>
            <main className="flex-1">{children}</main>
            <MobileNav />
          </div>
        </ThemeProvider>
      </body>
    </html>
  )
}



import './globals.css'