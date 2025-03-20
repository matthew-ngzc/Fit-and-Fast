// UserService.java
package com.fastnfit.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fastnfit.app.dto.UserDTO;
import com.fastnfit.app.dto.UserDetailsDTO;
import com.fastnfit.app.dto.UserRegistrationDTO;
import com.fastnfit.app.model.User;
import com.fastnfit.app.model.UserDetails;
import com.fastnfit.app.repository.UserDetailsRepository;
import com.fastnfit.app.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, 
                    UserDetailsRepository userDetailsRepository,
                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserDTO registerUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        // Create and save user
        User user = new User();
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        userRepository.save(user);

        // Create and save user details
        UserDetails userDetails = new UserDetails();
        userDetails.setUser(user);
        userDetails.setUsername(registrationDTO.getUsername());
        userDetails.setDob(registrationDTO.getDob());
        userDetails.setHeight(registrationDTO.getHeight());
        userDetails.setWeight(registrationDTO.getWeight());
        userDetails.setIsPregnant(registrationDTO.getIsPregnant());
        userDetails.setWorkoutGoal(registrationDTO.getWorkoutGoal());
        userDetails.setWorkoutDays(registrationDTO.getWorkoutDays());
        userDetailsRepository.save(userDetails);

        // Return DTO
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setEmail(user.getEmail());

        return userDTO;
    }

    public UserDetailsDTO getUserDetails(Long userId) {
        UserDetails userDetails = userDetailsRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User details not found"));

        UserDetailsDTO dto = new UserDetailsDTO();
        dto.setUserId(userDetails.getUserId());
        dto.setUsername(userDetails.getUsername());
        dto.setDob(userDetails.getDob());
        dto.setHeight(userDetails.getHeight());
        dto.setWeight(userDetails.getWeight());
        dto.setIsPregnant(userDetails.getIsPregnant());
        dto.setWorkoutGoal(userDetails.getWorkoutGoal());
        dto.setWorkoutDays(userDetails.getWorkoutDays());

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
        userDetails.setIsPregnant(detailsDTO.getIsPregnant());
        userDetails.setWorkoutGoal(detailsDTO.getWorkoutGoal());
        userDetails.setWorkoutDays(detailsDTO.getWorkoutDays());

        userDetailsRepository.save(userDetails);

        return detailsDTO;
    }
}
