// UserDTO.java
package com.fastnfit.app.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long userId;
    private String email;
    // Password is not included in DTO for security reasons
}
