package com.spring.jwt.profile.exception;

import com.spring.jwt.exception.ResourceNotFoundException;

/**
 * Exception thrown when a profile is not found.
 */
public class ProfileNotFoundException extends ResourceNotFoundException {

    public ProfileNotFoundException(String message) {
        super(message);
    }

    public ProfileNotFoundException(Integer profileId) {
        super("Profile not found with ID: " + profileId);
    }

    public ProfileNotFoundException(String field, Object value) {
        super(String.format("Profile not found with %s: %s", field, value));
    }
}
