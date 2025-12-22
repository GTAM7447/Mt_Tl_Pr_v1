package com.spring.jwt.PartnerPreference.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for partner preferences.
 * Contains all fields including audit information and computed fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Partner preferences response")
public class PartnerPreferenceResponse {

    @Schema(description = "Unique identifier", example = "1")
    private Integer partnerPreferenceId;

    @Schema(description = "Preferred age range", example = "25-30")
    private String ageRange;

    @Schema(description = "What you're looking for", example = "Life Partner")
    private String lookingFor;

    @Schema(description = "Preferred height range", example = "5'4\" - 5'8\"")
    private String heightRange;

    @Schema(description = "Preferred eating habits", example = "Vegetarian")
    private String eatingHabits;

    @Schema(description = "Preferred drinking habits", example = "Non-drinker")
    private String drinkingHabits;

    @Schema(description = "Preferred smoking habits", example = "Non-smoker")
    private String smokingHabits;

    @Schema(description = "Preferred country of residence", example = "India")
    private String countryLivingIn;

    @Schema(description = "Preferred city of residence", example = "Bangalore")
    private String cityLivingIn;

    @Schema(description = "Preferred state of residence", example = "Karnataka")
    private String stateLivingIn;

    @Schema(description = "Preferred complexion", example = "Fair")
    private String complexion;

    @Schema(description = "Preferred religion", example = "Hindu")
    private String religion;

    @Schema(description = "Preferred caste", example = "Brahmin")
    private String caste;

    @Schema(description = "Preferred sub caste", example = "Iyer")
    private String subCaste;

    @Schema(description = "Preferred education level", example = "Graduate")
    private String education;

    @Schema(description = "Mangal preference", example = "true")
    private Boolean mangal;

    @Schema(description = "Preferred resident status", example = "Citizen")
    private String residentStatus;

    @Schema(description = "Preferred partner occupation", example = "Software Engineer")
    private String partnerOccupation;

    @Schema(description = "Preferred minimum partner income", example = "800000")
    private Integer partnerIncome;

    @Schema(description = "Preferred marital status", example = "Never Married")
    private String maritalStatus;

    @Schema(description = "Preferred mother tongue", example = "Tamil")
    private String motherTongue;

    @Schema(description = "Additional preferences and requirements")
    private String additionalPreferences;

    @Schema(description = "Associated user ID", example = "123")
    private Integer userId;

    @Schema(description = "Version for optimistic locking", example = "1")
    private Integer version;

    // Audit fields
    @Schema(description = "Creation timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(description = "User who created this record")
    private Integer createdBy;

    @Schema(description = "User who last updated this record")
    private Integer updatedBy;

    @Schema(description = "Formatted income display", example = "₹8,00,000+")
    private String formattedIncome;

    @Schema(description = "Age range category", example = "Young Adult")
    private String ageCategory;

    @Schema(description = "Preference completeness percentage", example = "85")
    private Integer completenessPercentage;

    public String getAgeCategory() {
        if (ageRange == null || ageRange.trim().isEmpty()) {
            return "Not specified";
        }
        
        try {
            String[] parts = ageRange.split("-");
            if (parts.length == 2) {
                int minAge = Integer.parseInt(parts[0].trim());
                if (minAge <= 25) {
                    return "Young Adult";
                } else if (minAge <= 35) {
                    return "Adult";
                } else if (minAge <= 45) {
                    return "Mature Adult";
                } else {
                    return "Senior";
                }
            }
        } catch (NumberFormatException e) {
        }
        
        return "Not specified";
    }

    public String getFormattedIncome() {
        if (partnerIncome == null) {
            return "Not specified";
        }
        
        if (partnerIncome >= 10000000) {
            return String.format("₹%.1f Cr+", partnerIncome / 10000000.0);
        } else if (partnerIncome >= 100000) {
            return String.format("₹%.1f L+", partnerIncome / 100000.0);
        } else {
            return String.format("₹%,d+", partnerIncome);
        }
    }

    /**
     * Calculate preference completeness percentage
     */
    public Integer getCompletenessPercentage() {
        int totalFields = 21; // Total number of preference fields
        int filledFields = 0;
        
        if (ageRange != null && !ageRange.trim().isEmpty()) filledFields++;
        if (lookingFor != null && !lookingFor.trim().isEmpty()) filledFields++;
        if (heightRange != null && !heightRange.trim().isEmpty()) filledFields++;
        if (eatingHabits != null && !eatingHabits.trim().isEmpty()) filledFields++;
        if (drinkingHabits != null && !drinkingHabits.trim().isEmpty()) filledFields++;
        if (smokingHabits != null && !smokingHabits.trim().isEmpty()) filledFields++;
        if (countryLivingIn != null && !countryLivingIn.trim().isEmpty()) filledFields++;
        if (cityLivingIn != null && !cityLivingIn.trim().isEmpty()) filledFields++;
        if (stateLivingIn != null && !stateLivingIn.trim().isEmpty()) filledFields++;
        if (complexion != null && !complexion.trim().isEmpty()) filledFields++;
        if (religion != null && !religion.trim().isEmpty()) filledFields++;
        if (caste != null && !caste.trim().isEmpty()) filledFields++;
        if (subCaste != null && !subCaste.trim().isEmpty()) filledFields++;
        if (education != null && !education.trim().isEmpty()) filledFields++;
        if (mangal != null) filledFields++;
        if (residentStatus != null && !residentStatus.trim().isEmpty()) filledFields++;
        if (partnerOccupation != null && !partnerOccupation.trim().isEmpty()) filledFields++;
        if (partnerIncome != null) filledFields++;
        if (maritalStatus != null && !maritalStatus.trim().isEmpty()) filledFields++;
        if (motherTongue != null && !motherTongue.trim().isEmpty()) filledFields++;
        if (additionalPreferences != null && !additionalPreferences.trim().isEmpty()) filledFields++;
        
        return Math.round((filledFields * 100.0f) / totalFields);
    }
}