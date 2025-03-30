import Image from "next/image";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { ClockIcon, FlameIcon } from "lucide-react";
import Link from "next/link";

interface WorkoutData {
  workoutId: number;
  name: string;
  durationInMinutes: number;
  level: string;
  calories: number;
  image: string;
  category: string;
  description: string;
  exercises: string[];
}

interface WorkoutCardProps {
  workoutData: WorkoutData;
  href?: string;
}

export function WorkoutCard({ workoutData, href }: WorkoutCardProps) {

  const handleClick = () => {
    localStorage.setItem("currentWorkout", JSON.stringify(workoutData));
  };

  const content = (
    <Card className="overflow-hidden">
      <div className="relative h-48">
        <Image
          src={workoutData.image || "/placeholder.svg"}
          alt={workoutData.name}
          fill
          className="object-cover"
        />
      </div>
      <CardContent className="p-4">
        <div className="flex justify-between items-center mb-2">
          <h3 className="font-semibold">{workoutData.name}</h3>
          <div className="flex items-center text-xs text-muted-foreground">
            <ClockIcon className="h-3 w-3 mr-1" />
            {workoutData.durationInMinutes}
          </div>
        </div>
        <div className="flex justify-between items-center">
          <div className="text-xs text-muted-foreground">
            {workoutData.level}
          </div>
          <div className="flex items-center text-xs text-muted-foreground">
            <FlameIcon className="h-3 w-3 mr-1" />
            {workoutData.calories} cal
          </div>
        </div>
      </CardContent>
      <CardFooter className="p-4 pt-0">
        <Button variant="outline" size="sm" className="w-full">
          Start Workout
        </Button>
      </CardFooter>
    </Card>
  );

  if (href) {
    return (
      <Link href={href} className="block" onClick={handleClick}>
        {content}
      </Link>
    );
  }

  return content;
}
