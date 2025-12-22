package com.spring.jwt.ContactDetails;

import com.spring.jwt.dto.ResponseDto;
import com.spring.jwt.exception.ResourceAlreadyExistsException;
import com.spring.jwt.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception handler for ContactDetails module.
 * Provides consistent error responses and prevents information leakage.
 */
@RestControllerAdvice(basePackages = "com.spring.jwt.ContactDetails")
@Slf4j
public class ContactDetailsExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseDto<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Contact details resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseDto.error("Resource not found", "The requested contact details information was not found"));
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ResponseDto<Object>> handleResourceAlreadyExists(ResourceAlreadyExistsException ex) {
        log.warn("Contact details resource already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseDto.error("Resource already exists", "Contact details already exist for this user or mobile number is already registered"));
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ResponseDto<Object>> handleOptimisticLockingFailure(OptimisticLockingFailureException ex) {
        log.warn("Optimistic locking failure in contact details: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseDto.error("Version conflict", "The contact details have been modified by another transaction. Please refresh and try again."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDto<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid argument in contact details operation: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.error("Invalid input", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Validation failed for contact details request: {}", ex.getMessage());
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        StringBuilder errorMessage = new StringBuilder("Validation errors: ");
        fieldErrors.forEach((field, message) -> errorMessage.append(String.format("%s: %s; ", field, message)));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.error("Validation failed", errorMessage.toString()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseDto<Object>> handleRuntimeException(RuntimeException ex) {
        log.error("Unexpected error in contact details operation: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDto.error("Operation failed", "An error occurred while processing your contact details request"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error in contact details module: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDto.error("System error", "An unexpected error occurred. Please try again later."));
    }
}