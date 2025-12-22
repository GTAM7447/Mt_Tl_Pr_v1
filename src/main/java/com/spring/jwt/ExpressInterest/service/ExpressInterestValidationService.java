package com.spring.jwt.ExpressInterest.service;

import com.spring.jwt.ExpressInterest.dto.request.ExpressInterestCreateRequest;
import com.spring.jwt.ExpressInterest.dto.request.ExpressInterestUpdateRequest;
import com.spring.jwt.ExpressInterest.repository.ExpressInterestRepository;
import com.spring.jwt.entity.User;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.Subscription.SubscriptionService;
import com.spring.jwt.CompleteProfile.CompleteProfileService;
import com.spring.jwt.CompleteProfile.dto.CompleteProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class ExpressInterestValidationService {

    private final ExpressInterestRepository interestRepository;
    private final UserRepository userRepository;
    private final CompleteProfileService completeProfileService;

    @Value("${app.express-interest.daily-limit:10}")
    private Integer dailyLimit;

    @Value("${app.express-interest.min-profile-completion:60}")
    private Integer minProfileCompletion;

    @Value("${app.express-interest.max-message-length:500}")
    private Integer maxMessageLength;

    private static final Set<String> VALID_SOURCE_PLATFORMS = Set.of("WEB", "MOBILE", "API");
    private static final Set<String> VALID_STATUS_UPDATES = Set.of("ACCEPTED", "DECLINED", "WITHDRAWN");

    public void validateCreateRequest(ExpressInterestCreateRequest request, Integer currentUserId) {
        log.debug("Validating express interest create request for user: {}", currentUserId);
        
        List<String> errors = new ArrayList<>();

        validateBasicFields(request, errors);
        validateTargetUser(request.getToUserId(), currentUserId, errors);
        validateDuplicateInterest(currentUserId, request.getToUserId(), errors);
        validateDailyLimit(currentUserId, errors);
        validateUserSubscription(currentUserId, errors);
        validateProfileCompleteness(currentUserId, errors);
        validateTargetUserAvailability(request.getToUserId(), errors);
        
        if (!errors.isEmpty()) {
            String errorMessage = "Validation failed: " + String.join(", ", errors);
            log.warn("Express interest create validation failed for user {}: {}", currentUserId, errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        
        log.debug("Express interest create request validation passed for user: {}", currentUserId);
    }

    public void validateUpdateRequest(ExpressInterestUpdateRequest request, Integer currentUserId, Long interestId) {
        log.debug("Validating express interest update request for user: {} and interest: {}", currentUserId, interestId);
        
        List<String> errors = new ArrayList<>();
        
        validateUpdateFields(request, errors);

        validateInterestExists(interestId, errors);
        validateUserPermission(interestId, currentUserId, request.getStatus(), errors);
        validateStatusTransition(interestId, request.getStatus(), errors);
        
        if (!errors.isEmpty()) {
            String errorMessage = "Validation failed: " + String.join(", ", errors);
            log.warn("Express interest update validation failed for user {} and interest {}: {}", 
                    currentUserId, interestId, errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        
        log.debug("Express interest update request validation passed for user: {} and interest: {}", 
                currentUserId, interestId);
    }

    private void validateBasicFields(ExpressInterestCreateRequest request, List<String> errors) {
        if (request.getToUserId() == null || request.getToUserId() <= 0) {
            errors.add("Target user ID is required and must be positive");
        }

        if (request.getMessage() != null && request.getMessage().length() > maxMessageLength) {
            errors.add("Message cannot exceed " + maxMessageLength + " characters");
        }

        if (request.getSourcePlatform() != null && 
            !VALID_SOURCE_PLATFORMS.contains(request.getSourcePlatform())) {
            errors.add("Invalid source platform. Valid options: " + String.join(", ", VALID_SOURCE_PLATFORMS));
        }

        if (request.getMessage() != null && containsInappropriateContent(request.getMessage())) {
            errors.add("Message contains inappropriate content");
        }
    }

    private void validateUpdateFields(ExpressInterestUpdateRequest request, List<String> errors) {
        if (request.getVersion() == null || request.getVersion() < 0) {
            errors.add("Version is required for optimistic locking and must be non-negative");
        }

        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            errors.add("Status is required for update");
        } else if (!VALID_STATUS_UPDATES.contains(request.getStatus())) {
            errors.add("Invalid status. Valid options: " + String.join(", ", VALID_STATUS_UPDATES));
        }

        if (request.getResponseMessage() != null && 
            request.getResponseMessage().length() > maxMessageLength) {
            errors.add("Response message cannot exceed " + maxMessageLength + " characters");
        }

        if (request.getResponseMessage() != null && 
            containsInappropriateContent(request.getResponseMessage())) {
            errors.add("Response message contains inappropriate content");
        }
    }


    private void validateTargetUser(Integer toUserId, Integer currentUserId, List<String> errors) {
        if (toUserId.equals(currentUserId)) {
            errors.add("Cannot express interest in yourself");
            return;
        }

        Optional<User> targetUser = userRepository.findById(toUserId);
        if (targetUser.isEmpty()) {
            errors.add("Target user not found");
            return;
        }

        User user = targetUser.get();
        if (user.getEmailVerified() == null || !user.getEmailVerified()) {
            errors.add("Target user account is not verified");
        }
    }

    private void validateDuplicateInterest(Integer fromUserId, Integer toUserId, List<String> errors) {
        if (interestRepository.existsByFromUserIdAndToUserId(fromUserId, toUserId)) {
            errors.add("Interest already exists for this user");
        }
    }


    private void validateDailyLimit(Integer userId, List<String> errors) {
        Long sentToday = interestRepository.countInterestsSentToday(userId);
        if (sentToday >= dailyLimit) {
            errors.add("Daily limit of " + dailyLimit + " interests exceeded. Try again tomorrow.");
        }
    }

    private void validateUserSubscription(Integer userId, List<String> errors) {
        try {
            log.debug("Subscription validation skipped for user {}", userId);
        } catch (Exception e) {
            log.warn("Error checking subscription for user {}: {}", userId, e.getMessage());
        }
    }

    private void validateProfileCompleteness(Integer userId, List<String> errors) {
        try {
            CompleteProfileResponse completeProfile = completeProfileService.getByUserId(userId);
            if (completeProfile == null) {
                log.warn("User {} does not have a complete profile but can still send interests", userId);
                return;
            }
            
            log.debug("Profile completeness validation passed for user {}", userId);
        } catch (Exception e) {
            log.warn("Error checking profile completeness for user {}: {}", userId, e.getMessage());
        }
    }


    private void validateTargetUserAvailability(Integer toUserId, List<String> errors) {
        try {
            Optional<User> targetUser = userRepository.findById(toUserId);
            if (targetUser.isPresent()) {
                User user = targetUser.get();

                if (user.getAccountLockedUntil() != null && 
                    user.getAccountLockedUntil().isAfter(java.time.LocalDateTime.now())) {
                    errors.add("Target user account is temporarily locked");
                }

                if (user.getEmailVerified() == null || !user.getEmailVerified()) {
                    errors.add("Target user has not verified their email address");
                }
            }
        } catch (Exception e) {
            log.warn("Error checking target user availability {}: {}", toUserId, e.getMessage());
        }
    }

    private void validateInterestExists(Long interestId, List<String> errors) {
        if (!interestRepository.existsById(interestId)) {
            errors.add("Interest not found");
        }
    }

    private void validateUserPermission(Long interestId, Integer currentUserId, String status, List<String> errors) {
        Optional<com.spring.jwt.entity.ExpressInterest> interestOpt = interestRepository.findById(interestId);
        if (interestOpt.isEmpty()) {
            return;
        }

        com.spring.jwt.entity.ExpressInterest interest = interestOpt.get();
        
        if ("ACCEPTED".equals(status) || "DECLINED".equals(status)) {

            if (!currentUserId.equals(interest.getToUserId())) {
                errors.add("Only the receiver can accept or decline an interest");
            }
        } else if ("WITHDRAWN".equals(status)) {

            if (!currentUserId.equals(interest.getFromUserId())) {
                errors.add("Only the sender can withdraw an interest");
            }
        }
    }

    private void validateStatusTransition(Long interestId, String newStatus, List<String> errors) {
        Optional<com.spring.jwt.entity.ExpressInterest> interestOpt = interestRepository.findById(interestId);
        if (interestOpt.isEmpty()) {
            return;
        }

        com.spring.jwt.entity.ExpressInterest interest = interestOpt.get();
        String currentStatus = interest.getStatus().name();

        if (interest.isExpired()) {
            errors.add("Cannot update expired interest");
            return;
        }

        switch (currentStatus) {
            case "PENDING":
                if (!Set.of("ACCEPTED", "DECLINED", "WITHDRAWN").contains(newStatus)) {
                    errors.add("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            case "ACCEPTED":
            case "DECLINED":
            case "WITHDRAWN":
            case "EXPIRED":
                errors.add("Cannot update interest in " + currentStatus + " status");
                break;
            default:
                errors.add("Unknown current status: " + currentStatus);
        }
    }

    private boolean containsInappropriateContent(String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }

        String lowerMessage = message.toLowerCase();

        Set<String> inappropriateWords = Set.of(
            "spam", "scam", "fake", "money", "cash", "loan", "investment",
            "whatsapp", "telegram", "instagram", "facebook", "contact me at"
        );

        for (String word : inappropriateWords) {
            if (lowerMessage.contains(word)) {
                log.warn("Inappropriate content detected in message: {}", word);
                return true;
            }
        }

        if (lowerMessage.matches(".*\\b\\d{10}\\b.*") || lowerMessage.matches(".*\\+\\d{1,3}\\s?\\d{10}.*")) {
            log.warn("Phone number detected in message");
            return true;
        }

        if (lowerMessage.matches(".*\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b.*")) {
            log.warn("Email address detected in message");
            return true;
        }

        return false;
    }

    public void validateCompatibilityCalculation(Integer userId1, Integer userId2) {
        List<String> errors = new ArrayList<>();

        if (userId1 == null || userId1 <= 0) {
            errors.add("First user ID is required and must be positive");
        }

        if (userId2 == null || userId2 <= 0) {
            errors.add("Second user ID is required and must be positive");
        }

        if (userId1 != null && userId2 != null && userId1.equals(userId2)) {
            errors.add("Cannot calculate compatibility with yourself");
        }

        if (userId1 != null && !userRepository.existsById(userId1)) {
            errors.add("First user not found");
        }

        if (userId2 != null && !userRepository.existsById(userId2)) {
            errors.add("Second user not found");
        }

        if (!errors.isEmpty()) {
            String errorMessage = "Compatibility validation failed: " + String.join(", ", errors);
            log.warn("Compatibility validation failed: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public void validateBulkOperation(String operation, List<Long> interestIds, Integer adminUserId) {
        List<String> errors = new ArrayList<>();

        if (operation == null || operation.trim().isEmpty()) {
            errors.add("Operation is required");
        }

        if (interestIds == null || interestIds.isEmpty()) {
            errors.add("Interest IDs are required");
        }

        if (adminUserId == null || adminUserId <= 0) {
            errors.add("Admin user ID is required");
        }

        Set<String> validOperations = Set.of("EXPIRE", "DELETE", "RESTORE");
        if (operation != null && !validOperations.contains(operation.toUpperCase())) {
            errors.add("Invalid operation. Valid options: " + String.join(", ", validOperations));
        }

        if (interestIds != null && !interestIds.isEmpty()) {
            for (Long interestId : interestIds) {
                if (interestId == null || interestId <= 0) {
                    errors.add("All interest IDs must be positive");
                    break;
                }
            }
        }

        if (!errors.isEmpty()) {
            String errorMessage = "Bulk operation validation failed: " + String.join(", ", errors);
            log.warn("Bulk operation validation failed: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }
}