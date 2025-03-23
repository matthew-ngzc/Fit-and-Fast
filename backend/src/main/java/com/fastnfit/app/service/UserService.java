// UserService.java
package com.fastnfit.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fastnfit.app.dto.UserDTO;
import com.fastnfit.app.dto.UserDetailsDTO;
import com.fastnfit.app.dto.UserRegistrationDTO;
import com.fastnfit.app.dto.LoginRequestDTO;
import com.fastnfit.app.dto.ProfileDTO;
import com.fastnfit.app.dto.GoalsDTO;
import com.fastnfit.app.dto.AuthResponseDTO;
import com.fastnfit.app.dto.AvatarDTO;
import com.fastnfit.app.dto.WeeklyWorkoutsDTO;
import com.fastnfit.app.enums.PregnancyStatus;
import com.fastnfit.app.enums.WorkoutGoal;
import com.fastnfit.app.enums.WorkoutType;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.UserDetails;
import com.fastnfit.app.repository.UserDetailsRepository;
import com.fastnfit.app.repository.UserRepository;
import com.fastnfit.app.repository.HistoryRepository;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final HistoryRepository historyRepository;
    private final UserAchievementService userAchievementService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    public UserService(UserRepository userRepository,
            UserDetailsRepository userDetailsRepository,
            HistoryRepository historyRepository,
            UserAchievementService userAchievementService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.historyRepository = historyRepository;
        this.userAchievementService = userAchievementService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponseDTO registerUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        // Create basic user with encrypted password
        User user = new User();
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));

        // Use the createUser method to save user, initialize UserDetails if needed,
        // and create achievements
        User savedUser = createUser(user,registrationDTO.getUsername());

        String token = jwtService.generateToken(savedUser.getUserId());

        return AuthResponseDTO.builder()
                .token(token)
                .build();
    }

    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getUserId());

        return AuthResponseDTO.builder()
                .token(token)
                .build();
    }

    @Transactional
    public UserDetailsDTO completeUserQuestionnaire(Long userId, UserDetailsDTO detailsDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user details already exist
        Optional<UserDetails> existingDetails = userDetailsRepository.findByUser(user);
        UserDetails userDetails;

        if (existingDetails.isPresent()) {
            userDetails = existingDetails.get();
        } else {
            userDetails = new UserDetails();
        }

        // Update user details with questionnaire data
        userDetails.setUser(user);
        userDetails.setUsername(detailsDTO.getUsername());
        userDetails.setDob(detailsDTO.getDob());
        userDetails.setHeight(detailsDTO.getHeight());
        userDetails.setWeight(detailsDTO.getWeight());
        userDetails.setPregnancyStatus(detailsDTO.getPregnancyStatus());
        userDetails.setWorkoutGoal(detailsDTO.getWorkoutGoal());
        userDetails.setWorkoutDays(detailsDTO.getWorkoutDays());
        userDetails.setFitnessLevel(detailsDTO.getFitnessLevel());
        userDetails.setMenstrualCramps(detailsDTO.getMenstrualCramps());
        userDetails.setCycleBasedRecommendations(detailsDTO.getCycleBasedRecommendations());
        userDetails.setWorkoutType(detailsDTO.getWorkoutType());

        userDetailsRepository.save(userDetails);

        return detailsDTO;
    }

    public UserDetailsDTO getUserDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = userDetailsRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User details not found"));

        UserDetailsDTO dto = new UserDetailsDTO();
        dto.setUserId(userDetails.getUser().getUserId());
        dto.setUsername(userDetails.getUsername());
        dto.setDob(userDetails.getDob());
        dto.setHeight(userDetails.getHeight());
        dto.setWeight(userDetails.getWeight());
        dto.setPregnancyStatus(userDetails.getPregnancyStatus().getValue());
        dto.setWorkoutGoal(userDetails.getWorkoutGoal().getValue());
        dto.setWorkoutDays(userDetails.getWorkoutDays());
        dto.setFitnessLevel(userDetails.getFitnessLevel());
        dto.setMenstrualCramps(userDetails.getMenstrualCramps());
        dto.setCycleBasedRecommendations(userDetails.getCycleBasedRecommendations());
        dto.setWorkoutType(userDetails.getWorkoutType().getValue());

        return dto;
    }

    @Transactional
    public UserDetailsDTO updateUserDetails(Long userId, UserDetailsDTO detailsDTO) {
        // Find the user first
        if (detailsDTO==null || detailsDTO.getUsername()==null){
            throw new RuntimeException("DTO is invalid");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Find user details by user, not by userId
        UserDetails userDetails = userDetailsRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User details not found"));

        // Update fields
        userDetails.setUsername(detailsDTO.getUsername());

        if (detailsDTO.getDob() != null) {
            userDetails.setDob(detailsDTO.getDob());
        }

        if (detailsDTO.getHeight() != null) {
            userDetails.setHeight(detailsDTO.getHeight());
        }

        if (detailsDTO.getWeight() != null) {
            userDetails.setWeight(detailsDTO.getWeight());
        }

        if (detailsDTO.getPregnancyStatus() != null) {
            try {
                // First try direct enum mapping
                userDetails.setPregnancyStatus(PregnancyStatus.valueOf(detailsDTO.getPregnancyStatus()));
            } catch (IllegalArgumentException e) {
                // If that fails, try matching by value (assuming you have a fromValue method)
                userDetails.setPregnancyStatus(PregnancyStatus.fromValue(detailsDTO.getPregnancyStatus()));
            }
        }

        if (detailsDTO.getWorkoutGoal() != null) {
            try {
                // First try direct enum mapping
                userDetails.setWorkoutGoal(WorkoutGoal.valueOf(detailsDTO.getWorkoutGoal()));
            } catch (IllegalArgumentException e) {
                // If that fails, try matching by value
                userDetails.setWorkoutGoal(WorkoutGoal.fromString(detailsDTO.getWorkoutGoal()));
            }
        }

        if (detailsDTO.getWorkoutDays() != null) {
            userDetails.setWorkoutDays(detailsDTO.getWorkoutDays());
        }

        if (detailsDTO.getFitnessLevel() != null) {
            userDetails.setFitnessLevel(detailsDTO.getFitnessLevel());
        }

        if (detailsDTO.getWorkoutType() != null) {
            try {
                // First try direct enum mapping
                userDetails.setWorkoutType(WorkoutType.valueOf(detailsDTO.getWorkoutType()));
            } catch (IllegalArgumentException e) {
                // If that fails, try matching by value
                userDetails.setWorkoutType(WorkoutType.fromString(detailsDTO.getWorkoutType()));
            }
        }

        userDetails.setMenstrualCramps(detailsDTO.getMenstrualCramps());
        userDetails.setCycleBasedRecommendations(detailsDTO.getCycleBasedRecommendations());
        userDetails.setUser(user);

        userDetailsRepository.save(userDetails);

        // Convert back to DTO for response
        UserDetailsDTO responseDTO = new UserDetailsDTO();
        responseDTO.setUserId(userDetails.getUser().getUserId());
        responseDTO.setUsername(userDetails.getUsername());
        responseDTO.setDob(userDetails.getDob());
        responseDTO.setHeight(userDetails.getHeight());
        responseDTO.setWeight(userDetails.getWeight());
        responseDTO.setPregnancyStatus(userDetails.getPregnancyStatus().getValue());
        responseDTO.setWorkoutGoal(userDetails.getWorkoutGoal().getValue());
        responseDTO.setWorkoutDays(userDetails.getWorkoutDays());
        responseDTO.setFitnessLevel(userDetails.getFitnessLevel());
        responseDTO.setMenstrualCramps(userDetails.getMenstrualCramps());
        responseDTO.setCycleBasedRecommendations(userDetails.getCycleBasedRecommendations());
        responseDTO.setWorkoutType(userDetails.getWorkoutType().getValue());
        responseDTO.setCurrentStreak(userDetails.getCurrentStreak());
        responseDTO.setLongestStreak(userDetails.getLongestStreak());

        return responseDTO;
    }

    // New methods for the additional APIs

    public ProfileDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = userDetailsRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User details not found"));

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setUsername(userDetails.getUsername());
        profileDTO.setEmail(user.getEmail());
        profileDTO.setHeight(userDetails.getHeight());
        profileDTO.setWeight(userDetails.getWeight());
        profileDTO.setDob(userDetails.getDob());
        profileDTO.setWorkoutGoal(userDetails.getWorkoutGoal().getValue());
        profileDTO.setWorkoutDays(userDetails.getWorkoutDays());
        profileDTO.setAvatar(userDetails.getAvatar());

        return profileDTO;
    }

    @Transactional
    public ProfileDTO updateBasicProfile(Long userId, ProfileDTO profileDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = userDetailsRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User details not found"));

        // Update basic profile information
        userDetails.setUsername(profileDTO.getUsername());
        user.setEmail(profileDTO.getEmail());
        userDetails.setHeight(profileDTO.getHeight());
        userDetails.setWeight(profileDTO.getWeight());
        userDetails.setDob(profileDTO.getDob());

        userRepository.save(user);
        userDetailsRepository.save(userDetails);

        return profileDTO;
    }

    @Transactional
    public GoalsDTO updateUserGoals(Long userId, GoalsDTO goalsDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = userDetailsRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User details not found"));

        // Update user goals
        userDetails.setWorkoutGoal(goalsDTO.getWorkoutGoal());
        userDetails.setWorkoutDays(goalsDTO.getWorkoutDaysPerWeekGoal());

        userDetailsRepository.save(userDetails);

        return goalsDTO;
    }

    @Transactional
    public AvatarDTO updateUserAvatar(Long userId, AvatarDTO avatarDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = userDetailsRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User details not found"));

        // Update avatar link
        userDetails.setAvatar(avatarDTO.getAvatarLink());

        userDetailsRepository.save(userDetails);

        return avatarDTO;
    }

    public WeeklyWorkoutsDTO getWeeklyWorkouts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Calculate the start and end dates for the current week (Monday to Friday)
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));

        // Convert to LocalDateTime to include time component
        LocalDateTime startDateTime = startOfWeek.atStartOfDay();
        LocalDateTime endDateTime = endOfWeek.plusDays(1).atStartOfDay();

        // Convert LocalDateTime to Timestamp
        Timestamp startTimestamp = Timestamp.valueOf(startDateTime);
        Timestamp endTimestamp = Timestamp.valueOf(endDateTime);

        // Get the count of workouts completed between Monday and Friday
        int workoutCount = historyRepository.countByUserAndWorkoutDateTimeBetween(user, startTimestamp, endTimestamp);

        WeeklyWorkoutsDTO weeklyWorkoutsDTO = new WeeklyWorkoutsDTO();
        weeklyWorkoutsDTO.setTotalWorkouts(workoutCount);

        return weeklyWorkoutsDTO;
    }

    // /
    // * Get all users
    // */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // /
    // * Get user by ID
    // */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // /
    // * Get user by email
    // */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // /
    // * Get user by username
    // */
    public Optional<User> getUserByUsername(String username) {
        Optional<UserDetails> userDetails = userDetailsRepository.findByUsername(username);
        if (userDetails.isPresent()) {
            return userRepository.findById(userDetails.get().getUser().getUserId());
        }
        return null;
    }

    // /
    // * Create a new user
    // */
    @Transactional
    public User createUser(User user,String username) {

        // Save the user
        User savedUser = userRepository.save(user);

        // Initialize UserDetails if not already set
        if (user.getUserDetails() == null) {
            UserDetails userDetails = new UserDetails();
            userDetails.setUser(savedUser);
            userDetails.setCurrentStreak(0);
            userDetails.setLongestStreak(0);
            userDetailsRepository.save(userDetails);
        }

        // Initialize achievements for the new user
        userAchievementService.initializeUserAchievements(savedUser);

        return savedUser;
    }

    // /
    // * Update an existing user
    // */
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    // /
    // * Delete a user
    // */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setEmail(user.getEmail());
        dto.setUserId(user.getUserId());
        return dto;
    }
}