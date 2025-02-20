import openai

# Replace with your actual OpenAI API key
api_key = "api key here"

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
    "last_workout_routine": [
        "Jump Squats",
        "Push Ups",
        "Jumping Lunges",
        "Mountain Climbers",
        "Plank to Shoulder Tap",
        "Burpees",
        "Bicycle Crunches"
    ]
}

# Generate system prompt with user context
system_prompt = f"""
You are an AI fitness trainer specializing in personalized workout plans for women. 
Use the provided user profile to generate workouts that suit their fitness level, health conditions, and preferences. 

User Profile:
- Age: {user_profile['age']}
- Gender: {user_profile['gender']}
- Height: {user_profile['height']}
- Weight: {user_profile['weight']}
- Fitness Level: {user_profile['fitness_level']}
- Fitness Goal: {user_profile['fitness_goal']}
- Medical History: {user_profile['medical_history']}
- Workout Preferences: {user_profile['workout_preferences']}
- Menstrual Status: {user_profile['menstrual_status']}
- Last Workout Routine: {', '.join(user_profile['last_workout_routine'])}

If the user requests an easier routine due to their period, modify the existing routine to reduce high-impact movements and focus on gentler, low-impact exercises.
"""

# User input message (simulating user asking for a period-friendly routine)
user_message = "My period just started today, I would like to have easier workouts."

# Call OpenAI API
response = client.chat.completions.create(
    model="gpt-4o-mini",
    messages=[
        {"role": "system", "content": system_prompt},
        {"role": "user", "content": user_message}
    ],
    temperature=0.7,
    stream=True,
)

# Print streamed output
print("\n💬 AI Response:\n")
for chunk in response:
    if chunk.choices[0].delta.content:
        print(chunk.choices[0].delta.content, end="", flush=True)  # Print in real-time

print("\n\n✅ Streaming complete!")
