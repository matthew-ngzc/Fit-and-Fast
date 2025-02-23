import openai

# Load API key from environment variable
api_key = "api key"

# Ensure API key is set
if not api_key:
    raise ValueError("❌ OPENAI_API_KEY is not set. Please set it as an environment variable.")

# Create OpenAI client
client = openai.OpenAI(api_key=api_key)

# Define user's background information (to be filled dynamically in actual implementation)
user_profile = {
    "name": "Alice",
    "age": 26,
    "gender": "Female",
    "height": "165 cm",
    "weight": "60 kg",
    "fitness_level": "Intermediate",
    "fitness_goal": "Build endurance and tone muscles",
    "medical_history": "Mild knee pain, no major injuries",
    "workout_preferences": "Home workouts, minimal equipment",
    "menstrual_status": "Period started today",
    "default_workout_duration": 7,  # Default duration in minutes
    "current_workout": {
        "format": "50s work, 10s rest",
        "exercises": [
            "Jump Squats",
            "Push Ups",
            "Jumping Lunges",
            "Mountain Climbers",
            "Plank to Shoulder Tap",
            "Burpees",
            "Bicycle Crunches"
        ]
    }
}

# **Default AI Welcome Message (Preloaded)**
default_message = f"""
**Here is your current routine**\n
**Format:** {user_profile['current_workout']['format']}\n
- {chr(10).join(user_profile['current_workout']['exercises'])}\n
**How can I help you?**
"""

# Print the default AI message before calling OpenAI
print("\n🤖 AI (Preloaded):")
print(default_message)

# Generate system prompt with user context
system_prompt = f"""
You are an AI fitness trainer specializing in **time-efficient, effective workouts for busy women**. 
Your goal is to create **structured, clear workout plans** that match the app’s UI design.

### **Rules for AI Responses**
1. **Concise format** like:  
   **Here is your modified routine**  
   **Format:** 40s work, 20s rest  
   - Exercise 1  
   - Exercise 2  
   - Exercise 3  
   - ...  
   **Would you like to use this instead?**

2. **Avoid extra explanations.**  
   - No long descriptions.  
   - No extra justification.  
   - Keep it structured.

### **User Profile**
- **Age**: {user_profile['age']}
- **Gender**: {user_profile['gender']}
- **Fitness Level**: {user_profile['fitness_level']}
- **Fitness Goal**: {user_profile['fitness_goal']}
- **Medical History**: {user_profile['medical_history']}
- **Workout Preferences**: {user_profile['workout_preferences']}
- **Menstrual Status**: {user_profile['menstrual_status']}
- **Default Workout Duration**: {user_profile['default_workout_duration']} minutes

### **Current Workout Plan**
- **Format**: {user_profile['current_workout']['format']}
- **Exercises**: {', '.join(user_profile['current_workout']['exercises'])}

### **Modification Rules**
- If user requests **easier workouts (e.g., on period)** → Provide low-impact exercises.
- If user **wants longer workouts** → Ask if it’s a one-time change or permanent.
- If user **mentions injuries**, ask next time: *"How is your knee feeling today?"*

"""

# Get user input message
user_message = input("\n📝 User: ")

# Call OpenAI API with streaming
response = client.chat.completions.create(
    model="gpt-4o-mini",
    messages=[
        {"role": "system", "content": system_prompt},
        {"role": "user", "content": user_message}
    ],
    temperature=0.3,  # Lower temperature for structured response
    stream=True,  # Enable real-time response streaming
)

# Print streamed output
print("\n💬 AI Response:\n")
for chunk in response:
    if chunk.choices[0].delta.content:
        print(chunk.choices[0].delta.content, end="", flush=True)  # Print response live

print("\n\n✅ Streaming complete!")
