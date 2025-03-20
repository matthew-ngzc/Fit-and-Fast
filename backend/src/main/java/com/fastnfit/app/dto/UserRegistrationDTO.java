// Updated UserRegistrationDTO.java with validation
package com.fastnfit.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Date;

@Data
public class UserRegistrationDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    
    @NotBlank(message = "Username is required")
    private String username;
    
    private Date dob;
    
    private Integer height;
    
    private Integer weight;
    
    private Boolean isPregnant;
    
    private String workoutGoal;
    
    private Integer workoutDays;
}