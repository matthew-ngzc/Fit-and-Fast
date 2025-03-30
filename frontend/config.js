const API_BASE = process.env.REACT_APP_API_URL || "http://localhost:8080";
const config = {
  API_URL: API_BASE,
  AUTH_URL: `${API_BASE}/api/auth`,
  BOT_URL: `${API_BASE}/api/chatbot`,
  HISTORY_URL: `${API_BASE}/api/history`,
  CALENDAR_URL: `${API_BASE}/api/calendar`,
  HOME_URL: `${API_BASE}/api/home`,
  USER_URL: `${API_BASE}/api/users`,
  PROFILE_URL: `${API_BASE}/api/profile`,
  WORKOUT_URL: `${API_BASE}/api/workouts`,
  PROGRESS_URL: `${API_BASE}/api/workout-progress`
};

export default config;
