package com.spring.jwt.profile.exception;

import com.spring.jwt.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.spring.jwt.profile")
@Slf4j
public class ProfileExceptionHandler {

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<ResponseDto<Object>> handleProfileNotFound(ProfileNotFoundException ex) {
        log.warn("Profile not found: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseDto.error("Profile not found", ex.getMessage()));
    }


    @ExceptionHandler(ProfileAccessDeniedException.class)
    public ResponseEntity<ResponseDto<Object>> handleAccessDenied(ProfileAccessDeniedException ex) {
        log.warn("Profile access denied: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ResponseDto.error("Access denied", "You do not have permission to access this resource"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseDto<Object>> handleSpringAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ResponseDto.error("Access denied", "You do not have permission to perform this action"));
    }

    @ExceptionHandler(DuplicateProfileException.class)
    public ResponseEntity<ResponseDto<Object>> handleDuplicateProfile(DuplicateProfileException ex) {
        log.warn("Duplicate profile: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ResponseDto.error("Profile already exists", ex.getMessage()));
    }

    @ExceptionHandler(ProfileValidationException.class)
    public ResponseEntity<ResponseDto<Object>> handleProfileValidation(ProfileValidationException ex) {
        log.warn("Profile validation failed: {}", ex.getMessage());

        String errorMessage = ex.getField() != null
                ? String.format("Field '%s' validation failed: %s", ex.getField(), ex.getMessage())
                : ex.getMessage();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.error("Validation failed", errorMessage));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Request validation failed: {}", ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        StringBuilder errorMessage = new StringBuilder("Validation errors: ");
        fieldErrors.forEach((field, message) -> errorMessage.append(String.format("%s: %s; ", field, message)));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.error("Validation failed", errorMessage.toString()));
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ResponseDto<Object>> handleOptimisticLocking(OptimisticLockingFailureException ex) {
        log.warn("Optimistic locking failure: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ResponseDto.error("Concurrent modification detected",
                        "The profile has been modified by another user. Please refresh and try again."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDto<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.error("Invalid request", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDto.error("Internal server error",
                        "An unexpected error occurred. Please try again later."));
    }
}
