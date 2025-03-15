import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { CalendarIcon } from "lucide-react"

interface CycleData {
  periodStart: Date
  periodEnd: Date
  nextPeriodStart: Date
  cycleLength: number
  periodLength: number
}

interface CycleInfoProps {
  cycleData: CycleData
}

export function CycleInfo({ cycleData }: CycleInfoProps) {
  // Format date to display in a readable format
  const formatDate = (date: Date) => {
    return date.toLocaleDateString("en-US", { month: "short", day: "numeric" })
  }

  // Calculate days until next period
  const today = new Date()
  const daysUntilNextPeriod = Math.ceil((cycleData.nextPeriodStart.getTime() - today.getTime()) / (1000 * 60 * 60 * 24))

  // Determine current phase
  const isInPeriod = today >= cycleData.periodStart && today <= cycleData.periodEnd
  const isInFollicularPhase = today > cycleData.periodEnd && today < cycleData.nextPeriodStart

  let currentPhase = "Unknown"
  let phaseColor = "bg-muted"

  if (isInPeriod) {
    currentPhase = "Menstrual Phase"
    phaseColor = "bg-pink-100"
  } else if (isInFollicularPhase) {
    currentPhase = "Follicular Phase"
    phaseColor = "bg-blue-100"
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Cycle Information</CardTitle>
        <CardDescription>Track your menstrual cycle</CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className={`p-3 rounded-lg ${phaseColor}`}>
          <h3 className="font-medium">Current Phase: {currentPhase}</h3>
          <p className="text-sm text-muted-foreground">
            {daysUntilNextPeriod > 0 ? `${daysUntilNextPeriod} days until next period` : "Your period is due today"}
          </p>
        </div>

        <div className="grid grid-cols-2 gap-2 text-sm">
          <div className="flex items-center gap-2">
            <CalendarIcon className="h-4 w-4 text-muted-foreground" />
            <span className="text-muted-foreground">Last Period:</span>
          </div>
          <div>{formatDate(cycleData.periodStart)}</div>

          <div className="flex items-center gap-2">
            <CalendarIcon className="h-4 w-4 text-muted-foreground" />
            <span className="text-muted-foreground">Next Period:</span>
          </div>
          <div>{formatDate(cycleData.nextPeriodStart)}</div>

          <div className="flex items-center gap-2">
            <CalendarIcon className="h-4 w-4 text-muted-foreground" />
            <span className="text-muted-foreground">Cycle Length:</span>
          </div>
          <div>{cycleData.cycleLength} days</div>

          <div className="flex items-center gap-2">
            <CalendarIcon className="h-4 w-4 text-muted-foreground" />
            <span className="text-muted-foreground">Period Length:</span>
          </div>
          <div>{cycleData.periodLength} days</div>
        </div>
      </CardContent>
    </Card>
  )
}

