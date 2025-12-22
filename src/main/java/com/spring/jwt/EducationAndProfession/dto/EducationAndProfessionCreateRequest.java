package com.spring.jwt.EducationAndProfession.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating education and profession information.
 * Includes comprehensive validation rules and business constraints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create education and profession information")
public class EducationAndProfessionCreateRequest {

    @NotBlank(message = "Education field cannot be blank")
    @Size(max = 100, message = "Education cannot exceed 100 characters")
    @Schema(description = "Educational qualification", example = "Bachelor's Degree", required = true)
    private String education;

    @NotBlank(message = "Degree cannot be blank")
    @Size(max = 100, message = "Degree cannot exceed 100 characters")
    @Schema(description = "Degree or certification", example = "Computer Science Engineering", required = true)
    private String degree;

    @NotBlank(message = "Occupation cannot be blank")
    @Size(max = 100, message = "Occupation cannot exceed 100 characters")
    @Schema(description = "Current occupation", example = "Software Engineer", required = true)
    private String occupation;

    @Size(max = 500, message = "Occupation details cannot exceed 500 characters")
    @Schema(description = "Detailed description of occupation", example = "Full-stack developer working on web applications")
    private String occupationDetails;

    @NotNull(message = "Income per year is required")
    @Positive(message = "Income per year must be positive")
    @Max(value = 100000000, message = "Income per year cannot exceed 100,000,000")
    @Schema(description = "Annual income in local currency", example = "1200000", required = true)
    private Integer incomePerYear;

    @Size(max = 1000, message = "Additional details cannot exceed 1000 characters")
    @Schema(description = "Additional education and profession details")
    private String additionalDetails;

    @Size(max = 100, message = "Work location cannot exceed 100 characters")
    @Schema(description = "Current work location", example = "Bangalore, India")
    private String workLocation;

    @Size(max = 200, message = "Company name cannot exceed 200 characters")
    @Schema(description = "Current company name", example = "Tech Solutions Pvt Ltd")
    private String companyName;

    @Min(value = 0, message = "Experience years cannot be negative")
    @Max(value = 50, message = "Experience years cannot exceed 50")
    @Schema(description = "Years of professional experience", example = "5")
    private Integer experienceYears;


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
}