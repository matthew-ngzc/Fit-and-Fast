"use client"

import { useState, useRef, useEffect } from "react"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { ArrowLeft, Bot, Send, User } from "lucide-react"

type Message = {
  id: string
  content: string
  sender: "user" | "bot"
  timestamp: Date
}

export default function ChatPage() {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: "1",
      content: "Hi there! I'm your Fit&Fast AI assistant. How can I help you with your fitness journey today?",
      sender: "bot",
      timestamp: new Date(),
    },
  ])
  const [inputValue, setInputValue] = useState("")
  const messagesEndRef = useRef<HTMLDivElement>(null)

  // Sample quick questions - reduced to 2
  const quickQuestions = [
    "Can you modify my workout to be lower impact?",
    "How can I adjust workouts during my period?",
  ]

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" })
  }

  useEffect(() => {
    scrollToBottom()
  }, [messages])

  const handleSend = () => {
    if (inputValue.trim() === "") return

    // Add user message
    const userMessage: Message = {
      id: Date.now().toString(),
      content: inputValue,
      sender: "user",
      timestamp: new Date(),
    }

    setMessages([...messages, userMessage])
    setInputValue("")

    // Simulate bot response after a short delay
    setTimeout(() => {
      const botResponse: Message = {
        id: (Date.now() + 1).toString(),
        content: getBotResponse(inputValue),
        sender: "bot",
        timestamp: new Date(),
      }
      setMessages((prev) => [...prev, botResponse])
    }, 1000)
  }

  const handleQuickQuestion = (question: string) => {
    setInputValue(question)
    handleSend()
  }

  // Simple bot response logic - in a real app this would be connected to an AI service
  const getBotResponse = (message: string): string => {
    const lowerMessage = message.toLowerCase()

    if (lowerMessage.includes("modify") || lowerMessage.includes("lower impact")) {
      return "I can help modify your workout! I recommend replacing jumping exercises with marching in place, and high-impact movements with their low-impact alternatives. Would you like me to create a custom low-impact workout for you?"
    }

    if (lowerMessage.includes("knee pain") || lowerMessage.includes("avoid")) {
      return "With knee pain, it's best to avoid deep squats, lunges, and high-impact exercises like jumping. Instead, focus on swimming, cycling, and upper body workouts. Always consult with a healthcare provider for persistent pain."
    }

    if (lowerMessage.includes("period") || lowerMessage.includes("menstrual")) {
      return "During your period, you might want to focus on lighter exercises like yoga, walking, or swimming. Listen to your body and reduce intensity if you experience cramps or discomfort. Some find that gentle movement actually helps relieve period symptoms."
    }

    return (
      "I understand you're asking about \"" +
      message +
      "\". I'm still learning about fitness topics. Is there something specific about your workout routine or fitness goals you'd like help with?"
    )
  }

  return (
    <div className="container px-4 py-6 md:py-10 max-w-2xl mx-auto flex flex-col h-[calc(100vh-80px)]">
      <div className="flex items-center gap-2 mb-4">
        <Link href="/" className="text-muted-foreground hover:text-foreground">
          <ArrowLeft className="h-5 w-5" />
        </Link>
        <h1 className="text-2xl font-bold">Fitness Assistant</h1>
      </div>

      <Card className="flex-1 flex flex-col">
        <CardHeader className="pb-2">
          <CardTitle className="flex items-center gap-2">
            <Bot className="h-5 w-5 text-primary" />
            AI Fitness Chat
          </CardTitle>
          <CardDescription>Ask questions about your workouts or get personalized advice</CardDescription>
        </CardHeader>
        <CardContent className="flex-1 p-0 overflow-hidden">
          <div className="h-[calc(100vh-280px)] overflow-y-auto px-4">
            <div className="space-y-4 py-4">
              {messages.map((message) => (
                <div key={message.id} className={`flex ${message.sender === "user" ? "justify-end" : "justify-start"}`}>
                  <div
                    className={`
                      max-w-[80%] rounded-lg p-3 
                      ${message.sender === "user" ? "bg-primary text-primary-foreground" : "bg-muted/80 border"}
                    `}
                  >
                    <div className="flex items-center gap-2 mb-1">
                      {message.sender === "bot" ? <Bot className="h-4 w-4" /> : <User className="h-4 w-4" />}
                      <span className="text-xs opacity-70">
                        {message.sender === "bot" ? "Fitness Assistant" : "You"}
                      </span>
                    </div>
                    <p>{message.content}</p>
                  </div>
                </div>
              ))}
              <div ref={messagesEndRef} />
            </div>
          </div>
        </CardContent>
        <CardFooter className="flex flex-col gap-4 border-t bg-background pt-4">
          <div className="flex flex-wrap gap-2">
            {quickQuestions.map((question, index) => (
              <Button
                key={index}
                variant="outline"
                size="sm"
                onClick={() => handleQuickQuestion(question)}
                className="text-sm"
              >
                {question}
              </Button>
            ))}
          </div>
          <div className="flex w-full gap-2">
            <Input
              placeholder="Type your message..."
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  handleSend()
                }
              }}
            />
            <Button size="icon" onClick={handleSend}>
              <Send className="h-4 w-4" />
            </Button>
          </div>
        </CardFooter>
      </Card>
    </div>
  )
}

