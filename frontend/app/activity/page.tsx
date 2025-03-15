import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { FlameIcon, TimerIcon } from "lucide-react"
import { ActivityLineChart } from "@/components/activity-line-chart"
import { ActivityLog } from "@/components/activity-log"

export default function ActivityPage() {
  return (
    <div className="container px-4 py-6 md:py-10 pb-20 max-w-5xl mx-auto">
      <div className="flex flex-col gap-6">
        <section>
          <h1 className="text-2xl font-bold tracking-tight mb-4">Your Activity</h1>

          <div className="grid grid-cols-2 gap-4 mb-6">
            <Card className="bg-pink-100 border-none">
              <CardContent className="p-4 flex flex-row items-center gap-4">
                <div className="bg-white/50 rounded-full p-2">
                  <FlameIcon className="h-6 w-6 text-primary" />
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Today's Calories</p>
                  <p className="text-2xl font-bold">120</p>
                </div>
              </CardContent>
            </Card>
            <Card className="bg-pink-100 border-none">
              <CardContent className="p-4 flex flex-row items-center gap-4">
                <div className="bg-white/50 rounded-full p-2">
                  <TimerIcon className="h-6 w-6 text-primary" />
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Today's Minutes</p>
                  <p className="text-2xl font-bold">7</p>
                </div>
              </CardContent>
            </Card>
          </div>

          <Card className="mb-6">
            <CardHeader>
              <CardTitle>Weekly Progress</CardTitle>
              <CardDescription>Calories burned over the past 7 days</CardDescription>
            </CardHeader>
            <CardContent>
              <ActivityLineChart />
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Recent Workouts</CardTitle>
              <CardDescription>Your workout history</CardDescription>
            </CardHeader>
            <CardContent>
              <ActivityLog />
            </CardContent>
          </Card>
        </section>
      </div>
    </div>
  )
}

