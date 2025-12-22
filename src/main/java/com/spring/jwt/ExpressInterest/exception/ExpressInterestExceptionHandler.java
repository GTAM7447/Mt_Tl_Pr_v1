package com.spring.jwt.ExpressInterest.exception;

import com.spring.jwt.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.spring.jwt.ExpressInterest")
@Slf4j
public class ExpressInterestExceptionHandler {

    @ExceptionHandler(DailyLimitExceededException.class)
    public ResponseEntity<ResponseDto<Object>> handleDailyLimitExceeded(DailyLimitExceededException ex) {
        log.warn("Daily limit exceeded: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ResponseDto.error("Daily Limit Exceeded", ex.getMessage()));
    }

    @ExceptionHandler(InterestAlreadyExistsException.class)
    public ResponseEntity<ResponseDto<Object>> handleInterestAlreadyExists(InterestAlreadyExistsException ex) {
        log.warn("Interest already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseDto.error("Interest Already Exists", ex.getMessage()));
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<ResponseDto<Object>> handleInvalidStatusTransition(InvalidStatusTransitionException ex) {
        log.warn("Invalid status transition: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.error("Invalid Status Transition", ex.getMessage()));
    }

    @ExceptionHandler(InterestExpiredException.class)
    public ResponseEntity<ResponseDto<Object>> handleInterestExpired(InterestExpiredException ex) {
        log.warn("Interest expired: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.GONE)
                .body(ResponseDto.error("Interest Expired", ex.getMessage()));
    }

    @ExceptionHandler(InsufficientProfileCompletionException.class)
    public ResponseEntity<ResponseDto<Object>> handleInsufficientProfileCompletion(InsufficientProfileCompletionException ex) {
        log.warn("Insufficient profile completion: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                .body(ResponseDto.error("Profile Incomplete", ex.getMessage()));
    }

    @ExceptionHandler(SubscriptionRequiredException.class)
    public ResponseEntity<ResponseDto<Object>> handleSubscriptionRequired(SubscriptionRequiredException ex) {
        log.warn("Subscription required: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                .body(ResponseDto.error("Subscription Required", ex.getMessage()));
    }

    @ExceptionHandler(CompatibilityCalculationException.class)
    public ResponseEntity<ResponseDto<Object>> handleCompatibilityCalculation(CompatibilityCalculationException ex) {
        log.error("Compatibility calculation error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDto.error("Compatibility Calculation Failed", "Unable to calculate compatibility score"));
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ResponseDto<Object>> handleOptimisticLockingFailure(OptimisticLockingFailureException ex) {
        log.warn("Optimistic locking failure: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseDto.error("Concurrent Modification", 
                      "The interest has been modified by another user. Please refresh and try again."));
    }

    @ExceptionHandler(InappropriateContentException.class)
    public ResponseEntity<ResponseDto<Object>> handleInappropriateContent(InappropriateContentException ex) {
        log.warn("Inappropriate content detected: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.error("Inappropriate Content", 
                      "Your message contains inappropriate content. Please revise and try again."));
    }

    @ExceptionHandler(UserNotAvailableException.class)
    public ResponseEntity<ResponseDto<Object>> handleUserNotAvailable(UserNotAvailableException ex) {
        log.warn("User not available: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseDto.error("User Not Available", ex.getMessage()));
    }

    @ExceptionHandler(MatchingServiceException.class)
    public ResponseEntity<ResponseDto<Object>> handleMatchingService(MatchingServiceException ex) {
        log.error("Matching service error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ResponseDto.error("Matching Service Unavailable", 
                      "Unable to process matching request at this time. Please try again later."));
    }

    public static class DailyLimitExceededException extends RuntimeException {
        public DailyLimitExceededException(String message) {
            super(message);
        }
    }

    public static class InterestAlreadyExistsException extends RuntimeException {
        public InterestAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class InvalidStatusTransitionException extends RuntimeException {
        public InvalidStatusTransitionException(String message) {
            super(message);
        }
    }

    public static class InterestExpiredException extends RuntimeException {
        public InterestExpiredException(String message) {
            super(message);
        }
    }

    public static class InsufficientProfileCompletionException extends RuntimeException {
        public InsufficientProfileCompletionException(String message) {
            super(message);
        }
    }

    public static class SubscriptionRequiredException extends RuntimeException {
        public SubscriptionRequiredException(String message) {
            super(message);
        }
    }

    public static class CompatibilityCalculationException extends RuntimeException {
        public CompatibilityCalculationException(String message) {
            super(message);
        }
        
        public CompatibilityCalculationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class InappropriateContentException extends RuntimeException {
        public InappropriateContentException(String message) {
            super(message);
        }
    }

    public static class UserNotAvailableException extends RuntimeException {
        public UserNotAvailableException(String message) {
            super(message);
        }
    }

    public static class MatchingServiceException extends RuntimeException {
        public MatchingServiceException(String message) {
            super(message);
        }
        
        public MatchingServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}