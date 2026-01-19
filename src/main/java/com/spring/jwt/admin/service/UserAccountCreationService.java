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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAccountCreationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CompleteProfileRepository completeProfileRepository;

    /**
     * Creates a new user account with the given registration details.
     * Uses database constraints to prevent race conditions instead of
     * check-then-save pattern.
     * 
     * @param request the registration request
     * @return the created user
     * @throws BaseException if email or mobile number already exists
     */
    public User createUserAccount(AdminCompleteRegistrationRequest request) {
        try {
            User user = buildUser(request);
            user = userRepository.save(user);
            createCompleteProfile(user);
            log.info("User account created with ID: {} for email: {}", user.getId(), user.getEmail());
            return user;
        } catch (DataIntegrityViolationException e) {
            // Handle race condition - database constraint violation
            String message = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

            if (message.contains("email") || message.contains("uk_user_email")) {
                log.warn("Registration attempt failed: email already exists (caught race condition)");
                throw new BaseException(
                        String.valueOf(HttpStatus.CONFLICT.value()),
                        "A user with this email or mobile number already exists");
            }

            if (message.contains("mobile") || message.contains("uk_user_mobile")) {
                log.warn("Registration attempt failed: mobile number already exists (caught race condition)");
                throw new BaseException(
                        String.valueOf(HttpStatus.CONFLICT.value()),
                        "A user with this email or mobile number already exists");
            }

            // Other constraint violations
            log.error("Database constraint violation during user creation: {}", e.getMessage());
            throw new BaseException(
                    String.valueOf(HttpStatus.BAD_REQUEST.value()),
                    "Registration failed due to data constraint violation");
        }
    }

    private User buildUser(AdminCompleteRegistrationRequest request) {
        User user = new User(request.getEmail(), passwordEncoder.encode(request.getPassword()));
        user.changeMobileNumber(Long.parseLong(request.getMobileNumber()));
        user.changeGender(Gender.valueOf(request.getGender()));

        if (request.getSkipEmailVerification()) {
            user.markEmailVerified();
        }

        // CRITICAL: Ensure USER role exists before assignment
        Role userRole = roleRepository.findByName("USER");
        if (userRole == null) {
            log.error("CRITICAL: USER role not found in database - system misconfiguration");
            throw new BaseException(
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    "System configuration error: USER role not found. Please contact administrator.");
        }

        user.assignRole(userRole);
        log.debug("Assigned USER role to new account: {}", request.getEmail());

        return user;
    }

    private void createCompleteProfile(User user) {
        CompleteProfile completeProfile = new CompleteProfile();
        completeProfile.setUser(user);
        completeProfile.setProfileCompleted(false);
        completeProfileRepository.save(completeProfile);
        log.debug("Created CompleteProfile for user: {}", user.getId());
    }
}
