package com.fastnfit.app.UnitTests;

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
import com.fastnfit.app.service.JwtService;
import com.fastnfit.app.service.UserAchievementService;
import com.fastnfit.app.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

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

    @Mock
    private JwtService jwtService;

    private UserService userService;
    private User testUser;
    private UserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        userService = new UserService(
                userRepository,
                userDetailsRepository,
                historyRepository,
                userAchievementService,
                passwordEncoder,
                jwtService
        );

        testUser = new User();
        testUser.setUserId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedPassword");

        testUserDetails = new UserDetails();
        testUserDetails.setUserId(1L);
        testUserDetails.setUser(testUser);
        testUserDetails.setUsername("testuser");
        testUserDetails.setDob(Date.valueOf("1990-01-01"));
        testUserDetails.setHeight(170.0);
        testUserDetails.setWeight(70.0);
        testUserDetails.setWorkoutDays(5);
        testUserDetails.setFitnessLevel(FitnessLevel.INTERMEDIATE);
        testUserDetails.setPregnancyStatus(PregnancyStatus.NO);
        testUserDetails.setWorkoutGoal(WorkoutGoal.GENERAL_FITNESS);
        testUserDetails.setAvatar("default-avatar.png");
    }

    @Test
    void login_shouldReturnAuthResponseWhenCredentialsValid() {
        // Given
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtService.generateToken(1L)).thenReturn("jwtToken123");

        // When
        AuthResponseDTO response = userService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("jwtToken123", response.getToken());
        assertEquals(testUser.getUserId(), response.getUser().getUserId());
        assertEquals(testUser.getEmail(), response.getUser().getEmail());

        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", "hashedPassword");
        verify(jwtService).generateToken(1L);
    }

    @Test
    void login_shouldThrowExceptionWhenEmailNotFound() {
        // Given
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("password123");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When/Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.login(loginRequest);
        });

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyLong());
    }

    @Test
    void login_shouldThrowExceptionWhenPasswordDoesNotMatch() {
        // Given
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        // When/Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.login(loginRequest);
        });

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("wrongPassword", "hashedPassword");
        verify(jwtService, never()).generateToken(anyLong());
    }

    @Test
    void registerUser_shouldCreateUserAndReturnAuthResponse() {
        // Given
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
        registrationDTO.setEmail("newuser@example.com");
        registrationDTO.setPassword("newpassword123");
        registrationDTO.setUsername("newuser");

        User savedUser = new User();
        savedUser.setUserId(2L);
        savedUser.setEmail("newuser@example.com");
        savedUser.setPassword("encodedPassword");

        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("newpassword123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(2L)).thenReturn("newJwtToken123");

        // When
        AuthResponseDTO response = userService.registerUser(registrationDTO);

        // Then
        assertNotNull(response);
        assertEquals("newJwtToken123", response.getToken());
        assertEquals(savedUser.getUserId(), response.getUser().getUserId());
        assertEquals(savedUser.getEmail(), response.getUser().getEmail());

        // Verify user creation
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals(registrationDTO.getEmail(), userCaptor.getValue().getEmail());

        // Verify user details creation
        ArgumentCaptor<UserDetails> userDetailsCaptor = ArgumentCaptor.forClass(UserDetails.class);
        verify(userDetailsRepository).save(userDetailsCaptor.capture());
        assertEquals(registrationDTO.getUsername(), userDetailsCaptor.getValue().getUsername());
        assertEquals(savedUser, userDetailsCaptor.getValue().getUser());
    }

    @Test
    void registerUser_shouldThrowExceptionWhenEmailExists() {
        // Given
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
        registrationDTO.setEmail("existing@example.com");
        registrationDTO.setPassword("password123");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When/Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(registrationDTO);
        });

        assertEquals("Email is already in use", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(userDetailsRepository, never()).save(any(UserDetails.class));
    }

    @Test
    void completeUserQuestionnaire_shouldCreateDetailsWhenNotExists() {
        // Given
        UserDetailsDTO detailsDTO = new UserDetailsDTO();
        detailsDTO.setUsername("updatedUser");
        detailsDTO.setDob(Date.valueOf("1992-05-15"));
        detailsDTO.setHeight(175.0);
        detailsDTO.setWeight(75.0);
        detailsDTO.setWorkoutDays(4);
        detailsDTO.setFitnessLevel(FitnessLevel.BEGINNER);
        detailsDTO.setPregnancyStatus(PregnancyStatus.NO.getValue());
        detailsDTO.setWorkoutGoal(WorkoutGoal.GENERAL_FITNESS.getValue());
        detailsDTO.setWorkoutType(WorkoutType.OTHERS.getValue());

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userDetailsRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(userDetailsRepository.save(any(UserDetails.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UserDetailsDTO result = userService.completeUserQuestionnaire(1L, detailsDTO);

        // Then
        assertNotNull(result);
        
        // Verify user details creation
        ArgumentCaptor<UserDetails> userDetailsCaptor = ArgumentCaptor.forClass(UserDetails.class);
        verify(userDetailsRepository).save(userDetailsCaptor.capture());
        
        UserDetails capturedDetails = userDetailsCaptor.getValue();
        assertEquals(testUser, capturedDetails.getUser());
        assertEquals(detailsDTO.getUsername(), capturedDetails.getUsername());
        assertEquals(detailsDTO.getDob(), capturedDetails.getDob());
        assertEquals(detailsDTO.getHeight(), capturedDetails.getHeight());
        assertEquals(detailsDTO.getWeight(), capturedDetails.getWeight());
        assertEquals(detailsDTO.getWorkoutDays(), capturedDetails.getWorkoutDays());
        assertEquals(detailsDTO.getFitnessLevel(), capturedDetails.getFitnessLevel());
        assertEquals(detailsDTO.getWorkoutGoal(), capturedDetails.getWorkoutGoal().getValue());
        assertEquals(detailsDTO.getWorkoutType(), capturedDetails.getWorkoutType().getValue());
    }

    @Test
    void completeUserQuestionnaire_shouldUpdateExistingDetails() {
        // Given
        UserDetailsDTO detailsDTO = new UserDetailsDTO();
        detailsDTO.setUsername("updatedUser");
        detailsDTO.setDob(Date.valueOf("1992-05-15"));
        detailsDTO.setHeight(175.0);
        detailsDTO.setWeight(75.0);
        detailsDTO.setWorkoutDays(4);
        detailsDTO.setFitnessLevel(FitnessLevel.BEGINNER);
        detailsDTO.setPregnancyStatus(PregnancyStatus.NO.getValue());
        detailsDTO.setWorkoutGoal(WorkoutGoal.GENERAL_FITNESS.getValue());
        detailsDTO.setWorkoutType(WorkoutType.OTHERS.getValue());

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userDetailsRepository.findByUser(testUser)).thenReturn(Optional.of(testUserDetails));
        when(userDetailsRepository.save(any(UserDetails.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UserDetailsDTO result = userService.completeUserQuestionnaire(1L, detailsDTO);

        // Then
        assertNotNull(result);
        
        // Verify user details update
        ArgumentCaptor<UserDetails> userDetailsCaptor = ArgumentCaptor.forClass(UserDetails.class);
        verify(userDetailsRepository).save(userDetailsCaptor.capture());
        
        UserDetails capturedDetails = userDetailsCaptor.getValue();
        assertEquals(testUser, capturedDetails.getUser());
        assertEquals(detailsDTO.getUsername(), capturedDetails.getUsername());
        assertEquals(detailsDTO.getDob(), capturedDetails.getDob());
        assertEquals(detailsDTO.getHeight(), capturedDetails.getHeight());
        assertEquals(detailsDTO.getWeight(), capturedDetails.getWeight());
        assertEquals(detailsDTO.getWorkoutDays(), capturedDetails.getWorkoutDays());
        assertEquals(detailsDTO.getFitnessLevel(), capturedDetails.getFitnessLevel());
        assertEquals(detailsDTO.getWorkoutGoal(), capturedDetails.getWorkoutGoal().getValue());
        assertEquals(detailsDTO.getWorkoutType(), capturedDetails.getWorkoutType().getValue());
    }

    @Test
    void getUserProfile_shouldReturnUserProfile() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userDetailsRepository.findByUser(testUser)).thenReturn(Optional.of(testUserDetails));

        // When
        ProfileDTO result = userService.getUserProfile(1L);

        // Then
        assertNotNull(result);
        assertEquals(testUserDetails.getUsername(), result.getUsername());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUserDetails.getHeight(), result.getHeight());
        assertEquals(testUserDetails.getWeight(), result.getWeight());
        assertEquals(testUserDetails.getDob(), result.getDob());
        assertEquals(testUserDetails.getWorkoutDays(), result.getWorkoutDays());
        assertEquals(testUserDetails.getAvatar(), result.getAvatar());
    }

    @Test
    void updateBasicProfile_shouldUpdateUserProfile() {
        // Given
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setUsername("updatedUsername");
        profileDTO.setEmail("updated@example.com");
        profileDTO.setHeight(180.0);
        profileDTO.setWeight(80.0);
        profileDTO.setDob(Date.valueOf("1995-10-10"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userDetailsRepository.findByUser(testUser)).thenReturn(Optional.of(testUserDetails));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userDetailsRepository.save(any(UserDetails.class))).thenReturn(testUserDetails);

        // When
        ProfileDTO result = userService.updateBasicProfile(1L, profileDTO);

        // Then
        assertNotNull(result);
        
        // Verify user update
        verify(userRepository).save(testUser);
        assertEquals(profileDTO.getEmail(), testUser.getEmail());
        
        // Verify user details update
        verify(userDetailsRepository).save(testUserDetails);
        assertEquals(profileDTO.getUsername(), testUserDetails.getUsername());
        assertEquals(profileDTO.getHeight(), testUserDetails.getHeight());
        assertEquals(profileDTO.getWeight(), testUserDetails.getWeight());
        assertEquals(profileDTO.getDob(), testUserDetails.getDob());
    }

    @Test
    void getWeeklyWorkouts_shouldReturnWorkoutCount() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(historyRepository.countByUserAndWorkoutDateTimeBetween(
                eq(testUser), any(Timestamp.class), any(Timestamp.class))).thenReturn(3);

        // When
        WeeklyWorkoutsDTO result = userService.getWeeklyWorkouts(1L);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getTotalWorkouts());
        
        // Verify the repository was called with appropriate date range parameters
        verify(historyRepository).countByUserAndWorkoutDateTimeBetween(
                eq(testUser), any(Timestamp.class), any(Timestamp.class));
    }

    @Test
    void updateUserAvatar_shouldUpdateAvatar() {
        // Given
        AvatarDTO avatarDTO = new AvatarDTO();
        avatarDTO.setAvatarLink("new-avatar.png");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userDetailsRepository.findByUser(testUser)).thenReturn(Optional.of(testUserDetails));
        when(userDetailsRepository.save(any(UserDetails.class))).thenReturn(testUserDetails);

        // When
        AvatarDTO result = userService.updateUserAvatar(1L, avatarDTO);

        // Then
        assertNotNull(result);
        assertEquals(avatarDTO.getAvatarLink(), testUserDetails.getAvatar());
        verify(userDetailsRepository).save(testUserDetails);
    }

    @Test
    void createUser_shouldInitializeUserDetailsAndAchievements() {
        // Given
        User newUser = new User();
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("encodedPassword");

        User savedUser = new User();
        savedUser.setUserId(3L);
        savedUser.setEmail("newuser@example.com");
        savedUser.setPassword("encodedPassword");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        doNothing().when(userAchievementService).initializeUserAchievements(savedUser);

        // When
        User result = userService.createUser(newUser);

        // Then
        assertNotNull(result);
        assertEquals(savedUser.getUserId(), result.getUserId());
        assertEquals(savedUser.getEmail(), result.getEmail());

        // Verify user details initialization
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertNotNull(userCaptor.getValue().getUserDetails());
        assertEquals(0, userCaptor.getValue().getUserDetails().getCurrentStreak());
        assertEquals(0, userCaptor.getValue().getUserDetails().getLongestStreak());

        // Verify achievements initialization
        verify(userAchievementService).initializeUserAchievements(savedUser);
    }

    @Test
    void getUserByEmail_shouldReturnUserWhenExists() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserByEmail("test@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getUserId(), result.get().getUserId());
        assertEquals(testUser.getEmail(), result.get().getEmail());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void getUserByUsername_shouldReturnUserWhenExists() {
        // Given
        when(userDetailsRepository.findByUsername("testuser")).thenReturn(Optional.of(testUserDetails));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserByUsername("testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getUserId(), result.get().getUserId());
        assertEquals(testUser.getEmail(), result.get().getEmail());
        verify(userDetailsRepository).findByUsername("testuser");
        verify(userRepository).findById(1L);
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        // Given
        User user1 = new User();
        user1.setUserId(1L);
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setUserId(2L);
        user2.setEmail("user2@example.com");

        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertEquals(2, result.size());
        assertEquals(user1.getUserId(), result.get(0).getUserId());
        assertEquals(user2.getUserId(), result.get(1).getUserId());
        verify(userRepository).findAll();
    }
}