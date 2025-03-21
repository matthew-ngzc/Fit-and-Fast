"use client";

import {
  Line,
  LineChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "@/components/ui/chart";
import { useEffect, useState } from "react";

// Define the structure of the data
type ActivityData = {
  date: string;
  calories: number;
};

// Define simplified fake data directly in the same file
const fakeData: ActivityData[] = [
  { date: "2023-03-15", calories: 120 },
  { date: "2023-03-16", calories: 150 },
  { date: "2023-03-17", calories: 0 },
  { date: "2023-03-18", calories: 180 },
  { date: "2023-03-19", calories: 200 },
  { date: "2023-03-20", calories: 90 },
  { date: "2023-03-21", calories: 120 },
];

export function ActivityLineChart() {
  const [data, setData] = useState<ActivityData[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setTimeout(() => {
      try {
        // Modify the date format before setting it in the state
        const formattedData = fakeData.map((item) => {
          const date = new Date(item.date);
          const formattedDate = `${date
            .getDate()
            .toString()
            .padStart(2, "0")}/${(date.getMonth() + 1)
            .toString()
            .padStart(2, "0")}`;
          return { ...item, date: formattedDate };
        });
        setData(formattedData);
      } catch (error) {
        console.error("Failed to load activity data:", error);
      } finally {
        setLoading(false);
      }
    }, );
  }, []);

  {/* API call to get DATE and CALORIES for the past 7 days to display on graph*/}
  // useEffect(() => {
  //   async function fetchActivityData() {
  //     try {
  //       const response = await fetch("/api/activity"); 
  //       const activityData = await response.json();
  //       setData(activityData); // Set the fetched data
  //     } catch (error) {
  //       console.error("Failed to load activity data:", error);
  //     } finally {
  //       setLoading(false);
  //     }
  //   }

  //   fetchActivityData(); 
  // }, []); 

  if (loading) {
    return (
      <div className="h-[300px] flex items-center justify-center">
        Loading data...
      </div>
    );
  }

  return (
    <div className="w-full h-[300px]">
      <ResponsiveContainer width="100%" height="100%">
        <LineChart data={data}>
          <XAxis
            dataKey="date"
            stroke="#888888"
            fontSize={12}
            tickLine={false}
            axisLine={false}
          />
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
  );
}
