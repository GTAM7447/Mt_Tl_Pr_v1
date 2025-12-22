package com.spring.jwt.profile.exception;

import org.springframework.security.access.AccessDeniedException;

/**
 * Exception thrown when a user attempts to access or modify a profile they
 * don't own.
 */
public class ProfileAccessDeniedException extends AccessDeniedException {

    public ProfileAccessDeniedException(String message) {
        super(message);
    }

    public ProfileAccessDeniedException(Integer profileId, Integer userId) {
        super(String.format("User %d does not have permission to access profile %d", userId, profileId));
    }

    public ProfileAccessDeniedException() {
        super("You do not have permission to access this profile");
    }
}
