package com.spring.jwt.ExpressInterest.service;

import com.spring.jwt.CompleteProfile.CompleteProfileRepository;
import com.spring.jwt.entity.CompleteProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProfileCompletenessValidator {

    private final CompleteProfileRepository completeProfileRepository;

    @Value("${app.express-interest.min-profile-completion:50}")
    private Integer minProfileCompletion;

    @Value("${app.express-interest.require-profile-completion:true}")
    private Boolean requireProfileCompletion;

    public void validateProfileCompleteness(Integer userId, String userType) {
        if (!requireProfileCompletion) {
            log.debug("Profile completion validation disabled");
            return;
        }

        CompleteProfile completeProfile = completeProfileRepository.findByUser_Id(userId)
                .orElse(null);

        if (completeProfile == null) {
            log.warn("No complete profile found for user {}", userId);
            throw new IllegalStateException(
                    String.format("Please complete your profile before sending interests. %s profile not found.", userType)
            );
        }

        Integer completionPercentage = completeProfile.getCompletionPercentage();
        
        if (completionPercentage == null || completionPercentage < minProfileCompletion) {
            log.warn("User {} has insufficient profile completion: {}%", userId, completionPercentage);
            throw new IllegalStateException(
                    String.format("Your profile is only %d%% complete. Please complete at least %d%% of your profile before sending interests.",
                            completionPercentage != null ? completionPercentage : 0,
                            minProfileCompletion)
            );
        }

        log.debug("Profile completeness validated for user {}: {}%", userId, completionPercentage);
    }

    public void validateBothProfilesCompleteness(Integer fromUserId, Integer toUserId) {
        validateProfileCompleteness(fromUserId, "Your");
        validateProfileCompleteness(toUserId, "Target user's");
    }

    public boolean isProfileComplete(Integer userId) {
        return completeProfileRepository.findByUser_Id(userId)
                .map(cp -> cp.getCompletionPercentage() != null && cp.getCompletionPercentage() >= minProfileCompletion)
                .orElse(false);
    }

    public Integer getProfileCompletionPercentage(Integer userId) {
        return completeProfileRepository.findByUser_Id(userId)
                .map(CompleteProfile::getCompletionPercentage)
                .orElse(0);
    }
}
