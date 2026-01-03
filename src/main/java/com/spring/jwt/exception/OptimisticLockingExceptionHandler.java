package com.spring.jwt.exception;

import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class OptimisticLockingExceptionHandler {

    @ExceptionHandler({
        OptimisticLockException.class,
        OptimisticLockingFailureException.class,
        ObjectOptimisticLockingFailureException.class
    })
    public ResponseEntity<Map<String, Object>> handleOptimisticLockingFailure(Exception ex) {
        log.warn("Optimistic locking conflict detected: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.CONFLICT.value());
        errorResponse.put("error", "Conflict");
        errorResponse.put("message", "The record has been modified by another user. Please refresh and try again.");
        errorResponse.put("code", "OPTIMISTIC_LOCK_ERROR");
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}
