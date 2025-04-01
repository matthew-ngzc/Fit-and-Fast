"use client"

import { Line, LineChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "@/components/ui/chart"
import { useEffect, useState } from "react"
import { Button } from "@/components/ui/button"
import { ClockIcon, FlameIcon } from "lucide-react"

type ActivityData = {
  date: string
  caloriesBurned: number
  durationInMinutes: number
}

export function ActivityLineChart({ weeklyData }: { weeklyData: ActivityData[] }) {
  const [formattedData, setFormattedData] = useState<ActivityData[]>([])
  const [loading, setLoading] = useState(true)
  const [activeMetric, setActiveMetric] = useState<"calories" | "duration">("calories")

  useEffect(() => {
    if (weeklyData && weeklyData.length > 0) {
      try {
        const formatted = weeklyData.map((item) => {
          const date = new Date(item.date)
          const formattedDate = `${date.getDate().toString().padStart(2, "0")}/${(date.getMonth() + 1)
            .toString()
            .padStart(2, "0")}`
          return {
            date: formattedDate,
            caloriesBurned: item.caloriesBurned,
            durationInMinutes: item.durationInMinutes,
          }
        })
        setFormattedData(formatted)
        setLoading(false)
      } catch (error) {
        console.error("Failed to format activity data:", error)
        setLoading(false)
      }
    } else {
      setLoading(false)
    }
  }, [weeklyData])

  if (loading) {
    return <div className="h-[300px] flex items-center justify-center">Loading data...</div>
  }

  return (
    <div className="space-y-4">
      <div className="flex justify-end space-x-2">
        <Button
          variant={activeMetric === "calories" ? "default" : "outline"}
          size="sm"
          onClick={() => setActiveMetric("calories")}
          className="flex items-center gap-1"
        >
          <FlameIcon className="h-4 w-4" />
          Calories
        </Button>
        <Button
          variant={activeMetric === "duration" ? "default" : "outline"}
          size="sm"
          onClick={() => setActiveMetric("duration")}
          className="flex items-center gap-1"
        >
          <ClockIcon className="h-4 w-4" />
          Time
        </Button>
      </div>
      <div className="w-full h-[300px]">
        <ResponsiveContainer width="100%" height="100%">
          <LineChart data={formattedData}>
            <XAxis dataKey="date" stroke="#888888" fontSize={12} tickLine={false} axisLine={false} />
            <YAxis
              stroke="#888888"
              fontSize={12}
              tickLine={false}
              axisLine={false}
              tickFormatter={(value) => `${value}${activeMetric === "duration" ? "m" : ""}`}
            />
            <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
            <Tooltip
              formatter={(value, name) => {
                if (name === "caloriesBurned") return [`${value} cal`, "Calories Burned"]
                if (name === "durationInMinutes") return [`${value} min`, "Duration"]
                return [value, name]
              }}
            />
            {activeMetric === "calories" ? (
              <Line
                type="monotone"
                dataKey="caloriesBurned"
                stroke="#F52B86"
                strokeWidth={2}
                dot={{ fill: "#F52B86", r: 4 }}
                activeDot={{ r: 6, fill: "#F52B86" }}
                name="caloriesBurned"
              />
            ) : (
              <Line
                type="monotone"
                dataKey="durationInMinutes"
                stroke="#3B82F6"
                strokeWidth={2}
                dot={{ fill: "#3B82F6", r: 4 }}
                activeDot={{ r: 6, fill: "#3B82F6" }}
                name="durationInMinutes"
              />
            )}
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  )
}

