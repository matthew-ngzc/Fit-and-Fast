import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Bot } from "lucide-react"

export function AIChatButton() {
  return (
    <Link href="/chat">
      <Button variant="secondary" size="sm" className="gap-2 bg-pink-100 hover:bg-pink-200 text-primary">
        <Bot className="h-4 w-4" />
        <span>AI Assistant</span>
      </Button>
    </Link>
  )
}

