package com.spring.jwt.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Base exception for token validation failures
 * Provides specific error codes for different validation failure scenarios
 */
public class TokenValidationException extends AuthenticationException {
    
    private final String errorCode;
    
    public TokenValidationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public TokenValidationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
