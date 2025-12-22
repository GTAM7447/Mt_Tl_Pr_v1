package com.spring.jwt.PartnerPreference.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating partner preferences.
 * Includes comprehensive validation rules and business constraints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create partner preferences")
public class PartnerPreferenceCreateRequest {

    @NotBlank(message = "Age range cannot be blank")
    @Size(max = 50, message = "Age range cannot exceed 50 characters")
    @Schema(description = "Preferred age range", example = "25-30", required = true)
    private String ageRange;

    @NotBlank(message = "Looking for cannot be blank")
    @Size(max = 100, message = "Looking for cannot exceed 100 characters")
    @Schema(description = "What you're looking for", example = "Life Partner", required = true)
    private String lookingFor;

    @NotBlank(message = "Height range cannot be blank")
    @Size(max = 50, message = "Height range cannot exceed 50 characters")
    @Schema(description = "Preferred height range", example = "5'4\" - 5'8\"", required = true)
    private String heightRange;

    @Size(max = 50, message = "Eating habits cannot exceed 50 characters")
    @Schema(description = "Preferred eating habits", example = "Vegetarian")
    private String eatingHabits;

    @Size(max = 50, message = "Drinking habits cannot exceed 50 characters")
    @Schema(description = "Preferred drinking habits", example = "Non-drinker")
    private String drinkingHabits;

    @Size(max = 50, message = "Smoking habits cannot exceed 50 characters")
    @Schema(description = "Preferred smoking habits", example = "Non-smoker")
    private String smokingHabits;

    @Size(max = 100, message = "Country cannot exceed 100 characters")
    @Schema(description = "Preferred country of residence", example = "India")
    private String countryLivingIn;

    @Size(max = 100, message = "City cannot exceed 100 characters")
    @Schema(description = "Preferred city of residence", example = "Bangalore")
    private String cityLivingIn;

    @Size(max = 100, message = "State cannot exceed 100 characters")
    @Schema(description = "Preferred state of residence", example = "Karnataka")
    private String stateLivingIn;

    @Size(max = 50, message = "Complexion cannot exceed 50 characters")
    @Schema(description = "Preferred complexion", example = "Fair")
    private String complexion;

    @Size(max = 50, message = "Religion cannot exceed 50 characters")
    @Schema(description = "Preferred religion", example = "Hindu")
    private String religion;

    @Size(max = 50, message = "Caste cannot exceed 50 characters")
    @Schema(description = "Preferred caste", example = "Brahmin")
    private String caste;

    @Size(max = 100, message = "Sub caste cannot exceed 100 characters")
    @Schema(description = "Preferred sub caste", example = "Iyer")
    private String subCaste;

    @Size(max = 100, message = "Education cannot exceed 100 characters")
    @Schema(description = "Preferred education level", example = "Graduate")
    private String education;

    @Schema(description = "Mangal preference", example = "true")
    private Boolean mangal;

    @Size(max = 50, message = "Resident status cannot exceed 50 characters")
    @Schema(description = "Preferred resident status", example = "Citizen")
    private String residentStatus;

    @Size(max = 100, message = "Partner occupation cannot exceed 100 characters")
    @Schema(description = "Preferred partner occupation", example = "Software Engineer")
    private String partnerOccupation;

    @Positive(message = "Partner income must be positive")
    @Max(value = 100000000, message = "Partner income cannot exceed 100,000,000")
    @Schema(description = "Preferred minimum partner income", example = "800000")
    private Integer partnerIncome;

    @Size(max = 50, message = "Marital status cannot exceed 50 characters")
    @Schema(description = "Preferred marital status", example = "Never Married")
    private String maritalStatus;

    @Size(max = 50, message = "Mother tongue cannot exceed 50 characters")
    @Schema(description = "Preferred mother tongue", example = "Tamil")
    private String motherTongue;

    @Size(max = 1000, message = "Additional preferences cannot exceed 1000 characters")
    @Schema(description = "Additional preferences and requirements")
    private String additionalPreferences;

    /**
     * Business rule validation: Age range format validation
     */
    @AssertTrue(message = "Age range should be in format 'min-max' (e.g., '25-30')")
    public boolean isAgeRangeValid() {
        if (ageRange == null || ageRange.trim().isEmpty()) {
            return true; 
        }
        
        String[] parts = ageRange.split("-");
        if (parts.length != 2) {
            return false;
        }
        
        try {
            int minAge = Integer.parseInt(parts[0].trim());
            int maxAge = Integer.parseInt(parts[1].trim());
            return minAge >= 18 && maxAge <= 80 && minAge <= maxAge;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Business rule validation: Height range format validation
     */
    @AssertTrue(message = "Height range should be in valid format")
    public boolean isHeightRangeValid() {
        if (heightRange == null || heightRange.trim().isEmpty()) {
            return true; 
        }
        
        return heightRange.length() >= 3 && heightRange.length() <= 50;
    }

    /**
     * Business rule validation: Income should be reasonable if specified
     */
    @AssertTrue(message = "Partner income seems unrealistic")
    public boolean isPartnerIncomeRealistic() {
        if (partnerIncome == null) {
            return true;
        }
        
        return partnerIncome >= 100000 && partnerIncome <= 50000000;
    }
}