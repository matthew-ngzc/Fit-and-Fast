"use client";

import type React from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";

export function MainNav() {
  const pathname = usePathname();

  if (
    pathname?.startsWith("/auth") ||
    pathname?.startsWith("/workout") ||
    pathname?.startsWith("/chat")
  ) {
    return null;
  }

  return (
    <div className="mr-4 hidden md:flex">
      <Link href="/" className="mr-6 flex items-center space-x-2">
        <img src="/icon.png" alt="Fit&Fast" className="h-6 w-6" />
        <span className="hidden font-bold sm:inline-block">Fit&Fast</span>
      </Link>
      <nav className="flex items-center space-x-6 text-sm font-medium">
        <Link
          href="/home"
          className="transition-colors hover:text-foreground/80 text-foreground"
        >
          Home
        </Link>
        <Link
          href="/activity"
          className="transition-colors hover:text-foreground/80 text-muted-foreground"
        >
          Activity
        </Link>
        <Link
          href="/calendar"
          className="transition-colors hover:text-foreground/80 text-muted-foreground"
        >
          Calendar
        </Link>
        <Link
          href="/profile"
          className="transition-colors hover:text-foreground/80 text-muted-foreground"
        >
          Profile
        </Link>
      </nav>
    </div>
  );
}
