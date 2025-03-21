"use client"

import { useState, useRef, useEffect } from "react"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { ArrowLeft, Bot, Send, User, Check, X } from "lucide-react"

type Message = {
  id: string
  content: string
  sender: "user" | "bot"
  timestamp: Date
  showActions?: boolean
  workoutId?: string
}

type Exercise = {
  id: string
  name: string
  sets: number
  reps: number
  duration?: string
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
  const [loading, setLoading] = useState(false)
  const messagesEndRef = useRef<HTMLDivElement>(null)

  // Sample quick questions 
  const quickQuestions = [
    "Can you lower the difficulty because it's my period?",
    "How can I make the workout more intense?",
    "Generate a 20-minute workout for me",
  ];

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" })
  }

  useEffect(() => {
    scrollToBottom()
  }, [messages])

  const handleSend = () => {
    if (inputValue.trim() === "" || loading) return

    // Add user message
    const userMessage: Message = {
      id: Date.now().toString(),
      content: inputValue,
      sender: "user",
      timestamp: new Date(),
    }

    setMessages([...messages, userMessage])
    setInputValue("")
    setLoading(true)

    // Simulate bot response after a short delay
    setTimeout(() => {
      const botResponse = getBotResponse(inputValue)
      setMessages((prev) => [...prev, botResponse])
      setLoading(false)
    }, 1000)
  }

  const handleQuickQuestion = (question: string) => {
    if (loading) return
    
    const userMessage: Message = {
      id: Date.now().toString(),
      content: question,
      sender: "user",
      timestamp: new Date(),
    }

    setMessages([...messages, userMessage])
    setLoading(true)

    // Simulate bot response after a short delay
    setTimeout(() => {
      const botResponse = getBotResponse(question)
      setMessages((prev) => [...prev, botResponse])
      setLoading(false)
    }, 1000)
  }

  const handleWorkoutAction = async (accept: boolean, workoutId: string) => {
    // Remove action buttons from the message
    setMessages(messages.map(msg => 
      msg.workoutId === workoutId ? {...msg, showActions: false} : msg
    ))

    if (accept) {
      // Add user acceptance message
      const userMessage: Message = {
        id: Date.now().toString(),
        content: "I'll try this workout!",
        sender: "user",
        timestamp: new Date(),
      }
      setMessages(prev => [...prev, userMessage])
      
      // Show loading state
      setLoading(true)
      
      // Simulate API call to get exercise IDs
      try {
        // In a real app, this would be an actual API call:
        // const response = await fetch('/api/workouts/accept', {
        //   method: 'POST',
        //   headers: { 'Content-Type': 'application/json' },
        //   body: JSON.stringify({ workoutId })
        // });
        // const data = await response.json();
        
        // Simulate API response with exercise IDs
        setTimeout(() => {
          const exerciseIds = ["ex123", "ex456", "ex789", "ex101", "ex202"] 
          
          // Show confirmation message from bot
          const confirmationMessage: Message = {
            id: Date.now().toString(),
            content: `Great! I've added this workout to your routine. The exercises (IDs: ${exerciseIds.join(", ")}) are now available in your workout plan. Would you like me to explain any of these exercises in detail?`,
            sender: "bot",
            timestamp: new Date(),
          }
          
          setMessages(prev => [...prev, confirmationMessage])
          setLoading(false)
        }, 1000)
      } catch (error) {
        // Handle any errors
        const errorMessage: Message = {
          id: Date.now().toString(),
          content: "Sorry, there was an error saving your workout. Please try again later.",
          sender: "bot",
          timestamp: new Date(),
        }
        
        setMessages(prev => [...prev, errorMessage])
        setLoading(false)
      }
    } else {
      // Add rejection message
      const userMessage: Message = {
        id: Date.now().toString(),
        content: "I'd like a different workout.",
        sender: "user",
        timestamp: new Date(),
      }
      
      setMessages(prev => [...prev, userMessage])
      
      // Bot asks for feedback
      setTimeout(() => {
        const feedbackMessage: Message = {
          id: Date.now().toString(),
          content: "No problem! Could you tell me what you'd like to change about the workout? Would you prefer something less intense, different exercises, or a different focus area?",
          sender: "bot",
          timestamp: new Date(),
        }
        
        setMessages(prev => [...prev, feedbackMessage])
      }, 800)
    }
  }

  // Enhanced bot response logic with workout generation
  const getBotResponse = (message: string): Message => {
    const lowerMessage = message.toLowerCase();
    let responseContent = "";
    let showActions = false;
    let workoutId = "";

    if (lowerMessage.includes("generate") || lowerMessage.includes("workout") || lowerMessage.includes("routine") || lowerMessage.includes("exercise")) {
      // Generate a sample workout routine
      workoutId = "workout-" + Date.now().toString();
      responseContent = `Here's a personalized 20-minute workout routine for you:

**Warm-up (3 minutes)**
• Jumping jacks - 30 seconds
• Arm circles - 30 seconds
• High knees - 30 seconds
• Torso twists - 30 seconds
• Light jogging in place - 1 minute

**Main Workout (15 minutes)**
• Bodyweight squats - 3 sets of 12 reps
• Push-ups (modified if needed) - 3 sets of 10 reps
• Alternating lunges - 3 sets of 10 reps per leg
• Plank - 3 sets of 30 seconds
• Mountain climbers - 3 sets of 20 reps

**Cool Down (2 minutes)**
• Gentle stretching for major muscle groups

Would you like to add this workout to your routine?`;
      showActions = true;
    } else if (lowerMessage.includes("modify") || lowerMessage.includes("lower impact")) {
      responseContent = "I can help modify your workout! I recommend replacing jumping exercises with marching in place, and high-impact movements with their low-impact alternatives. Would you like me to create a custom low-impact workout for you?"
    } else if (lowerMessage.includes("knee pain") || lowerMessage.includes("avoid")) {
      responseContent = "With knee pain, it's best to avoid deep squats, lunges, and high-impact exercises like jumping. Instead, focus on swimming, cycling, and upper body workouts. Always consult with a healthcare provider for persistent pain."
    } else if (lowerMessage.includes("period") || lowerMessage.includes("menstrual")) {
      // Generate a period-friendly workout
      workoutId = "workout-" + Date.now().toString();
      responseContent = `Here's a gentle workout that's more suitable during your period:

**Warm-up (5 minutes)**
• Gentle walking in place - 2 minutes
• Shoulder rolls - 1 minute
• Gentle side stretches - 2 minutes

**Main Workout (10 minutes)**
• Modified cat-cow stretches - 2 minutes
• Seated overhead stretches - 3 sets of 30 seconds
• Gentle core engagement (seated) - 3 sets of 10 reps
• Light arm raises with or without light weights - 3 sets of 12 reps
• Seated leg extensions - 3 sets of 10 reps

**Cool Down (5 minutes)**
• Deep breathing exercises
• Gentle full-body stretching

This workout avoids intense abdominal exercises and high-impact movements. Would you like to try this workout?`;
      showActions = true;
    } else if (lowerMessage.includes("intense") || lowerMessage.includes("harder") || lowerMessage.includes("challenge")) {
      // Generate a more intense workout
      workoutId = "workout-" + Date.now().toString();
      responseContent = `Here's a more intense 25-minute HIIT workout:

**Warm-up (5 minutes)**
• Jumping jacks - 1 minute
• High knees - 1 minute
• Butt kicks - 1 minute
• Dynamic stretches - 2 minutes

**HIIT Circuit (15 minutes) - 30 seconds work, 15 seconds rest**
• Burpees
• Mountain climbers
• Jump squats
• Push-up to side plank
• Speed skaters
• Repeat circuit 3 times

**Finisher (3 minutes)**
• Plank challenge - hold as long as possible
• 20 jumping lunges

**Cool Down (2 minutes)**
• Full-body stretching

Would you like to add this challenging workout to your routine?`;
      showActions = true;
    } else {
      responseContent = "I understand you're asking about \"" + message + "\". Is there a specific type of workout you're interested in? I can create personalized routines for different goals and fitness levels."
    }

    return {
      id: Date.now().toString(),
      content: responseContent,
      sender: "bot",
      timestamp: new Date(),
      showActions,
      workoutId,
    }
  }

  return (
    <div className="container px-4 py-6 md:py-10 max-w-2xl mx-auto flex flex-col h-[calc(100vh-80px)]">
      <div className="flex items-center gap-2 mb-4">
        <Link href="/" className="text-muted-foreground hover:text-foreground">
          <ArrowLeft className="h-5 w-5" />
        </Link>
        <h1 className="text-2xl font-bold">Fitness AI Assistant</h1>
      </div>

      <Card className="flex-1 flex flex-col">
        <CardHeader className="pb-2">
          <CardTitle className="flex items-center gap-2">
            <Bot className="h-5 w-5 text-primary" />
            FitBuddy
          </CardTitle>
          <CardDescription>Ask questions about your workouts or get personalized workouts</CardDescription>
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
                        {message.sender === "bot" ? "FitBuddy" : "You"}
                      </span>
                    </div>
                    <p className="whitespace-pre-line">{message.content}</p>
                    
                    {message.showActions && (
                      <div className="flex gap-2 mt-3 justify-end">
                        <Button 
                          size="sm" 
                          variant="outline" 
                          className="flex items-center gap-1"
                          onClick={() => handleWorkoutAction(true, message.workoutId || "")}
                        >
                          <Check className="h-4 w-4" /> Accept
                        </Button>
                        <Button 
                          size="sm" 
                          variant="outline" 
                          className="flex items-center gap-1 text-destructive"
                          onClick={() => handleWorkoutAction(false, message.workoutId || "")}
                        >
                          <X className="h-4 w-4" /> Reject
                        </Button>
                      </div>
                    )}
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
                disabled={loading}
              >
                {question}
              </Button>
            ))}
          </div>
          <div className="flex w-full gap-2">
            <Input
              placeholder={loading ? "Thinking..." : "Type your message..."}
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter" && !loading) {
                  handleSend()
                }
              }}
              disabled={loading}
            />
            <Button size="icon" onClick={handleSend} disabled={loading}>
              <Send className="h-4 w-4" />
            </Button>
          </div>
        </CardFooter>
      </Card>
    </div>
  )
}