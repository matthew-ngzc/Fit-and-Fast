import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { CalendarIcon } from "lucide-react";

interface CycleData {
  lastPeriodStartDate: Date;
  lastPeriodEndDate: Date;
  nextPeriodStart: Date;
  cycleLength: number;
  periodLength: number;
  nextPeriodStartDate: Date;
  daysUntilNextPeriod: number;
  currentPhase: string;
}

interface CycleInfoProps {
  cycleData: CycleData;
}

export function CycleInfo({ cycleData }: CycleInfoProps) {
  if (!cycleData) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Cycle Information</CardTitle>
          <CardDescription>No cycle data available</CardDescription>
        </CardHeader>
        <CardContent>
          <p className="text-sm text-muted-foreground">
            Please add your cycle information to see predictions and
            recommendations.
          </p>
        </CardContent>
      </Card>
    );
  }

  const formatDate = (date: Date) => {
    if (!date || !(date instanceof Date) || isNaN(date.getTime())) {
      return "Unknown date";
    }
    return date.toLocaleDateString("en-US", { month: "short", day: "numeric" });
  };

  let phaseColor = "bg-muted";

  switch (cycleData.currentPhase) {
    case "Menstrual Phase":
      phaseColor = "bg-pink-100";
      break;
    case "Follicular Phase":
      phaseColor = "bg-blue-100";
      break;
    case "Ovulation Phase":
      phaseColor = "bg-green-100";
      break;
    case "Luteal Phase":
      phaseColor = "bg-yellow-100";
      break;
    default:
      phaseColor = "bg-muted";
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Cycle Information</CardTitle>
        <CardDescription>Track your menstrual cycle</CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className={`p-3 rounded-lg ${phaseColor}`}>
          <h3 className="font-medium">
            Current Phase: {cycleData.currentPhase}
          </h3>
          <p className="text-sm text-muted-foreground">
            {cycleData.daysUntilNextPeriod > 0
              ? `${cycleData.daysUntilNextPeriod} days until next period`
              : "Your period is due today"}
          </p>
        </div>

        <div className="grid grid-cols-2 gap-2 text-sm">
          <div className="flex items-center gap-2">
            <CalendarIcon className="h-4 w-4 text-muted-foreground" />
            <span className="text-muted-foreground">Last Period:</span>
          </div>
          <div>{formatDate(cycleData.lastPeriodStartDate)}</div>

          <div className="flex items-center gap-2">
            <CalendarIcon className="h-4 w-4 text-muted-foreground" />
            <span className="text-muted-foreground">Next Period:</span>
          </div>
          <div>{formatDate(cycleData.nextPeriodStartDate)}</div>

          <div className="flex items-center gap-2">
            <CalendarIcon className="h-4 w-4 text-muted-foreground" />
            <span className="text-muted-foreground">Cycle Length:</span>
          </div>
          <div>{cycleData.cycleLength || "Unknown"} days</div>

          <div className="flex items-center gap-2">
            <CalendarIcon className="h-4 w-4 text-muted-foreground" />
            <span className="text-muted-foreground">Period Length:</span>
          </div>
          <div>{cycleData.periodLength || "Unknown"} days</div>
        </div>
      </CardContent>
    </Card>
  );
}
