import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import {
  DumbbellIcon,
  FlameIcon,
  HeartIcon,
  TrendingUpIcon,
  SpaceIcon as Yoga,
  Zap,
  Baby,
  Sparkles,
} from "lucide-react"
import { WorkoutCard } from "@/components/workout-card"

export default function HomePage() {
  return (
    <div className="container px-4 py-6 md:py-10 pb-20 max-w-5xl mx-auto">
      <div className="flex flex-col gap-6">
        <section className="space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-bold tracking-tight">Welcome back, Sarah!</h1>
              <p className="text-muted-foreground">Ready for your 7-minute workout today?</p>
            </div>
            <Button size="sm" className="hidden md:flex">
              View All
            </Button>
          </div>

          <Card>
            <CardHeader className="pb-2">
              <CardTitle>Today's Recommendation</CardTitle>
              <CardDescription>Based on your cycle and preferences</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="bg-muted/50 rounded-lg p-4 flex flex-col md:flex-row gap-4 items-center">
                <div className="bg-pink-100 rounded-full p-3">
                  <HeartIcon className="h-8 w-8 text-primary" />
                </div>
                <div className="flex-1 text-center md:text-left">
                  <h3 className="font-semibold text-lg">Low-Impact Energy Boost</h3>
                  <p className="text-muted-foreground text-sm">Perfect for day 15 of your cycle</p>
                </div>
                <Button>Start Workout</Button>
              </div>
            </CardContent>
          </Card>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <Card className="bg-pink-100 border-none">
              <CardContent className="p-4 flex flex-col items-center justify-center text-center">
                <FlameIcon className="h-6 w-6 text-primary mb-2" />
                <p className="text-sm font-medium">120</p>
                <p className="text-xs text-muted-foreground">Calories</p>
              </CardContent>
            </Card>
            <Card className="bg-pink-100 border-none">
              <CardContent className="p-4 flex flex-col items-center justify-center text-center">
                <DumbbellIcon className="h-6 w-6 text-primary mb-2" />
                <p className="text-sm font-medium">7</p>
                <p className="text-xs text-muted-foreground">Minutes</p>
              </CardContent>
            </Card>
            <Card className="bg-pink-100 border-none">
              <CardContent className="p-4 flex flex-col items-center justify-center text-center">
                <TrendingUpIcon className="h-6 w-6 text-primary mb-2" />
                <p className="text-sm font-medium">5</p>
                <p className="text-xs text-muted-foreground">Day Streak</p>
              </CardContent>
            </Card>
            <Card className="bg-pink-100 border-none">
              <CardContent className="p-4 flex flex-col items-center justify-center text-center">
                <HeartIcon className="h-6 w-6 text-primary mb-2" />
                <p className="text-sm font-medium">Good</p>
                <p className="text-xs text-muted-foreground">Cycle Phase</p>
              </CardContent>
            </Card>
          </div>
        </section>

        <section className="space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-semibold">Workout Categories</h2>
          </div>

          <Tabs defaultValue="yoga" className="w-full">
            <div className="bg-pink-100/50 p-2 rounded-lg mb-4">
              <TabsList className="grid grid-cols-3 md:grid-cols-6 h-auto bg-white/80 p-1 rounded-md">
                <TabsTrigger
                  value="yoga"
                  className="py-3 data-[state=active]:bg-pink-100 data-[state=active]:text-primary"
                >
                  <div className="flex flex-col items-center gap-1">
                    <Yoga className="h-5 w-5" />
                    <span className="text-xs">Yoga</span>
                  </div>
                </TabsTrigger>
                <TabsTrigger
                  value="hiit"
                  className="py-3 data-[state=active]:bg-pink-100 data-[state=active]:text-primary"
                >
                  <div className="flex flex-col items-center gap-1">
                    <Zap className="h-5 w-5" />
                    <span className="text-xs">HIIT</span>
                  </div>
                </TabsTrigger>
                <TabsTrigger
                  value="strength"
                  className="py-3 data-[state=active]:bg-pink-100 data-[state=active]:text-primary"
                >
                  <div className="flex flex-col items-center gap-1">
                    <DumbbellIcon className="h-5 w-5" />
                    <span className="text-xs">Strength</span>
                  </div>
                </TabsTrigger>
                <TabsTrigger
                  value="pre-pregnancy"
                  className="py-3 data-[state=active]:bg-pink-100 data-[state=active]:text-primary"
                >
                  <div className="flex flex-col items-center gap-1">
                    <HeartIcon className="h-5 w-5" />
                    <span className="text-xs">Pre-Preg</span>
                  </div>
                </TabsTrigger>
                <TabsTrigger
                  value="post-pregnancy"
                  className="py-3 data-[state=active]:bg-pink-100 data-[state=active]:text-primary"
                >
                  <div className="flex flex-col items-center gap-1">
                    <Baby className="h-5 w-5" />
                    <span className="text-xs">Post-Preg</span>
                  </div>
                </TabsTrigger>
                <TabsTrigger
                  value="others"
                  className="py-3 data-[state=active]:bg-pink-100 data-[state=active]:text-primary"
                >
                  <div className="flex flex-col items-center gap-1">
                    <Sparkles className="h-5 w-5" />
                    <span className="text-xs">Others</span>
                  </div>
                </TabsTrigger>
              </TabsList>
            </div>

            <TabsContent value="yoga" className="mt-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <WorkoutCard
                  title="Morning Yoga Flow"
                  duration="7 min"
                  level="Beginner"
                  calories="70"
                  image="/placeholder.svg?height=200&width=400"
                  href="/workout/morning-yoga"
                />
                <WorkoutCard
                  title="Relaxing Stretch"
                  duration="7 min"
                  level="All Levels"
                  calories="60"
                  image="/placeholder.svg?height=200&width=400"
                  href="/workout/relaxing-stretch"
                />
              </div>
            </TabsContent>

            <TabsContent value="hiit" className="mt-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <WorkoutCard
                  title="Quick Burn HIIT"
                  duration="7 min"
                  level="Intermediate"
                  calories="120"
                  image="/placeholder.svg?height=200&width=400"
                  href="/workout/quick-burn"
                />
                <WorkoutCard
                  title="Tabata Challenge"
                  duration="7 min"
                  level="Advanced"
                  calories="130"
                  image="/placeholder.svg?height=200&width=400"
                  href="/workout/tabata"
                />
              </div>
            </TabsContent>

            <TabsContent value="strength" className="mt-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <WorkoutCard
                  title="Core Strength"
                  duration="7 min"
                  level="Beginner"
                  calories="90"
                  image="/placeholder.svg?height=200&width=400"
                  href="/workout/core-strength"
                />
                <WorkoutCard
                  title="Full Body Tone"
                  duration="7 min"
                  level="Intermediate"
                  calories="100"
                  image="/placeholder.svg?height=200&width=400"
                  href="/workout/full-body"
                />
              </div>
            </TabsContent>

            <TabsContent value="pre-pregnancy" className="mt-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <WorkoutCard
                  title="Pelvic Floor Strength"
                  duration="7 min"
                  level="All Levels"
                  calories="70"
                  image="/placeholder.svg?height=200&width=400"
                />
                <WorkoutCard
                  title="Gentle Cardio"
                  duration="7 min"
                  level="Beginner"
                  calories="80"
                  image="/placeholder.svg?height=200&width=400"
                />
              </div>
            </TabsContent>

            <TabsContent value="post-pregnancy" className="mt-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <WorkoutCard
                  title="Postpartum Recovery"
                  duration="7 min"
                  level="Beginner"
                  calories="60"
                  image="/placeholder.svg?height=200&width=400"
                />
                <WorkoutCard
                  title="Mommy & Baby"
                  duration="7 min"
                  level="All Levels"
                  calories="70"
                  image="/placeholder.svg?height=200&width=400"
                />
              </div>
            </TabsContent>

            <TabsContent value="others" className="mt-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <WorkoutCard
                  title="Stress Relief"
                  duration="7 min"
                  level="All Levels"
                  calories="80"
                  image="/placeholder.svg?height=200&width=400"
                />
                <WorkoutCard
                  title="Office Break"
                  duration="7 min"
                  level="Beginner"
                  calories="70"
                  image="/placeholder.svg?height=200&width=400"
                />
              </div>
            </TabsContent>
          </Tabs>
        </section>
      </div>
    </div>
  )
}

