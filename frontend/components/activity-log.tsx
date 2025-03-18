"use client"

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Badge } from "@/components/ui/badge"
import { CalendarIcon, ClockIcon, FlameIcon } from "lucide-react"
import { useEffect, useState } from "react"
import { fetchData } from "@/lib/data-module"

export function ActivityLog() {
  const [activities, setActivities] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    async function loadData() {
      try {
        const data = await fetchData()
        setActivities(data.activities)
      } catch (error) {
        console.error("Failed to load activities:", error)
      } finally {
        setLoading(false)
      }
    }

    loadData()
  }, [])

  if (loading) {
    return <div className="py-4 text-center">Loading activities...</div>
  }

  return (
    <div className="space-y-4">
      {activities.map((activity: any) => (
        <div key={activity.id} className="flex items-start gap-4 p-4 border rounded-lg">
          <Avatar className="h-10 w-10">
            <AvatarImage src="/placeholder.svg?height=40&width=40" alt={activity.title} />
            <AvatarFallback>WO</AvatarFallback>
          </Avatar>
          <div className="flex-1 space-y-1">
            <div className="flex items-center justify-between">
              <h4 className="font-medium">{activity.title}</h4>
              <Badge variant="outline">{activity.type}</Badge>
            </div>
            <div className="flex items-center text-sm text-muted-foreground">
              <CalendarIcon className="mr-1 h-3 w-3" />
              <span>{activity.date}</span>
            </div>
            <div className="flex items-center gap-4 text-sm text-muted-foreground">
              <div className="flex items-center">
                <ClockIcon className="mr-1 h-3 w-3" />
                <span>{activity.duration}</span>
              </div>
              <div className="flex items-center">
                <FlameIcon className="mr-1 h-3 w-3" />
                <span>{activity.calories} cal</span>
              </div>
            </div>
          </div>
        </div>
      ))}
    </div>
  )
}

