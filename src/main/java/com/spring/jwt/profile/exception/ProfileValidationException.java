package com.spring.jwt.profile.exception;

/**
 * Exception thrown when profile validation fails.
 */
public class ProfileValidationException extends IllegalArgumentException {

    private final String field;
    private final Object rejectedValue;

    public ProfileValidationException(String message) {
        super(message);
        this.field = null;
        this.rejectedValue = null;
    }

    public ProfileValidationException(String field, Object rejectedValue, String message) {
        super(message);
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

    public String getField() {
        return field;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }
}
