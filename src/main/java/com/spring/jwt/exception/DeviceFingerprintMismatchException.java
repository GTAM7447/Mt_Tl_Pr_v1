package com.spring.jwt.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception thrown when device fingerprint validation fails
 * This indicates the token was generated on a different device/browser
 */
public class DeviceFingerprintMismatchException extends AuthenticationException {
    
    public DeviceFingerprintMismatchException(String message) {
        super(message);
    }
    
    public DeviceFingerprintMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
