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
        this.userAchievementService=userAchievementService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService=jwtService;
    }

    // public UserDTO login(LoginRequestDTO loginRequest) {
    //     User user = userRepository.findByEmail(loginRequest.getEmail())
    //         .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
    //     if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
    //         throw new RuntimeException("Invalid email or password");
    //     }
        
    //     UserDTO userDTO = new UserDTO();
    //     userDTO.setUserId(user.getUserId());
    //     userDTO.setEmail(user.getEmail());
        
    //     return userDTO;
    // }

    // @Transactional
    // public UserDTO registerUser(UserRegistrationDTO registrationDTO) {
    //     if (userRepository.existsByEmail(registrationDTO.getEmail())) {
    //         throw new RuntimeException("Email already in use");
    //     }

    //     // Create and save user
    //     User user = new User();
    //     user.setEmail(registrationDTO.getEmail());
    //     user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
    //     User savedUser=userRepository.save(user);

    //     // Create basic user details if username is provided
    //     if (registrationDTO.getUsername() != null && !registrationDTO.getUsername().isEmpty()) {
    //         UserDetails userDetails = new UserDetails();
    //         userDetails.setUser(savedUser);
    //         userDetails.setUsername(registrationDTO.getUsername());
    //         userDetailsRepository.save(userDetails);
    //     }

    //     // Return DTO
    //     UserDTO userDTO = new UserDTO();
    //     userDTO.setUserId(savedUser.getUserId());
    //     userDTO.setEmail(savedUser.getEmail());

    //     return userDTO;
    // }

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
            userDetails.setUser(user);
        }

        // Update user details with questionnaire data
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
        dto.setUserId(userDetails.getUserId());
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
        UserDetails userDetails = userDetailsRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User details not found"));

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
    //  * Get all users
    //  */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    // /
    //  * Get user by ID
    //  */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    // /
    //  * Get user by email
    //  */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    // /
    //  * Get user by username
    //  */
    public Optional<User> getUserByUsername(String username) {
        Optional<UserDetails> userDetails=userDetailsRepository.findByUsername(username);
        if (userDetails.isPresent()){
            return userRepository.findById(userDetails.get().getUserId());
        }
        return null;
    }
    
    // /
    //  * Create a new user
    //  */
    @Transactional
    public User createUser(User user) {
        // Initialize UserDetails if not already set
        if (user.getUserDetails() == null) {
            UserDetails userDetails = new UserDetails();
            userDetails.setUser(user);
            userDetails.setCurrentStreak(0);
            userDetails.setLongestStreak(0);
            user.setUserDetails(userDetails);
        }
        
        // Save the user
        User savedUser = userRepository.save(user);
        
        // Initialize achievements for the new user
        userAchievementService.initializeUserAchievements(savedUser);
        
        return savedUser;
    }
    
    // /
    //  * Update an existing user
    //  */
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    // /
    //  * Delete a user
    //  */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    // /
    //  * Update user details
    //  */
    @Transactional
    public User updateUserDetails(Long userId, UserDetails userDetails) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            UserDetails existingDetails = user.getUserDetails();
            
            if (existingDetails != null) {
                // Update fields that are not null in the new userDetails
                if (userDetails.getUsername() != null) {
                    existingDetails.setUsername(userDetails.getUsername());
                }
                if (userDetails.getDob() != null) {
                    existingDetails.setDob(userDetails.getDob());
                }
                if (userDetails.getHeight() != null) {
                    existingDetails.setHeight(userDetails.getHeight());
                }
                if (userDetails.getWeight() != null) {
                    existingDetails.setWeight(userDetails.getWeight());
                }
                if (userDetails.getPregnancyStatus() != null) {
                    existingDetails.setPregnancyStatus(userDetails.getPregnancyStatus());
                }
                if (userDetails.getWorkoutGoal() != null) {
                    existingDetails.setWorkoutGoal(userDetails.getWorkoutGoal());
                }
                if (userDetails.getWorkoutDays() != null) {
                    existingDetails.setWorkoutDays(userDetails.getWorkoutDays());
                }
                if (userDetails.getFitnessLevel() != null) {
                    existingDetails.setFitnessLevel(userDetails.getFitnessLevel());
                }
                if (userDetails.getWorkoutType() != null) {
                    existingDetails.setWorkoutType(userDetails.getWorkoutType());
                }
                if (userDetails.getAvatar() != null) {
                    existingDetails.setAvatar(userDetails.getAvatar());
                }
            } else {
                // Create new UserDetails if it doesn't exist
                userDetails.setUser(user);
                userDetails.setCurrentStreak(0);
                userDetails.setLongestStreak(0);
                user.setUserDetails(userDetails);
            }
            
            return userRepository.save(user);
        }
        
        return null;
    }

    public UserDTO convertToDTO(User user){
        UserDTO dto=new UserDTO();
        dto.setEmail(user.getEmail());
        dto.setUserId(user.getUserId());
        return dto;
    }

    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        
        UserDTO userDTO = convertToDTO(user);
        String token = jwtService.generateToken(user.getUserId());
        
        return AuthResponseDTO.builder()
                .user(userDTO)
                .token(token)
                .build();
    }

    public AuthResponseDTO registerUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }
        
        User user = new User();
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        User savedUser=userRepository.save(user);

        //Create basic user details if username is provided
        if (registrationDTO.getUsername() != null && !registrationDTO.getUsername().isEmpty()) {
            UserDetails userDetails = new UserDetails();
            userDetails.setUser(savedUser);
            userDetails.setUsername(registrationDTO.getUsername());
            userDetailsRepository.save(userDetails);
        }
        
        UserDTO userDTO = convertToDTO(savedUser);
        String token = jwtService.generateToken(savedUser.getUserId());
        
        return AuthResponseDTO.builder()
                .user(userDTO)
                .token(token)
                .build();
    }


}