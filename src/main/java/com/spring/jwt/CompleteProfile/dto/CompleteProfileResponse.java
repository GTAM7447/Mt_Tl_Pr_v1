package com.spring.jwt.CompleteProfile.dto;

import com.spring.jwt.ContactDetails.dto.ContactDetailsResponse;
import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionResponse;
import com.spring.jwt.FamilyBackground.dto.FamilyBackgroundResponse;
import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceResponse;
import com.spring.jwt.dto.horoscope.HoroscopeResponse;

import com.spring.jwt.profile.dto.response.ProfileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for complete profile information.
 * Aggregates all profile sections with completeness metrics and metadata.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Complete profile response with all sections and completeness metrics")
public class CompleteProfileResponse {

    @Schema(description = "Complete profile ID", example = "1")
    private Integer completeProfileId;

    @Schema(description = "User ID", example = "123")
    private Integer userId;

    @Schema(description = "User profile information")
    private ProfileResponse userProfile;

    @Schema(description = "Horoscope details")
    private HoroscopeResponse horoscopeDetails;

    @Schema(description = "Education and profession information")
    private EducationAndProfessionResponse educationAndProfession;

    @Schema(description = "Family background information")
    private FamilyBackgroundResponse familyBackground;

    @Schema(description = "Partner preferences")
    private PartnerPreferenceResponse partnerPreference;

    @Schema(description = "Contact details")
    private ContactDetailsResponse contactDetails;

    @Schema(description = "Document information")
    private List<DocumentInfo> documents;

    @Schema(description = "Overall profile completion status", example = "true")
    private Boolean profileCompleted;

    @Schema(description = "Profile completion percentage", example = "85")
    private Integer completionPercentage;

    @Schema(description = "Profile completeness score (0-100)", example = "85")
    private Integer completenessScore;

    @Schema(description = "Profile quality rating", example = "EXCELLENT", 
            allowableValues = {"POOR", "FAIR", "GOOD", "VERY_GOOD", "EXCELLENT"})
    private String profileQuality;

    @Schema(description = "Missing sections count", example = "2")
    private Integer missingSectionsCount;

    @Schema(description = "List of missing section names")
    private List<String> missingSections;

    @Schema(description = "Profile strength indicators")
    private ProfileStrengthMetrics strengthMetrics;

    @Schema(description = "Profile visibility status", example = "PUBLIC",
            allowableValues = {"PRIVATE", "MEMBERS_ONLY", "PUBLIC"})
    private String profileVisibility;

    @Schema(description = "Profile verification status", example = "VERIFIED",
            allowableValues = {"UNVERIFIED", "PENDING", "VERIFIED", "REJECTED"})
    private String verificationStatus;

    @Schema(description = "Last profile update timestamp")
    private LocalDateTime lastUpdated;

    @Schema(description = "Profile creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Profile version for optimistic locking", example = "5")
    private Integer version;

    @Schema(description = "Profile photo as base64 encoded string")
    private String profilePhotoBase64;

    @Schema(description = "Profile photo content type", example = "image/jpeg")
    private String profilePhotoContentType;

    @Schema(description = "Whether user has a profile photo", example = "true")
    private Boolean hasProfilePhoto;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Document information")
    public static class DocumentInfo {
        @Schema(description = "Document ID", example = "1")
        private Integer documentId;

        @Schema(description = "Document type", example = "PROFILE_PHOTO")
        private String documentType;

        @Schema(description = "Document name", example = "profile_photo.jpg")
        private String documentName;

        @Schema(description = "Document verification status", example = "VERIFIED")
        private String verificationStatus;

        @Schema(description = "Document upload timestamp")
        private LocalDateTime uploadedAt;
    }

    /**
     * Nested class for profile strength metrics
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Profile strength and quality metrics")
    public static class ProfileStrengthMetrics {
        @Schema(description = "Basic information completeness (0-100)", example = "95")
        private Integer basicInfoScore;

        @Schema(description = "Contact information completeness (0-100)", example = "80")
        private Integer contactInfoScore;

        @Schema(description = "Personal details completeness (0-100)", example = "90")
        private Integer personalDetailsScore;

        @Schema(description = "Family information completeness (0-100)", example = "75")
        private Integer familyInfoScore;

        @Schema(description = "Professional information completeness (0-100)", example = "85")
        private Integer professionalInfoScore;

        @Schema(description = "Partner preferences completeness (0-100)", example = "70")
        private Integer preferencesScore;

        @Schema(description = "Document verification completeness (0-100)", example = "60")
        private Integer documentScore;

        @Schema(description = "Profile photo availability", example = "true")
        private Boolean hasProfilePhoto;

        @Schema(description = "Mobile number verification status", example = "true")
        private Boolean mobileVerified;

        @Schema(description = "Email address verification status", example = "false")
        private Boolean emailVerified;

        @Schema(description = "Identity document verification status", example = "true")
        private Boolean identityVerified;
    }
}