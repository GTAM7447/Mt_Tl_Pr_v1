package com.spring.jwt.admin.service;

import com.spring.jwt.CompleteProfile.CompleteProfileRepository;
import com.spring.jwt.CompleteProfile.CompleteProfileService;
import com.spring.jwt.CompleteProfile.MissingProfileDTO;
import com.spring.jwt.ContactDetails.ContactDetailsRepository;
import com.spring.jwt.EducationAndProfession.EducationAndProfessionRepository;
import com.spring.jwt.FamilyBackground.FamilyBackgroundRepository;
import com.spring.jwt.HoroscopeDetails.HoroscopeDetailsRepository;
import com.spring.jwt.PartnerPreference.PartnerPreferenceRepository;
import com.spring.jwt.admin.dto.AdminCompleteRegistrationResponse;
import com.spring.jwt.entity.CompleteProfile;
import com.spring.jwt.exception.BaseException;
import com.spring.jwt.repository.UserProfileRepository;
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
    private final UserProfileRepository userProfileRepository;
    private final HoroscopeDetailsRepository horoscopeDetailsRepository;
    private final EducationAndProfessionRepository educationAndProfessionRepository;
    private final FamilyBackgroundRepository familyBackgroundRepository;
    private final ContactDetailsRepository contactDetailsRepository;
    private final PartnerPreferenceRepository partnerPreferenceRepository;

    public void calculateAndUpdate(Integer userId, AdminCompleteRegistrationResponse response) {
        try {
            log.debug("Starting profile completion calculation for user ID: {}", userId);
            
            // Fetch CompleteProfile and populate relationships
            CompleteProfile completeProfile = findAndPopulateCompleteProfile(userId);
            log.debug("CompleteProfile fetched and populated for user ID: {}", userId);
            
            // Calculate metrics
            recalculateProfileSync(completeProfile);
            log.debug("Profile recalculated for user ID: {}", userId);
            
            // Update response with metrics
            updateResponseWithMetrics(completeProfile, response);
            log.debug("Response metrics updated for user ID: {}", userId);
            
            // Update missing sections
            updateMissingSections(userId, response);
            log.debug("Missing sections updated for user ID: {}", userId);
            
        } catch (Exception e) {
            log.error("Error calculating profile completion for user ID: {}", userId, e);
            setDefaultMetrics(response);
        }
    }

    /**
     * Fetch CompleteProfile and manually populate all relationships.
     * This is necessary during admin registration because we skip async updates.
     */
    private CompleteProfile findAndPopulateCompleteProfile(Integer userId) {
        CompleteProfile completeProfile = completeProfileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new BaseException(
                        String.valueOf(HttpStatus.NOT_FOUND.value()),
                        "CompleteProfile not found for user ID: " + userId
                ));
        
        // Manually fetch and set all relationships
        userProfileRepository.findByUser_Id(userId).ifPresent(completeProfile::setUserProfile);
        horoscopeDetailsRepository.findByUser_Id(userId).ifPresent(completeProfile::setHoroscopeDetails);
        educationAndProfessionRepository.findByUser_Id(userId).ifPresent(completeProfile::setEducationAndProfession);
        familyBackgroundRepository.findByUser_Id(userId).ifPresent(completeProfile::setFamilyBackground);
        contactDetailsRepository.findByUser_Id(userId).ifPresent(completeProfile::setContactDetails);
        partnerPreferenceRepository.findByUser_Id(userId).ifPresent(completeProfile::setPartnerPreference);
        
        log.debug("Populated CompleteProfile relationships for user ID: {}", userId);
        return completeProfile;
    }

    private void updateMissingSections(Integer userId, AdminCompleteRegistrationResponse response) {
        MissingProfileDTO missingProfile = completeProfileService.checkMissingSections(userId);
        response.setMissingSections(missingProfile.getMissingSectionNames());
    }
    
    private void recalculateProfileSync(CompleteProfile completeProfile) {
        try {
            calculateCompletionMetrics(completeProfile);
            calculateStrengthMetrics(completeProfile);
            determineProfileQuality(completeProfile);
            updateVerificationStatus(completeProfile);
            completeProfileRepository.save(completeProfile);
            log.debug("Profile completeness calculated synchronously for user ID: {}", completeProfile.getUser().getId());
        } catch (Exception e) {
            log.error("Error calculating profile completeness: {}", e.getMessage());
            throw e;
        }
    }
    
    private void calculateCompletionMetrics(CompleteProfile profile) {
        int totalSections = 7;
        int completedSections = 0;
        int totalScore = 0;

        if (profile.getUserProfile() != null) {
            completedSections++;
            totalScore += 25;
        }
        if (profile.getContactDetails() != null) {
            completedSections++;
            totalScore += 20;
        }
        if (profile.getPartnerPreference() != null) {
            completedSections++;
            totalScore += 20;
        }
        if (profile.getEducationAndProfession() != null) {
            completedSections++;
            totalScore += 15;
        }
        if (profile.getFamilyBackground() != null) {
            completedSections++;
            totalScore += 10;
        }
        if (profile.getHoroscopeDetails() != null) {
            completedSections++;
            totalScore += 5;
        }
        if (profile.getDocuments() != null && !profile.getDocuments().isEmpty()) {
            completedSections++;
            totalScore += 5;
        }

        profile.setProfileCompleted(completedSections == totalSections);
        profile.setCompletionPercentage((completedSections * 100) / totalSections);
        profile.setCompletenessScore(totalScore);
        profile.setMissingSectionsCount(totalSections - completedSections);
    }
    
    private void calculateStrengthMetrics(CompleteProfile profile) {
        profile.setBasicInfoScore(profile.getUserProfile() != null ? 95 : 0);

        int contactScore = 0;
        if (profile.getContactDetails() != null) {
            contactScore = 60;
            if (profile.getMobileVerified()) contactScore += 20;
            if (profile.getEmailVerified()) contactScore += 20;
        }
        profile.setContactInfoScore(contactScore);

        profile.setPersonalDetailsScore(profile.getHoroscopeDetails() != null ? 90 : 0);
        profile.setFamilyInfoScore(profile.getFamilyBackground() != null ? 85 : 0);
        profile.setProfessionalInfoScore(profile.getEducationAndProfession() != null ? 85 : 0);
        profile.setPreferencesScore(profile.getPartnerPreference() != null ? 80 : 0);

        int docScore = 0;
        if (profile.getDocuments() != null && !profile.getDocuments().isEmpty()) {
            docScore = 40;
            if (profile.getHasProfilePhoto()) docScore += 30;
            if (profile.getIdentityVerified()) docScore += 30;
        }
        profile.setDocumentScore(docScore);
    }
    
    private void determineProfileQuality(CompleteProfile profile) {
        int score = profile.getCompletenessScore();
        
        if (score >= 90) {
            profile.setProfileQuality(CompleteProfile.ProfileQuality.EXCELLENT);
        } else if (score >= 75) {
            profile.setProfileQuality(CompleteProfile.ProfileQuality.VERY_GOOD);
        } else if (score >= 60) {
            profile.setProfileQuality(CompleteProfile.ProfileQuality.GOOD);
        } else if (score >= 40) {
            profile.setProfileQuality(CompleteProfile.ProfileQuality.FAIR);
        } else {
            profile.setProfileQuality(CompleteProfile.ProfileQuality.POOR);
        }
    }
    
    private void updateVerificationStatus(CompleteProfile profile) {
        boolean hasVerifications = profile.getMobileVerified() || 
                                 profile.getEmailVerified() || 
                                 profile.getIdentityVerified();

        if (profile.getMobileVerified() && profile.getEmailVerified() && profile.getIdentityVerified()) {
            profile.setVerificationStatus(CompleteProfile.VerificationStatus.VERIFIED);
        } else if (hasVerifications) {
            profile.setVerificationStatus(CompleteProfile.VerificationStatus.PENDING);
        } else {
            profile.setVerificationStatus(CompleteProfile.VerificationStatus.UNVERIFIED);
        }
    }

    private void updateResponseWithMetrics(CompleteProfile completeProfile, AdminCompleteRegistrationResponse response) {
        response.setCompleteProfileId(completeProfile.getCompleteProfileId().longValue());
        response.setCompletionPercentage(completeProfile.getCompletionPercentage());
        response.setProfileComplete(completeProfile.getProfileCompleted());
        response.setProfileQuality(completeProfile.getProfileQuality().name());
        
        log.info("Profile completion: {}% - Quality: {}",
                completeProfile.getCompletionPercentage(),
                completeProfile.getProfileQuality());
    }

    private void setDefaultMetrics(AdminCompleteRegistrationResponse response) {
        response.setCompletionPercentage(0);
        response.setProfileComplete(false);
        response.setProfileQuality("POOR");
    }
}
