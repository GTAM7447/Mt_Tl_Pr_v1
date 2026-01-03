package com.spring.jwt.admin.service;

import com.spring.jwt.CompleteProfile.CompleteProfileRepository;
import com.spring.jwt.admin.dto.AdminCompleteRegistrationRequest;
import com.spring.jwt.entity.CompleteProfile;
import com.spring.jwt.entity.Enums.Gender;
import com.spring.jwt.entity.Role;
import com.spring.jwt.entity.User;
import com.spring.jwt.exception.BaseException;
import com.spring.jwt.repository.RoleRepository;
import com.spring.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAccountCreationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CompleteProfileRepository completeProfileRepository;

    public User createUserAccount(AdminCompleteRegistrationRequest request) {
        validateUniqueConstraints(request);
        User user = buildUser(request);
        user = userRepository.save(user);
        createCompleteProfile(user);
        log.info("User account created with ID: {}", user.getId());
        return user;
    }

    private void validateUniqueConstraints(AdminCompleteRegistrationRequest request) {
        Optional.ofNullable(userRepository.findByEmail(request.getEmail()))
                .ifPresent(u -> {
                    throw new BaseException(
                            String.valueOf(HttpStatus.BAD_REQUEST.value()),
                            "Email is already registered: " + request.getEmail()
                    );
                });

        Long mobileNumber = Long.parseLong(request.getMobileNumber());
        userRepository.findByMobileNumber(mobileNumber)
                .ifPresent(u -> {
                    throw new BaseException(
                            String.valueOf(HttpStatus.BAD_REQUEST.value()),
                            "Mobile number is already registered: " + request.getMobileNumber()
                    );
                });
    }

    private User buildUser(AdminCompleteRegistrationRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setMobileNumber(Long.parseLong(request.getMobileNumber()));
        user.setGender(Gender.valueOf(request.getGender()));
        user.setEmailVerified(request.getSkipEmailVerification());
        user.setRoles(getUserRoles());
        return user;
    }

    private Set<Role> getUserRoles() {
        Set<Role> roles = new HashSet<>();
        Optional.ofNullable(roleRepository.findByName("USER"))
                .ifPresent(roles::add);
        return roles;
    }

    private void createCompleteProfile(User user) {
        CompleteProfile completeProfile = new CompleteProfile();
        completeProfile.setUser(user);
        completeProfile.setProfileCompleted(false);
        completeProfileRepository.save(completeProfile);
    }
}
