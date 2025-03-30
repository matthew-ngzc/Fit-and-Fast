"use client";

import { useState, useRef, useEffect } from "react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { ArrowLeft, Bot, Send, User, Check } from "lucide-react";
import config from "@/config";

type Message = {
  id: string;
  content: string;
  sender: "user" | "bot";
  timestamp: Date;
  showActions?: boolean;
  workoutId?: string;
};

type Exercise = {
  id: string;
  name: string;
  sets: number;
  reps: number;
  duration?: string;
};

interface WorkoutExercise {
  name: string;
  duration: number;
  rest: number;
}

interface WorkoutData {
  calories: number | null;
  category: string;
  description: string;
  durationInMinutes: number;
  image: string | null;
  level: string;
  name: string;
  workoutExercise: WorkoutExercise[];
}

export default function ChatPage() {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: "1",
      content:
        "Hi there! I'm your Fit&Fast AI assistant. How can I help you with your fitness journey today?",
      sender: "bot",
      timestamp: new Date(),
    },
  ]);
  const [inputValue, setInputValue] = useState("");
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const saveWorkoutToLocalStorage = (workoutData: WorkoutData): void => {
    localStorage.setItem("workout", JSON.stringify(workoutData));
  };

  // Sample quick questions
  const quickQuestions = [
    "Can you lower the difficulty because it's my period?",
    "How can I make the workout more intense?",
    "Generate a 20-minute workout for me",
  ];

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  const getCurrentWorkout = () => {
    if (typeof window !== "undefined") {
      const currentWorkoutStr = localStorage.getItem("currentWorkout");
      return currentWorkoutStr ? JSON.parse(currentWorkoutStr) : null;
    }
    return null;
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSend = async () => {
    if (inputValue.trim() === "" || loading) return;

    // Add user message
    const userMessage: Message = {
      id: Date.now().toString(),
      content: inputValue,
      sender: "user",
      timestamp: new Date(),
    };

    setMessages([...messages, userMessage]);
    setInputValue("");
    setLoading(true);

    try {
      const botResponse = await getBotResponse(inputValue);
      setMessages((prev) => [...prev, botResponse]);
    } catch (error) {
      console.error("Error in handleSend:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleQuickQuestion = async (question: string) => {
    if (loading) return;

    const userMessage: Message = {
      id: Date.now().toString(),
      content: question,
      sender: "user",
      timestamp: new Date(),
    };

    setMessages([...messages, userMessage]);
    setLoading(true);

    try {
      const botResponse = await getBotResponse(question);
      setMessages((prev) => [...prev, botResponse]);
    } catch (error) {
      console.error("Error in handleQuickQuestion:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleWorkoutAction = async (accept: boolean, workoutId: string) => {
    // Remove action buttons from the message
    setMessages(
      messages.map((msg) =>
        msg.workoutId === workoutId ? { ...msg, showActions: false } : msg
      )
    );

    if (accept) {
      // Add user acceptance message
      const userMessage: Message = {
        id: Date.now().toString(),
        content: "I'll try this workout!",
        sender: "user",
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, userMessage]);

      // Show loading state
      setLoading(true);

      try {
        const userId = localStorage.getItem("userId") || "default";
        const token = localStorage.getItem("token") || "";
        const workoutData = JSON.parse(localStorage.getItem("workout") || "{}");
        console.log("Returning: " + JSON.stringify(workoutData, null, 2));
        // Make API call to save the workout (you'll need to implement this endpoint)
        const response = await fetch(`${config.BOT_URL}/${userId}/accept`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(workoutData),
        });

        if (!response.ok) {
          throw new Error("Failed to save workout");
        }

        const data = await response.json();

        localStorage.removeItem("workout"); // Clear previous workout
        localStorage.setItem("currentWorkout", JSON.stringify(workoutData));

        // Show confirmation message from bot
        const confirmationMessage: Message = {
          id: Date.now().toString(),
          content: `Great! I've added this workout to your routine. Redirecting you to your personalised workout plan...`,
          sender: "bot",
          timestamp: new Date(),
        };

        setMessages((prev) => [...prev, confirmationMessage]);

        // Redirect to workout page after a short delay
        setTimeout(() => {
          window.location.href = "/workout/0";
        }, 2000); // 2 seconds delay before redirection

      } catch (error) {
        console.error("Error saving workout:", error);
        const errorMessage: Message = {
          id: Date.now().toString(),
          content:
            "Sorry, there was an error saving your workout. Please try again later.",
          sender: "bot",
          timestamp: new Date(),
        };

        setMessages((prev) => [...prev, errorMessage]);
      } finally {
        setLoading(false);
      }
    } else {
      // Add rejection message
      const userMessage: Message = {
        id: Date.now().toString(),
        content: "I'd like a different workout.",
        sender: "user",
        timestamp: new Date(),
      };

      setMessages((prev) => [...prev, userMessage]);

      // Get bot response for the rejection
      setLoading(true);
      try {
        const botResponse = await getBotResponse(
          "I'd like a different workout. Can you suggest an alternative?"
        );
        setMessages((prev) => [...prev, botResponse]);
      } catch (error) {
        console.error("Error getting alternative workout:", error);
        const feedbackMessage: Message = {
          id: Date.now().toString(),
          content:
            "No problem! Could you tell me what you'd like to change about the workout? Would you prefer something less intense, different exercises, or a different focus area?",
          sender: "bot",
          timestamp: new Date(),
        };
        setMessages((prev) => [...prev, feedbackMessage]);
      } finally {
        setLoading(false);
      }
    }
  };

  // Enhanced bot response logic with workout generation
  const getBotResponse = async (message: string): Promise<Message> => {
    try {
      const userId = localStorage.getItem("userId") || "default";
      const token = localStorage.getItem("token") || "";
      const currentWorkout = getCurrentWorkout();

      const response = await fetch(`${config.BOT_URL}/${userId}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          message: message,
          exercises: currentWorkout?.workoutExercise || [],
        }),
      });

      if (!response.ok) {
        throw new Error("Failed to get response from chatbot");
      }

      const data = await response.json();
      console.log("Bot response data:", data);
      const workoutData = data.workout;
      saveWorkoutToLocalStorage(workoutData);

      // Check if response includes a workout
      const showActions = !!data.workout;
      const workoutId = data.workout?.workoutId?.toString() || "";

      return {
        id: Date.now().toString(),
        content: data.response,
        sender: "bot",
        timestamp: new Date(),
        showActions,
        workoutId,
      };
    } catch (error) {
      console.error("Error getting bot response:", error);
      return {
        id: Date.now().toString(),
        content:
          "Sorry, I'm having trouble connecting right now. Please try again later.",
        sender: "bot",
        timestamp: new Date(),
      };
    }
  };

  return (
    <div className="container px-4 py-6 md:py-10 max-w-2xl mx-auto flex flex-col h-[calc(100vh-80px)]">
      <div className="flex items-center gap-2 mb-4">
        <Link
          href="/home"
          className="text-muted-foreground hover:text-foreground"
        >
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
          <CardDescription>
            Ask questions about your workouts or get personalized workouts
          </CardDescription>
        </CardHeader>
        <CardContent className="flex-1 p-0 overflow-hidden">
          <div className="h-[calc(100vh-280px)] overflow-y-auto px-4">
            <div className="space-y-4 py-4">
              {messages.map((message) => (
                <div
                  key={message.id}
                  className={`flex ${
                    message.sender === "user" ? "justify-end" : "justify-start"
                  }`}
                >
                  <div
                    className={`
                      max-w-[80%] rounded-lg p-3 
                      ${
                        message.sender === "user"
                          ? "bg-primary text-primary-foreground"
                          : "bg-muted/80 border"
                      }
                    `}
                  >
                    <div className="flex items-center gap-2 mb-1">
                      {message.sender === "bot" ? (
                        <Bot className="h-4 w-4" />
                      ) : (
                        <User className="h-4 w-4" />
                      )}
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
                          onClick={() =>
                            handleWorkoutAction(true, message.workoutId || "")
                          }
                        >
                          <Check className="h-4 w-4" /> Accept
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
                  handleSend();
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
  );
}
