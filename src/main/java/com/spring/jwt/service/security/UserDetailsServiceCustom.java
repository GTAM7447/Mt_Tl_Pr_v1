package com.spring.jwt.service.security;

import com.spring.jwt.entity.User;
import com.spring.jwt.entity.UserProfile;
import com.spring.jwt.exception.BaseException;
import com.spring.jwt.repository.UserProfileRepository;
import com.spring.jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceCustom implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    public UserDetailsServiceCustom(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetailsCustom userDetailsCustom = getUserDetails(username);

        if (ObjectUtils.isEmpty(userDetailsCustom)) {
            throw new BaseException(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Invalid username or password!");
        }

        return userDetailsCustom;
    }

    private UserDetailsCustom getUserDetails(String username) {
        User user = userRepository.findByEmail(username);
        if (ObjectUtils.isEmpty(user)) {
            // Log the failed attempt with the username/email
            System.out.println("Login failed: User not found for email/username: " + username);
            throw new BaseException(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Invalid username or password!");
        }

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> {
                    String roleName = role.getName();
                    if (!roleName.startsWith("ROLE_")) {
                        roleName = "ROLE_" + roleName;
                    }
                    return new SimpleGrantedAuthority(roleName);
                })
                .collect(Collectors.toList());

        String firstName = null;
        Integer userId = null;
        Integer userProfileId = null;

        // firstName = user.getFirstName();
        userId = user.getId();

        if (authorities.contains(new SimpleGrantedAuthority("ROLE_USER"))) {
            userProfileId = userProfileRepository.findByUser_Id(userId)
                    .map(UserProfile::getUserProfileId)
                    .orElse(null);
        }

        return new UserDetailsCustom(
                user.getEmail(),
                user.getPassword(),
                firstName,
                userId,
                userProfileId,
                authorities);
    }
}