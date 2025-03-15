"use client"

import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "@/components/ui/chart"

export function ActivityChart() {
  // Sample data - in a real app, this would come from an API or database
  const data = [
    { name: "Mon", calories: 120 },
    { name: "Tue", calories: 150 },
    { name: "Wed", calories: 0 },
    { name: "Thu", calories: 180 },
    { name: "Fri", calories: 200 },
    { name: "Sat", calories: 90 },
    { name: "Sun", calories: 100 },
  ]

  return (
    <div className="w-full h-[300px]">
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={data}>
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
          <Bar dataKey="calories" fill="currentColor" radius={[4, 4, 0, 0]} className="fill-primary" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  )
}

