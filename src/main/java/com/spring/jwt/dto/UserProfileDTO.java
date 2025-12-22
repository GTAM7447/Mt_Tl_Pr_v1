package com.spring.jwt.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    // Base user information
    private UserDTO user;

    // Role-specific information
    private UserProfileDTO1 userProfileDTO1;


    // User roles
    private Set<String> roles;
}
