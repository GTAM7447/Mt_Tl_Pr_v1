package com.spring.jwt.CompleteProfile;

import com.spring.jwt.CompleteProfile.dto.CompleteProfileResponse;
import com.spring.jwt.ContactDetails.ContactDetailsMapper;
import com.spring.jwt.EducationAndProfession.EducationAndProfessionMapper;
import com.spring.jwt.FamilyBackground.FamilyBackgroundMapper;
import com.spring.jwt.HoroscopeDetails.HoroscopeDetailsMapper;
import com.spring.jwt.PartnerPreference.PartnerPreferenceMapper;
import com.spring.jwt.entity.CompleteProfile;
import com.spring.jwt.entity.Document;
import com.spring.jwt.profile.mapper.ProfileDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for CompleteProfile entity and DTOs.
 * Uses functional programming approach for clean, maintainable mapping logic.
 */
@Component
@RequiredArgsConstructor
public class CompleteProfileMapper {

    private final ProfileDtoMapper profileMapper;
    private final HoroscopeDetailsMapper horoscopeMapper;
    private final EducationAndProfessionMapper educationMapper;
    private final FamilyBackgroundMapper familyBackgroundMapper;
    private final PartnerPreferenceMapper partnerPreferenceMapper;
    private final ContactDetailsMapper contactDetailsMapper;

    /**
     * Convert CompleteProfile entity to response DTO.
     */
    public CompleteProfileResponse toResponse(CompleteProfile entity) {
        if (entity == null) {
            return null;
        }

        CompleteProfileResponse response = new CompleteProfileResponse();
        
        // Basic information
        response.setCompleteProfileId(entity.getCompleteProfileId());
        response.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
        response.setProfileCompleted(entity.getProfileCompleted());
        response.setCompletionPercentage(entity.getCompletionPercentage());
        response.setCompletenessScore(entity.getCompletenessScore());
        response.setProfileQuality(entity.getProfileQuality() != null ? entity.getProfileQuality().name() : null);
        response.setMissingSectionsCount(entity.getMissingSectionsCount());
        response.setProfileVisibility(entity.getProfileVisibility() != null ? entity.getProfileVisibility().name() : null);
        response.setVerificationStatus(entity.getVerificationStatus() != null ? entity.getVerificationStatus().name() : null);
        response.setLastUpdated(entity.getUpdatedAt());
        response.setCreatedAt(entity.getCreatedAt());
        response.setVersion(entity.getVersion());

        // Profile sections (only include if not null)
        response.setUserProfile(entity.getUserProfile() != null ? 
                profileMapper.toResponse(entity.getUserProfile()) : null);
        response.setHoroscopeDetails(entity.getHoroscopeDetails() != null ? 
                horoscopeMapper.toResponse(entity.getHoroscopeDetails()) : null);
        response.setEducationAndProfession(entity.getEducationAndProfession() != null ? 
                educationMapper.toResponse(entity.getEducationAndProfession()) : null);
        response.setFamilyBackground(entity.getFamilyBackground() != null ? 
                familyBackgroundMapper.toResponse(entity.getFamilyBackground()) : null);
        response.setPartnerPreference(entity.getPartnerPreference() != null ? 
                partnerPreferenceMapper.toResponse(entity.getPartnerPreference()) : null);
        response.setContactDetails(entity.getContactDetails() != null ? 
                contactDetailsMapper.toResponse(entity.getContactDetails()) : null);

        // Documents
        response.setDocuments(mapDocuments(entity.getDocuments()));

        // Missing sections
        response.setMissingSections(calculateMissingSections(entity));

        // Strength metrics
        response.setStrengthMetrics(buildStrengthMetrics(entity));

        return response;
    }

    /**
     * Build missing profile DTO with enhanced analytics.
     */
    public MissingProfileDTO toMissingProfileDTO(CompleteProfile entity) {
        if (entity == null) {
            return null;
        }

        MissingProfileDTO dto = new MissingProfileDTO();
        
        // Section completion status
        dto.setUserProfile(entity.getUserProfile() != null);
        dto.setHoroscopeDetails(entity.getHoroscopeDetails() != null);
        dto.setEducationAndProfession(entity.getEducationAndProfession() != null);
        dto.setFamilyBackground(entity.getFamilyBackground() != null);
        dto.setPartnerPreference(entity.getPartnerPreference() != null);
        dto.setContactDetails(entity.getContactDetails() != null);
        dto.setDocument(entity.getDocuments() != null && !entity.getDocuments().isEmpty());

        // Completion metrics
        dto.setCompletionPercentage(entity.getCompletionPercentage());
        dto.setTotalSections(7);
        dto.setCompletedSections(7 - entity.getMissingSectionsCount());
        dto.setMissingSections(entity.getMissingSectionsCount());
        dto.setProfileQuality(entity.getProfileQuality() != null ? entity.getProfileQuality().name() : "POOR");

        // Missing section names
        dto.setMissingSectionNames(calculateMissingSections(entity));

        // Priority sections (sections that have high impact on profile score)
        dto.setPrioritySections(calculatePrioritySections(entity));

        // Estimated completion time
        dto.setEstimatedCompletionTime(calculateEstimatedCompletionTime(entity));

        // Recommendations
        dto.setRecommendations(generateRecommendations(entity));

        return dto;
    }

    /**
     * Map documents to document info DTOs.
     */
    private List<CompleteProfileResponse.DocumentInfo> mapDocuments(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return new ArrayList<>();
        }

        return documents.stream()
                .map(this::mapDocument)
                .collect(Collectors.toList());
    }

    /**
     * Map single document to document info DTO.
     */
    private CompleteProfileResponse.DocumentInfo mapDocument(Document document) {
        CompleteProfileResponse.DocumentInfo info = new CompleteProfileResponse.DocumentInfo();
        info.setDocumentId(document.getDocumentId());
        info.setDocumentType(document.getDocumentType() != null ? document.getDocumentType().name() : null);
        info.setDocumentName(document.getFileName());
//        info.setVerificationStatus(document.get() != null ? document.getVerificationStatus().name() : null);
        info.setUploadedAt(document.getUploadedAt());
        return info;
    }

    /**
     * Calculate missing sections list.
     */
    private List<String> calculateMissingSections(CompleteProfile entity) {
        List<String> missingSections = new ArrayList<>();

        if (entity.getUserProfile() == null) {
            missingSections.add("userProfile");
        }
        if (entity.getHoroscopeDetails() == null) {
            missingSections.add("horoscopeDetails");
        }
        if (entity.getEducationAndProfession() == null) {
            missingSections.add("educationAndProfession");
        }
        if (entity.getFamilyBackground() == null) {
            missingSections.add("familyBackground");
        }
        if (entity.getPartnerPreference() == null) {
            missingSections.add("partnerPreference");
        }
        if (entity.getContactDetails() == null) {
            missingSections.add("contactDetails");
        }
        if (entity.getDocuments() == null || entity.getDocuments().isEmpty()) {
            missingSections.add("documents");
        }

        return missingSections;
    }

    /**
     * Calculate priority sections that should be completed first.
     */
    private List<String> calculatePrioritySections(CompleteProfile entity) {
        List<String> prioritySections = new ArrayList<>();

        if (entity.getUserProfile() == null) {
            prioritySections.add("userProfile");
        }

        if (entity.getContactDetails() == null) {
            prioritySections.add("contactDetails");
        }
        if (entity.getPartnerPreference() == null) {
            prioritySections.add("partnerPreference");
        }

        return prioritySections;
    }

    /**
     * Calculate estimated completion time in minutes.
     */
    private Integer calculateEstimatedCompletionTime(CompleteProfile entity) {
        int totalMinutes = 0;
        
        if (entity.getUserProfile() == null) totalMinutes += 10;
        if (entity.getHoroscopeDetails() == null) totalMinutes += 5;
        if (entity.getEducationAndProfession() == null) totalMinutes += 8;
        if (entity.getFamilyBackground() == null) totalMinutes += 12;
        if (entity.getPartnerPreference() == null) totalMinutes += 15;
        if (entity.getContactDetails() == null) totalMinutes += 7;
        if (entity.getDocuments() == null || entity.getDocuments().isEmpty()) totalMinutes += 10;

        return totalMinutes;
    }

    /**
     * Generate completion recommendations.
     */
    private List<MissingProfileDTO.CompletionRecommendation> generateRecommendations(CompleteProfile entity) {
        List<MissingProfileDTO.CompletionRecommendation> recommendations = new ArrayList<>();

        if (entity.getUserProfile() == null) {
            recommendations.add(new MissingProfileDTO.CompletionRecommendation(
                    "userProfile",
                    "Complete Basic Profile",
                    "Add your basic information to make your profile visible to others",
                    "CRITICAL",
                    10,
                    25
            ));
        }

        if (entity.getContactDetails() == null) {
            recommendations.add(new MissingProfileDTO.CompletionRecommendation(
                    "contactDetails",
                    "Add Contact Information",
                    "Provide contact details so interested matches can reach you",
                    "HIGH",
                    7,
                    20
            ));
        }

        if (entity.getPartnerPreference() == null) {
            recommendations.add(new MissingProfileDTO.CompletionRecommendation(
                    "partnerPreference",
                    "Define Partner Preferences",
                    "Specify your partner preferences to get better matches",
                    "HIGH",
                    15,
                    20
            ));
        }

        if (entity.getEducationAndProfession() == null) {
            recommendations.add(new MissingProfileDTO.CompletionRecommendation(
                    "educationAndProfession",
                    "Add Professional Details",
                    "Include your education and career information",
                    "MEDIUM",
                    8,
                    15
            ));
        }

        if (entity.getFamilyBackground() == null) {
            recommendations.add(new MissingProfileDTO.CompletionRecommendation(
                    "familyBackground",
                    "Share Family Background",
                    "Add family information to help matches understand your background",
                    "MEDIUM",
                    12,
                    10
            ));
        }

        if (entity.getHoroscopeDetails() == null) {
            recommendations.add(new MissingProfileDTO.CompletionRecommendation(
                    "horoscopeDetails",
                    "Include Horoscope Details",
                    "Add horoscope information for astrological compatibility",
                    "LOW",
                    5,
                    5
            ));
        }

        if (entity.getDocuments() == null || entity.getDocuments().isEmpty()) {
            recommendations.add(new MissingProfileDTO.CompletionRecommendation(
                    "documents",
                    "Upload Profile Documents",
                    "Add photos and verification documents to build trust",
                    "MEDIUM",
                    10,
                    15
            ));
        }

        return recommendations;
    }

    /**
     * Build strength metrics from entity data.
     */
    private CompleteProfileResponse.ProfileStrengthMetrics buildStrengthMetrics(CompleteProfile entity) {
        CompleteProfileResponse.ProfileStrengthMetrics metrics = new CompleteProfileResponse.ProfileStrengthMetrics();
        
        metrics.setBasicInfoScore(entity.getBasicInfoScore());
        metrics.setContactInfoScore(entity.getContactInfoScore());
        metrics.setPersonalDetailsScore(entity.getPersonalDetailsScore());
        metrics.setFamilyInfoScore(entity.getFamilyInfoScore());
        metrics.setProfessionalInfoScore(entity.getProfessionalInfoScore());
        metrics.setPreferencesScore(entity.getPreferencesScore());
        metrics.setDocumentScore(entity.getDocumentScore());
        
        metrics.setHasProfilePhoto(entity.getHasProfilePhoto());
        metrics.setMobileVerified(entity.getMobileVerified());
        metrics.setEmailVerified(entity.getEmailVerified());
        metrics.setIdentityVerified(entity.getIdentityVerified());

        return metrics;
    }
}