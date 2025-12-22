package com.spring.jwt.CompleteProfile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for profile completeness analysis.
 * Provides detailed breakdown of missing profile sections and completion metrics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Profile completeness analysis with missing sections breakdown")
public class MissingProfileDTO {

    @Schema(description = "User profile section completion status", example = "true")
    private boolean userProfile;

    @Schema(description = "Horoscope details section completion status", example = "false")
    private boolean horoscopeDetails;

    @Schema(description = "Education and profession section completion status", example = "true")
    private boolean educationAndProfession;

    @Schema(description = "Family background section completion status", example = "false")
    private boolean familyBackground;

    @Schema(description = "Partner preferences section completion status", example = "true")
    private boolean partnerPreference;

    @Schema(description = "Contact details section completion status", example = "true")
    private boolean contactDetails;

    @Schema(description = "Documents section completion status", example = "false")
    private boolean document;

    @Schema(description = "Overall completion percentage (0-100)", example = "71")
    private Integer completionPercentage;

    @Schema(description = "Total number of profile sections", example = "7")
    private Integer totalSections;

    @Schema(description = "Number of completed sections", example = "5")
    private Integer completedSections;

    @Schema(description = "Number of missing sections", example = "2")
    private Integer missingSections;

    @Schema(description = "List of missing section names")
    private List<String> missingSectionNames;

    @Schema(description = "Priority sections that should be completed first")
    private List<String> prioritySections;

    @Schema(description = "Profile quality rating based on completeness", 
            example = "GOOD", 
            allowableValues = {"POOR", "FAIR", "GOOD", "VERY_GOOD", "EXCELLENT"})
    private String profileQuality;

    @Schema(description = "Estimated time to complete profile (in minutes)", example = "15")
    private Integer estimatedCompletionTime;

    @Schema(description = "Profile completeness recommendations")
    private List<CompletionRecommendation> recommendations;

    /**
     * Nested class for completion recommendations
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Profile completion recommendation")
    public static class CompletionRecommendation {
        @Schema(description = "Section name", example = "horoscopeDetails")
        private String section;

        @Schema(description = "Recommendation title", example = "Add Horoscope Information")
        private String title;

        @Schema(description = "Detailed recommendation", example = "Complete your horoscope details to improve profile visibility")
        private String description;

        @Schema(description = "Priority level", example = "HIGH", allowableValues = {"LOW", "MEDIUM", "HIGH", "CRITICAL"})
        private String priority;

        @Schema(description = "Estimated completion time in minutes", example = "5")
        private Integer estimatedMinutes;

        @Schema(description = "Impact on profile score (0-100)", example = "15")
        private Integer scoreImpact;
    }
}
