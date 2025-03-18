import { NextResponse } from "next/server"
import { promises as fs } from "fs"
import path from "path"

export async function GET() {
  try {
    // Read the JSON file from the public directory
    const filePath = path.join(process.cwd(), "public", "data.json")
    const jsonData = await fs.readFile(filePath, "utf8")
    const data = JSON.parse(jsonData)

    // Return the data as JSON
    return NextResponse.json(data)
  } catch (error) {
    console.error("Error reading data file:", error)
    return NextResponse.json({ error: "Failed to load data" }, { status: 500 })
  }
}

