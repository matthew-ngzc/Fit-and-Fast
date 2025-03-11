import React from "react";
import { View, Text, TouchableOpacity, StyleSheet, ScrollView, Image } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { Colors, Fonts } from "../theme"; // Import your color theme
import { useRouter } from "expo-router";

// Dummy user data
const user = {
  name: "Jane Doe",
  age: 28,
  weight: "65 kg",
  height: "170 cm",
  bio: "Fitness enthusiast and yoga lover. Always striving to be the best version of myself!",
  photo: "https://via.placeholder.com/150", // Placeholder image URL
};

const ProfileScreen: React.FC = () => {
  const router = useRouter();

  const handleLogout = () => {
    // to have a pop up confirming logout
    // Here, you can clear any user authentication data (e.g., tokens, session data)
    // For example: AsyncStorage.clear() or other means of logging the user out

    // Navigate to the login page after logout
    router.push("/login");  // Make sure the path matches your login page route
  };

  return (
    <ScrollView style={styles.container}>
      {/* Profile Header */}
      <View style={styles.profileHeader}>
        <Image source={{ uri: user.photo }} style={styles.profilePhoto} />
        <Text style={styles.profileName}>{user.name}</Text>
        <Text style={styles.profileBio}>{user.bio}</Text>
      </View>

      {/* User Information */}
      <View style={styles.infoContainer}>
        <Text style={styles.sectionTitle}>Personal Information</Text>
        <View style={styles.infoItem}>
          <Ionicons name="person-outline" size={20} color={Colors.text} />
          <Text style={styles.infoText}>Age: {user.age}</Text>
        </View>
        <View style={styles.infoItem}>
          <Ionicons name="barbell-outline" size={20} color={Colors.text} />
          <Text style={styles.infoText}>Weight: {user.weight}</Text>
        </View>
        <View style={styles.infoItem}>
          <Ionicons name="resize-outline" size={20} color={Colors.text} />
          <Text style={styles.infoText}>Height: {user.height}</Text>
        </View>
      </View>

      {/* Settings */}
      <View style={styles.settingsContainer}>
        <Text style={styles.sectionTitle}>Settings</Text>
        <TouchableOpacity style={styles.settingItem}>
          <Ionicons name="create-outline" size={20} color={Colors.text} />
          <Text style={styles.settingText}>Edit Profile</Text>
          <Ionicons name="chevron-forward" size={20} color={Colors.text} />
        </TouchableOpacity>
        <TouchableOpacity style={styles.settingItem}>
          <Ionicons name="lock-closed-outline" size={20} color={Colors.text} />
          <Text style={styles.settingText}>Change Password</Text>
          <Ionicons name="chevron-forward" size={20} color={Colors.text} />
        </TouchableOpacity>
        <TouchableOpacity style={styles.settingItem}>
          <Ionicons name="notifications-outline" size={20} color={Colors.text} />
          <Text style={styles.settingText}>Notification Preferences</Text>
          <Ionicons name="chevron-forward" size={20} color={Colors.text} />
        </TouchableOpacity>
        <TouchableOpacity style={styles.settingItem} onPress={handleLogout}>
          <Ionicons name="log-out-outline" size={20} color={Colors.text} />
          <Text style={styles.settingText}>Logout</Text>
          <Ionicons name="chevron-forward" size={20} color={Colors.text} />
        </TouchableOpacity>
      </View>

      {/* Achievements */}
      <View style={styles.achievementsContainer}>
        <Text style={styles.sectionTitle}>Achievements</Text>
        <View style={styles.achievementItem}>
          <Ionicons name="trophy-outline" size={24} color={Colors.primary} />
          <Text style={styles.achievementText}>Completed 50 Workouts</Text>
        </View>
        <View style={styles.achievementItem}>
          <Ionicons name="trophy-outline" size={24} color={Colors.primary} />
          <Text style={styles.achievementText}>5-Day Streak</Text>
        </View>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.background,
    padding: 16,
  },
  profileHeader: {
    alignItems: "center",
    marginBottom: 24,
  },
  profilePhoto: {
    width: 120,
    height: 120,
    borderRadius: 60,
    marginBottom: 16,
  },
  profileName: {
    fontFamily: Fonts.body,
    fontSize: 24,
    fontWeight: "bold",
    color: Colors.text,
    marginBottom: 8,
  },
  profileBio: {
    fontSize: 14,
    color: Colors.inactive,
    textAlign: "center",
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: "bold",
    color: Colors.text,
    marginBottom: 16,
  },
  infoContainer: {
    marginBottom: 24,
  },
  infoItem: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: Colors.secondary,
    padding: 16,
    borderRadius: 8,
    marginBottom: 8,
    elevation: 2,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
  },
  infoText: {
    fontSize: 16,
    color: Colors.text,
    marginLeft: 8,
  },
  settingsContainer: {
    marginBottom: 24,
  },
  settingItem: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    backgroundColor: Colors.secondary,
    padding: 16,
    borderRadius: 8,
    marginBottom: 8,
    elevation: 2,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
  },
  settingText: {
    fontSize: 16,
    color: Colors.text,
    flex: 1,
    marginLeft: 8,
  },
  achievementsContainer: {
    marginBottom: 24,
  },
  achievementItem: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: Colors.secondary,
    padding: 16,
    borderRadius: 8,
    marginBottom: 8,
    elevation: 2,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
  },
  achievementText: {
    fontSize: 16,
    color: Colors.text,
    marginLeft: 8,
  },
});

export default ProfileScreen;