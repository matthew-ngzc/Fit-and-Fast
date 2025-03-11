import { Stack } from "expo-router";
import { useEffect, useState } from "react";
import { useRouter } from "expo-router";
import AsyncStorage from "@react-native-async-storage/async-storage";

export default function Layout() {
  const router = useRouter();
  const [isAuthenticated, setIsAuthenticated] = useState(false); // Set to true by default
  const [loading, setLoading] = useState(false); // Set loading to false since we're bypassing auth
  
  useEffect(() => {
    const checkAuth = async () => {
      const token = await AsyncStorage.getItem("authToken"); 
      if (token) setIsAuthenticated(true);
      else router.replace("/login"); 
      setLoading(false);
    };
    checkAuth();
  }, []);
  

  if (loading) return null; 

  return (
    <Stack screenOptions={{ headerShown: false }}>
      {!isAuthenticated ? (
        <>
          <Stack.Screen name="login"/>
          <Stack.Screen name="signup"/>
        </>
      ) : (
        <Stack.Screen name="(tabs)" />
      )}
    </Stack>
  );
}