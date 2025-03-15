import type React from "react"
import { Card, CardContent } from "@/components/ui/card"
import Link from "next/link"

interface CategoryCardProps {
  title: string
  description: string
  icon: React.ReactNode
  color: string
  textColor: string
}

export function CategoryCard({ title, description, icon, color, textColor }: CategoryCardProps) {
  return (
    <Link href="#">
      <Card className="overflow-hidden transition-all hover:shadow-md">
        <CardContent className="p-4">
          <div className="flex flex-col gap-2">
            <div className={`${color} ${textColor} rounded-full w-10 h-10 flex items-center justify-center`}>
              {icon}
            </div>
            <div>
              <h3 className="font-semibold">{title}</h3>
              <p className="text-xs text-muted-foreground">{description}</p>
            </div>
          </div>
        </CardContent>
      </Card>
    </Link>
  )
}

