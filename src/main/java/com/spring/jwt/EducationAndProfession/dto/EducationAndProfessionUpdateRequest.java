package com.spring.jwt.EducationAndProfession.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating education and profession information.
 * Supports partial updates with optimistic locking.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update education and profession information")
public class EducationAndProfessionUpdateRequest {

    @NotNull(message = "Version is required for optimistic locking")
    @Schema(description = "Version for optimistic locking", example = "1", required = true)
    private Integer version;

    @Size(max = 100, message = "Education cannot exceed 100 characters")
    @Schema(description = "Educational qualification", example = "Master's Degree")
    private String education;

    @Size(max = 100, message = "Degree cannot exceed 100 characters")
    @Schema(description = "Degree or certification", example = "Computer Science Engineering")
    private String degree;

    @Size(max = 100, message = "Occupation cannot exceed 100 characters")
    @Schema(description = "Current occupation", example = "Senior Software Engineer")
    private String occupation;

    @Size(max = 500, message = "Occupation details cannot exceed 500 characters")
    @Schema(description = "Detailed description of occupation")
    private String occupationDetails;

    @Positive(message = "Income per year must be positive")
    @Max(value = 100000000, message = "Income per year cannot exceed 100,000,000")
    @Schema(description = "Annual income in local currency", example = "1500000")
    private Integer incomePerYear;

    @Size(max = 1000, message = "Additional details cannot exceed 1000 characters")
    @Schema(description = "Additional education and profession details")
    private String additionalDetails;

    @Size(max = 100, message = "Work location cannot exceed 100 characters")
    @Schema(description = "Current work location", example = "Mumbai, India")
    private String workLocation;

    @Size(max = 200, message = "Company name cannot exceed 200 characters")
    @Schema(description = "Current company name", example = "Global Tech Corp")
    private String companyName;

    @Min(value = 0, message = "Experience years cannot be negative")
    @Max(value = 50, message = "Experience years cannot exceed 50")
    @Schema(description = "Years of professional experience", example = "7")
    private Integer experienceYears;

    /**
     * Business rule validation: Experience should be reasonable for the income level
     */
    @AssertTrue(message = "Experience and income combination seems unrealistic")
    public boolean isExperienceIncomeValid() {
        if (experienceYears == null || incomePerYear == null) {
            return true;
        }

        if (incomePerYear > 5000000 && experienceYears < 2) {
            return false;
        }
        
        return true;
    }

    /**
     * Business rule validation: Occupation details should be provided for certain occupations
     */
    @AssertTrue(message = "Occupation details are required for this type of occupation")
    public boolean isOccupationDetailsValid() {
        if (occupation == null) {
            return true;
        }
        
        String lowerOccupation = occupation.toLowerCase();
        boolean requiresDetails = lowerOccupation.contains("engineer") || 
                                lowerOccupation.contains("manager") || 
                                lowerOccupation.contains("consultant") ||
                                lowerOccupation.contains("developer");
        
        if (requiresDetails && (occupationDetails == null || occupationDetails.trim().isEmpty())) {
            return false;
        }
        
        return true;
    }

    /**
     * Check if any field is provided for update
     */
    public boolean hasAnyFieldToUpdate() {
        return education != null || degree != null || occupation != null || 
               occupationDetails != null || incomePerYear != null || 
               additionalDetails != null || workLocation != null || 
               companyName != null || experienceYears != null;
    }
}