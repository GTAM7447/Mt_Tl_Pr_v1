package com.spring.jwt.admin.service;

import com.spring.jwt.CompleteProfile.CompleteProfileRepository;
import com.spring.jwt.CompleteProfile.CompleteProfileService;
import com.spring.jwt.CompleteProfile.MissingProfileDTO;
import com.spring.jwt.admin.dto.AdminCompleteRegistrationResponse;
import com.spring.jwt.entity.CompleteProfile;
import com.spring.jwt.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileCompletionCalculator {

    private final CompleteProfileService completeProfileService;
    private final CompleteProfileRepository completeProfileRepository;

    public void calculateAndUpdate(Integer userId, AdminCompleteRegistrationResponse response) {
        try {
            updateMissingSections(userId, response);
            recalculateProfile(userId);
            updateResponseWithMetrics(userId, response);
        } catch (Exception e) {
            log.error("Error calculating profile completion for user ID: {}", userId, e);
            setDefaultMetrics(response);
        }
    }

    private void updateMissingSections(Integer userId, AdminCompleteRegistrationResponse response) {
        MissingProfileDTO missingProfile = completeProfileService.checkMissingSections(userId);
        response.setMissingSections(missingProfile.getMissingSectionNames());
    }

    private void recalculateProfile(Integer userId) {
        CompleteProfile completeProfile = findCompleteProfile(userId);
        completeProfileService.recalcAndSave(completeProfile);
    }

    private void updateResponseWithMetrics(Integer userId, AdminCompleteRegistrationResponse response) {
        CompleteProfile completeProfile = findCompleteProfile(userId);
        response.setCompleteProfileId(completeProfile.getCompleteProfileId().longValue());
        response.setCompletionPercentage(completeProfile.getCompletionPercentage());
        response.setProfileComplete(completeProfile.getProfileCompleted());
        response.setProfileQuality(completeProfile.getProfileQuality().name());
        
        log.info("Profile completion: {}% - Quality: {}",
                completeProfile.getCompletionPercentage(),
                completeProfile.getProfileQuality());
    }

    private CompleteProfile findCompleteProfile(Integer userId) {
        return completeProfileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new BaseException(
                        String.valueOf(HttpStatus.NOT_FOUND.value()),
                        "CompleteProfile not found for user ID: " + userId
                ));
    }

    private void setDefaultMetrics(AdminCompleteRegistrationResponse response) {
        response.setCompletionPercentage(0);
        response.setProfileComplete(false);
        response.setProfileQuality("POOR");
    }
}
