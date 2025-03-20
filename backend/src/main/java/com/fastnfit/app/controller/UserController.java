// UserController.java
package com.fastnfit.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fastnfit.app.dto.UserDTO;
import com.fastnfit.app.dto.UserDetailsDTO;
import com.fastnfit.app.dto.UserRegistrationDTO;
import com.fastnfit.app.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        UserDTO userDTO = userService.registerUser(registrationDTO);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/details")
    public ResponseEntity<UserDetailsDTO> getUserDetails(@PathVariable Long userId) {
        UserDetailsDTO detailsDTO = userService.getUserDetails(userId);
        return ResponseEntity.ok(detailsDTO);
    }

    @PutMapping("/{userId}/details")
    public ResponseEntity<UserDetailsDTO> updateUserDetails(
            @PathVariable Long userId,
            @Valid @RequestBody UserDetailsDTO detailsDTO) {
        UserDetailsDTO updatedDetails = userService.updateUserDetails(userId, detailsDTO);
        return ResponseEntity.ok(updatedDetails);
    }
}
