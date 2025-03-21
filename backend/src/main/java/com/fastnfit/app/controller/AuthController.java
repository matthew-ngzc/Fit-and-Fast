package com.fastnfit.app.controller;

import com.fastnfit.app.dto.LoginRequestDTO;
import com.fastnfit.app.dto.UserDTO;
import com.fastnfit.app.dto.UserRegistrationDTO;
import com.fastnfit.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            // Get user and generate token
            UserDTO userDTO = userService.login(loginRequest);
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@RequestBody UserRegistrationDTO registrationDTO) {
        try {
            UserDTO userDTO = userService.registerUser(registrationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}