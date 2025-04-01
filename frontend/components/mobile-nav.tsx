"use client"

import type React from "react"

import Link from "next/link"
import { usePathname } from "next/navigation"
import { CalendarIcon, HomeIcon, LineChartIcon, UserIcon } from "lucide-react"
import { cn } from "@/lib/utils"

export function MobileNav() {
  const pathname = usePathname()

  if (pathname?.startsWith("/auth") || pathname?.startsWith("/workout") || pathname?.startsWith("/chat")) {
    return null
  }

  return (
    <div className="md:hidden fixed bottom-0 left-0 z-50 w-full h-16 bg-background border-t">
      <div className="grid h-full max-w-lg grid-cols-4 mx-auto">
        <NavItem href="/home" icon={<HomeIcon className="w-6 h-6" />} label="Home" isActive={pathname === "/home"} />
        <NavItem
          href="/activity"
          icon={<LineChartIcon className="w-6 h-6" />}
          label="Activity"
          isActive={pathname === "/activity"}
        />
        <NavItem
          href="/calendar"
          icon={<CalendarIcon className="w-6 h-6" />}
          label="Calendar"
          isActive={pathname === "/calendar"}
        />
        <NavItem
          href="/profile"
          icon={<UserIcon className="w-6 h-6" />}
          label="Profile"
          isActive={pathname === "/profile"}
        />
      </div>
    </div>
  )
}

interface NavItemProps {
  href: string
  icon: React.ReactNode
  label: string
  isActive: boolean
}

function NavItem({ href, icon, label, isActive }: NavItemProps) {
  return (
    <Link
      href={href}
      className={cn(
        "inline-flex flex-col items-center justify-center px-5 hover:bg-muted/50 group",
        isActive && "text-primary",
      )}
    >
      {icon}
      <span className="text-xs mt-1">{label}</span>
    </Link>
  )
}

