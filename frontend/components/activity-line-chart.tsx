"use client"

import { Line, LineChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "@/components/ui/chart"
import { useEffect, useState } from "react"
import { fetchData } from "@/lib/data-module"

export function ActivityLineChart() {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    async function loadData() {
      try {
        const jsonData = await fetchData()
        setData(jsonData.weeklyProgress)
      } catch (error) {
        console.error("Failed to load activity data:", error)
      } finally {
        setLoading(false)
      }
    }

    loadData()
  }, [])

  if (loading) {
    return <div className="h-[300px] flex items-center justify-center">Loading data...</div>
  }

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

