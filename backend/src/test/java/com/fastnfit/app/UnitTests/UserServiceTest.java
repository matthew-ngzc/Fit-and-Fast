package com.fastnfit.app.UnitTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fastnfit.app.dto.*;
import com.fastnfit.app.enums.FitnessLevel;
import com.fastnfit.app.enums.PregnancyStatus;
import com.fastnfit.app.enums.WorkoutGoal;
import com.fastnfit.app.enums.WorkoutType;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.UserDetails;
import com.fastnfit.app.repository.HistoryRepository;
import com.fastnfit.app.repository.UserDetailsRepository;
import com.fastnfit.app.repository.UserRepository;
import com.fastnfit.app.service.UserAchievementService;
import com.fastnfit.app.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserDetailsRepository userDetailsRepository;
    
    @Mock
    private HistoryRepository historyRepository;
    
    @Mock
    private UserAchievementService userAchievementService;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    private User testUser;
    private UserDetails testUserDetails;
    
    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        
        // Create test user details
        testUserDetails = new UserDetails();
        testUserDetails.setUserId(1L);
        testUserDetails.setUser(testUser);
        testUserDetails.setUsername("testuser");
        testUserDetails.setHeight(170.0);
        testUserDetails.setWeight(70.0);
        testUserDetails.setDob(new Date());
        testUserDetails.setWorkoutDays(3);
        testUserDetails.setWorkoutGoal(WorkoutGoal.GENERAL_FITNESS);
        testUserDetails.setPregnancyStatus(PregnancyStatus.NO);
        testUserDetails.setWorkoutType(WorkoutType.OTHERS);
        testUserDetails.setFitnessLevel(FitnessLevel.BEGINNER);
        
        // Associate user and details
        testUser.setUserDetails(testUserDetails);
    }
    
    @Test
    void testLogin_Success() {
        // Setup
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        
        // Execute
        UserDTO result = userService.login(loginRequest);
        
        // Verify
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", "encodedPassword");
    }
    
    @Test
    void testLogin_InvalidEmail() {
        // Setup
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("password123");
        
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        
        // Execute & Verify
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.login(loginRequest);
        });
        
        assertEquals("Invalid email or password", exception.getMessage());
    }
    
    @Test
    void testLogin_InvalidPassword() {
        // Setup
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongPassword");
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);
        
        // Execute & Verify
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.login(loginRequest);
        });
        
        assertEquals("Invalid email or password", exception.getMessage());
    }
    
    @Test
    void testRegisterUser_Success() {
        // Setup
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
        registrationDTO.setEmail("new@example.com");
        registrationDTO.setPassword("password123");
        registrationDTO.setUsername("newuser");
        
        User newUser = new User();
        newUser.setUserId(2L);
        newUser.setEmail("new@example.com");
        newUser.setPassword("encodedPassword");
        
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        
        // Execute
        UserDTO result = userService.registerUser(registrationDTO);

        // Verify
        assertNotNull(result);
        assertEquals(2L, result.getUserId());
        assertEquals("new@example.com", result.getEmail());
        
        verify(userRepository).existsByEmail("new@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(userDetailsRepository).save(any(UserDetails.class));
    }
    
    @Test
    void testRegisterUser_EmailAlreadyExists() {
        // Setup
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
        registrationDTO.setEmail("test@example.com");
        registrationDTO.setPassword("password123");
        
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        
        // Execute & Verify
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(registrationDTO);
        });
        
        assertEquals("Email already in use", exception.getMessage());
    }
    
    @Test
    void testCompleteUserQuestionnaire_NewDetails() {
        // Setup
        Long userId = 1L;
        UserDetailsDTO detailsDTO = new UserDetailsDTO();
        detailsDTO.setUsername("updatedUser");
        detailsDTO.setHeight(175.0);
        detailsDTO.setWeight(75.0);
        detailsDTO.setWorkoutDays(4);
        detailsDTO.setPregnancyStatus("no");
        detailsDTO.setFitnessLevel(FitnessLevel.BEGINNER);
        detailsDTO.setWorkoutGoal("general");
        detailsDTO.setWorkoutType("Strength");
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userDetailsRepository.findByUser(testUser)).thenReturn(Optional.empty());
        
        // Execute
        UserDetailsDTO result = userService.completeUserQuestionnaire(userId, detailsDTO);
        
        // Verify
        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(userDetailsRepository).findByUser(testUser);
        verify(userDetailsRepository).save(any(UserDetails.class));
    }
    
    @Test
    void testCompleteUserQuestionnaire_ExistingDetails() {
        // Setup
        Long userId = 1L;
        UserDetailsDTO detailsDTO = new UserDetailsDTO();
        detailsDTO.setUsername("updatedUser");
        detailsDTO.setHeight(175.0);
        detailsDTO.setWeight(75.0);
        detailsDTO.setWorkoutDays(4);
        detailsDTO.setPregnancyStatus("no");
        detailsDTO.setFitnessLevel(FitnessLevel.BEGINNER);
        detailsDTO.setWorkoutGoal("general");
        detailsDTO.setWorkoutType("Strength");
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userDetailsRepository.findByUser(testUser)).thenReturn(Optional.of(testUserDetails));
        
        // Execute
        UserDetailsDTO result = userService.completeUserQuestionnaire(userId, detailsDTO);
        
        // Verify
        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(userDetailsRepository).findByUser(testUser);
        verify(userDetailsRepository).save(testUserDetails);
    }
    
    @Test
    void testGetUserDetails() {
        // Setup
        Long userId = 1L;
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userDetailsRepository.findByUser(testUser)).thenReturn(Optional.of(testUserDetails));
        
        // Execute
        UserDetailsDTO result = userService.getUserDetails(userId);
        
        // Verify
        assertNotNull(result);
        assertEquals(testUserDetails.getUserId(), result.getUserId());
        assertEquals(testUserDetails.getUsername(), result.getUsername());
        assertEquals(testUserDetails.getHeight(), result.getHeight());
        assertEquals(testUserDetails.getWeight(), result.getWeight());
        
        verify(userRepository).findById(userId);
        verify(userDetailsRepository).findByUser(testUser);
    }
    
    @Test
    void testUpdateUserDetails() {
        // Setup
        Long userId = 1L;
        UserDetailsDTO detailsDTO = new UserDetailsDTO();
        detailsDTO.setUsername("updatedUser");
        detailsDTO.setHeight(175.0);
        detailsDTO.setWeight(75.0);
        detailsDTO.setWorkoutDays(4);
        detailsDTO.setPregnancyStatus("no");
        detailsDTO.setFitnessLevel(FitnessLevel.BEGINNER);
        detailsDTO.setWorkoutGoal("general");
        detailsDTO.setWorkoutType("Strength");
        
        when(userDetailsRepository.findById(userId)).thenReturn(Optional.of(testUserDetails));
        
        // Execute
        UserDetailsDTO result = userService.updateUserDetails(userId, detailsDTO);
        
        // Verify
        assertNotNull(result);
        verify(userDetailsRepository).findById(userId);
        verify(userDetailsRepository).save(testUserDetails);
        
        // Verify that user details were updated
        assertEquals("updatedUser", testUserDetails.getUsername());
        assertEquals(175.0, testUserDetails.getHeight());
        assertEquals(75.0, testUserDetails.getWeight());
        assertEquals(4, testUserDetails.getWorkoutDays());
    }
    
    @Test
    void testGetUserProfile() {
        // Setup
        Long userId = 1L;
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userDetailsRepository.findByUser(testUser)).thenReturn(Optional.of(testUserDetails));
        
        // Execute
        ProfileDTO result = userService.getUserProfile(userId);
        
        // Verify
        assertNotNull(result);
        assertEquals(testUserDetails.getUsername(), result.getUsername());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUserDetails.getHeight(), result.getHeight());
        assertEquals(testUserDetails.getWeight(), result.getWeight());
        
        verify(userRepository).findById(userId);
        verify(userDetailsRepository).findByUser(testUser);
    }
    
    @Test
    void testUpdateBasicProfile() {
        // Setup
        Long userId = 1L;
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setUsername("updatedUser");
        profileDTO.setEmail("updated@example.com");
        profileDTO.setHeight(175.0);
        profileDTO.setWeight(75.0);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userDetailsRepository.findByUser(testUser)).thenReturn(Optional.of(testUserDetails));
        
        // Execute
        ProfileDTO result = userService.updateBasicProfile(userId, profileDTO);
        
        // Verify
        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(userDetailsRepository).findByUser(testUser);
        verify(userRepository).save(testUser);
        verify(userDetailsRepository).save(testUserDetails);
        
        // Verify that user and details were updated
        assertEquals("updatedUser", testUserDetails.getUsername());
        assertEquals("updated@example.com", testUser.getEmail());
        assertEquals(175.0, testUserDetails.getHeight());
        assertEquals(75.0, testUserDetails.getWeight());
    }
    
    @Test
    void testUpdateUserGoals() {
        // Setup
        Long userId = 1L;
        GoalsDTO goalsDTO = new GoalsDTO();
        goalsDTO.setWorkoutDaysPerWeekGoal(5);
        goalsDTO.setWorkoutGoal(WorkoutGoal.GENERAL_FITNESS);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userDetailsRepository.findByUser(testUser)).thenReturn(Optional.of(testUserDetails));
        
        // Execute
        GoalsDTO result = userService.updateUserGoals(userId, goalsDTO);
        
        // Verify
        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(userDetailsRepository).findByUser(testUser);
        verify(userDetailsRepository).save(testUserDetails);
        
        // Verify that goals were updated
        assertEquals(5, testUserDetails.getWorkoutDays());
    }
    
    @Test
    void testUpdateUserAvatar() {
        // Setup
        Long userId = 1L;
        AvatarDTO avatarDTO = new AvatarDTO();
        avatarDTO.setAvatarLink("new-avatar-link.png");
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userDetailsRepository.findByUser(testUser)).thenReturn(Optional.of(testUserDetails));
        
        // Execute
        AvatarDTO result = userService.updateUserAvatar(userId, avatarDTO);
        
        // Verify
        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(userDetailsRepository).findByUser(testUser);
        verify(userDetailsRepository).save(testUserDetails);
        
        // Verify that avatar was updated
        assertEquals("new-avatar-link.png", testUserDetails.getAvatar());
    }
    
    @Test
    void testGetWeeklyWorkouts() {
        // Setup
        Long userId = 1L;
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(historyRepository.countByUserAndWorkoutDateBetween(eq(testUser), any(Date.class), any(Date.class))).thenReturn(3);
        
        // Execute
        WeeklyWorkoutsDTO result = userService.getWeeklyWorkouts(userId);
        
        // Verify
        assertNotNull(result);
        assertEquals(3, result.getTotalWorkouts());
        verify(userRepository).findById(userId);
        verify(historyRepository).countByUserAndWorkoutDateBetween(eq(testUser), any(Date.class), any(Date.class));
    }
    
    @Test
    void testGetAllUsers() {
        // Setup
        List<User> users = new ArrayList<>();
        users.add(testUser);
        
        when(userRepository.findAll()).thenReturn(users);
        
        // Execute
        List<User> result = userService.getAllUsers();
        
        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser, result.get(0));
        verify(userRepository).findAll();
    }
    
    @Test
    void testGetUserById() {
        // Setup
        Long userId = 1L;
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        
        // Execute
        Optional<User> result = userService.getUserById(userId);
        
        // Verify
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository).findById(userId);
    }
    
    @Test
    void testGetUserByEmail() {
        // Setup
        String email = "test@example.com";
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        
        // Execute
        Optional<User> result = userService.getUserByEmail(email);
        
        // Verify
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository).findByEmail(email);
    }
    
    @Test
    void testGetUserByUsername() {
        // Setup
        String username = "testuser";
        
        when(userDetailsRepository.findByUsername(username)).thenReturn(Optional.of(testUserDetails));
        when(userRepository.findById(testUserDetails.getUserId())).thenReturn(Optional.of(testUser));
        
        // Execute
        Optional<User> result = userService.getUserByUsername(username);
        
        // Verify
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userDetailsRepository).findByUsername(username);
        verify(userRepository).findById(testUserDetails.getUserId());
    }
    
    @Test
    void testCreateUser() {
        // Setup
        User newUser = new User();
        newUser.setEmail("new@example.com");
        newUser.setPassword("password123");
        
        when(userRepository.save(newUser)).thenReturn(newUser);
        
        // Execute
        User result = userService.createUser(newUser);
        
        // Verify
        assertNotNull(result);
        verify(userRepository).save(newUser);
        verify(userAchievementService).initializeUserAchievements(newUser);
        
        // Verify that UserDetails was created if not provided
        assertNotNull(newUser.getUserDetails());
    }
    
    @Test
    void testUpdateUser() {
        // Setup
        when(userRepository.save(testUser)).thenReturn(testUser);
        
        // Execute
        User result = userService.updateUser(testUser);
        
        // Verify
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).save(testUser);
    }
    
    @Test
    void testDeleteUser() {
        // Setup
        Long userId = 1L;
        
        // Execute
        userService.deleteUser(userId);
        
        // Verify
        verify(userRepository).deleteById(userId);
    }
    
    @Test
    void testUpdateUserDetailsEntity_Existing() {
        // Setup
        Long userId = 1L;
        UserDetails updatedDetails = new UserDetails();
        updatedDetails.setUsername("updatedUser");
        updatedDetails.setHeight(175.0);
        updatedDetails.setWeight(75.0);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);
        
        // Execute
        User result = userService.updateUserDetails(userId, updatedDetails);
        
        // Verify
        assertNotNull(result);
        assertEquals("updatedUser", testUser.getUserDetails().getUsername());
        assertEquals(175.0, testUser.getUserDetails().getHeight());
        assertEquals(75.0, testUser.getUserDetails().getWeight());
        verify(userRepository).findById(userId);
        verify(userRepository).save(testUser);
    }
    
    @Test
    void testUpdateUserDetailsEntity_New() {
        // Setup
        Long userId = 1L;
        UserDetails updatedDetails = new UserDetails();
        updatedDetails.setUsername("updatedUser");
        updatedDetails.setHeight(175.0);
        updatedDetails.setWeight(75.0);
        
        User userWithoutDetails = new User();
        userWithoutDetails.setUserId(userId);
        userWithoutDetails.setEmail("test@example.com");
        userWithoutDetails.setPassword("encodedPassword");
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithoutDetails));
        when(userRepository.save(userWithoutDetails)).thenReturn(userWithoutDetails);
        
        // Execute
        User result = userService.updateUserDetails(userId, updatedDetails);
        
        // Verify
        assertNotNull(result);
        assertNotNull(userWithoutDetails.getUserDetails());
        assertEquals("updatedUser", userWithoutDetails.getUserDetails().getUsername());
        assertEquals(175.0, userWithoutDetails.getUserDetails().getHeight());
        assertEquals(75.0, userWithoutDetails.getUserDetails().getWeight());
        verify(userRepository).findById(userId);
        verify(userRepository).save(userWithoutDetails);
    }
}