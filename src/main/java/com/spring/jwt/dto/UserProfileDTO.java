package com.spring.jwt.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private UserDTO user;

    private UserProfileDTO1 userProfileDTO1;

    private Set<String> roles;
}
