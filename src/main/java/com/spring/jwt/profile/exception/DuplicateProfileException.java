package com.spring.jwt.profile.exception;


import com.spring.jwt.exception.ResourceAlreadyExistsException;

/**
 * Exception thrown when attempting to create a duplicate profile.
 */
public class DuplicateProfileException extends ResourceAlreadyExistsException {

    public DuplicateProfileException(String message) {
        super(message);
    }

    public DuplicateProfileException(Integer userId) {
        super("Profile already exists for user ID: " + userId);
    }
}
