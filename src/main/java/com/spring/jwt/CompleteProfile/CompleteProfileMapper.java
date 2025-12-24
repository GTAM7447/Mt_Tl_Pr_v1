package com.spring.jwt.CompleteProfile;

import com.spring.jwt.CompleteProfile.dto.CompleteProfileResponse;
import com.spring.jwt.ContactDetails.ContactDetailsMapper;
import com.spring.jwt.Document.DocumentRepository;
import com.spring.jwt.EducationAndProfession.EducationAndProfessionMapper;
import com.spring.jwt.Enums.DocumentType;
import com.spring.jwt.FamilyBackground.FamilyBackgroundMapper;
import com.spring.jwt.HoroscopeDetails.HoroscopeDetailsMapper;
import com.spring.jwt.PartnerPreference.PartnerPreferenceMapper;
import com.spring.jwt.entity.CompleteProfile;
import com.spring.jwt.entity.Document;
import com.spring.jwt.profile.mapper.ProfileDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mapper for CompleteProfile entity and DTOs.
 * Uses functional programming approach for clean, maintainable mapping logic.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CompleteProfileMapper {

    private final ProfileDtoMapper profileMapper;
    private final HoroscopeDetailsMapper horoscopeMapper;
    private final EducationAndProfessionMapper educationMapper;
    private final FamilyBackgroundMapper familyBackgroundMapper;
    private final PartnerPreferenceMapper partnerPreferenceMapper;
    private final ContactDetailsMapper contactDetailsMapper;
    private final DocumentRepository documentRepository;

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

        // Profile photo data
        ProfilePhotoData photoData = getProfilePhotoData(entity.getUser() != null ? entity.getUser().getId() : null);
        response.setProfilePhotoBase64(photoData.base64Data);
        response.setProfilePhotoContentType(photoData.contentType);
        response.setHasProfilePhoto(photoData.hasPhoto);

        return response;
    }

    /**
     * Convert CompleteProfile entity to public-safe response DTO.
     * This method excludes sensitive information like contact details, 
     * personal documents, and other private data.
     */
    public CompleteProfileResponse toPublicResponse(CompleteProfile entity) {
        if (entity == null) {
            return null;
        }

        CompleteProfileResponse response = new CompleteProfileResponse();
        
        // Basic information (public-safe)
        response.setCompleteProfileId(entity.getCompleteProfileId());
        response.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
        response.setProfileCompleted(entity.getProfileCompleted());
        response.setCompletionPercentage(entity.getCompletionPercentage());
        response.setCompletenessScore(entity.getCompletenessScore());
        response.setProfileQuality(entity.getProfileQuality() != null ? entity.getProfileQuality().name() : null);
        response.setProfileVisibility(entity.getProfileVisibility() != null ? entity.getProfileVisibility().name() : null);
        response.setVerificationStatus(entity.getVerificationStatus() != null ? entity.getVerificationStatus().name() : null);
        response.setLastUpdated(entity.getUpdatedAt());
        response.setCreatedAt(entity.getCreatedAt());
        
        // Profile sections (public-safe versions)
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
        
        // Exclude sensitive contact details for public viewing
        response.setContactDetails(null);
        
        // Exclude documents for public viewing
        response.setDocuments(null);
        
        // Public-safe strength metrics (exclude sensitive verification info)
        response.setStrengthMetrics(buildPublicStrengthMetrics(entity));

        // Profile photo data (safe for public viewing)
        ProfilePhotoData photoData = getProfilePhotoData(entity.getUser() != null ? entity.getUser().getId() : null);
        response.setProfilePhotoBase64(photoData.base64Data);
        response.setProfilePhotoContentType(photoData.contentType);
        response.setHasProfilePhoto(photoData.hasPhoto);

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

    /**
     * Build public-safe strength metrics from entity data.
     * Excludes sensitive verification information.
     */
    private CompleteProfileResponse.ProfileStrengthMetrics buildPublicStrengthMetrics(CompleteProfile entity) {
        CompleteProfileResponse.ProfileStrengthMetrics metrics = new CompleteProfileResponse.ProfileStrengthMetrics();
        
        metrics.setBasicInfoScore(entity.getBasicInfoScore());
        // Hide contact info score for public viewing
        metrics.setContactInfoScore(null);
        metrics.setPersonalDetailsScore(entity.getPersonalDetailsScore());
        metrics.setFamilyInfoScore(entity.getFamilyInfoScore());
        metrics.setProfessionalInfoScore(entity.getProfessionalInfoScore());
        metrics.setPreferencesScore(entity.getPreferencesScore());
        // Hide document score for public viewing
        metrics.setDocumentScore(null);
        
        metrics.setHasProfilePhoto(entity.getHasProfilePhoto());
        // Hide verification details for public viewing
        metrics.setMobileVerified(null);
        metrics.setEmailVerified(null);
        metrics.setIdentityVerified(null);

        return metrics;
    }

    /**
     * Helper method to fetch and convert profile photo to base64
     * Reuses the same logic as ProfileDtoMapper
     */
    private ProfilePhotoData getProfilePhotoData(Integer userId) {
        if (userId == null) {
            return new ProfilePhotoData(null, null, false);
        }
        
        try {
            Optional<Document> profilePhotoDoc = documentRepository.findByUserIdAndDocumentType(userId, DocumentType.PROFILE_PHOTO);
            
            if (profilePhotoDoc.isPresent()) {
                Document document = profilePhotoDoc.get();
                String base64Data = Base64.getEncoder().encodeToString(document.getFileData());
                return new ProfilePhotoData(base64Data, document.getContentType(), true);
            } else {
                return new ProfilePhotoData(null, null, false);
            }
        } catch (Exception e) {
            log.warn("Error fetching profile photo for user {}: {}", userId, e.getMessage());
            return new ProfilePhotoData(null, null, false);
        }
    }

    /**
     * Helper class to hold profile photo data
     * Reused from ProfileDtoMapper pattern
     */
    private static class ProfilePhotoData {
        final String base64Data;
        final String contentType;
        final boolean hasPhoto;

        ProfilePhotoData(String base64Data, String contentType, boolean hasPhoto) {
            this.base64Data = base64Data;
            this.contentType = contentType;
            this.hasPhoto = hasPhoto;
        }
    }
}