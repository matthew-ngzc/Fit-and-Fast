// UserRegistrationDTO.java
package com.fastnfit.app.dto;

import lombok.Data;
import java.util.Date;
import com.fastnfit.app.enums.*;

@Data
public class UserRegistrationDTO {
    private String email;
    private String password;
    private String username;
}