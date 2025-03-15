"use client"

import { Line, LineChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "@/components/ui/chart"

export function ActivityLineChart() {
  // Sample data - in a real app, this would come from an API or database
  const data = [
    { name: "Mon", calories: 120 },
    { name: "Tue", calories: 150 },
    { name: "Wed", calories: 0 },
    { name: "Thu", calories: 180 },
    { name: "Fri", calories: 200 },
    { name: "Sat", calories: 90 },
    { name: "Sun", calories: 120 },
  ]

  return (
    <div className="w-full h-[300px]">
      <ResponsiveContainer width="100%" height="100%">
        <LineChart data={data}>
          <XAxis dataKey="name" stroke="#888888" fontSize={12} tickLine={false} axisLine={false} />
          <YAxis
            stroke="#888888"
            fontSize={12}
            tickLine={false}
            axisLine={false}
            tickFormatter={(value) => `${value}`}
          />
          <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
          <Tooltip />
          <Line
            type="monotone"
            dataKey="calories"
            stroke="#F52B86"
            strokeWidth={2}
            dot={{ fill: "#F52B86", r: 4 }}
            activeDot={{ r: 6, fill: "#F52B86" }}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  )
}

