package com.spring.jwt.EducationAndProfession.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for education and profession information.
 * Contains all fields including audit information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Education and profession information response")
public class EducationAndProfessionResponse {

    @Schema(description = "Unique identifier", example = "1")
    private Integer educationId;

    @Schema(description = "Educational qualification", example = "Bachelor's Degree")
    private String education;

    @Schema(description = "Degree or certification", example = "Computer Science Engineering")
    private String degree;

    @Schema(description = "Current occupation", example = "Software Engineer")
    private String occupation;

    @Schema(description = "Detailed description of occupation")
    private String occupationDetails;

    @Schema(description = "Annual income in local currency", example = "1200000")
    private Integer incomePerYear;

    @Schema(description = "Additional education and profession details")
    private String additionalDetails;

    @Schema(description = "Current work location", example = "Bangalore, India")
    private String workLocation;

    @Schema(description = "Current company name", example = "Tech Solutions Pvt Ltd")
    private String companyName;

    @Schema(description = "Years of professional experience", example = "5")
    private Integer experienceYears;

    @Schema(description = "Associated user ID", example = "123")
    private Integer userId;

    @Schema(description = "Version for optimistic locking", example = "1")
    private Integer version;

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

    @Schema(description = "Formatted income display", example = "₹12,00,000")
    private String formattedIncome;

    @Schema(description = "Experience level category", example = "Mid-level")
    private String experienceLevel;

    @Schema(description = "Education level category", example = "Graduate")
    private String educationLevel;

    public String getExperienceLevel() {
        if (experienceYears == null) {
            return "Not specified";
        }
        
        if (experienceYears == 0) {
            return "Fresher";
        } else if (experienceYears <= 2) {
            return "Entry-level";
        } else if (experienceYears <= 5) {
            return "Mid-level";
        } else if (experienceYears <= 10) {
            return "Senior-level";
        } else {
            return "Expert-level";
        }
    }

    /**
     * Get education level category
     */
    public String getEducationLevel() {
        if (education == null) {
            return "Not specified";
        }
        
        String lowerEducation = education.toLowerCase();
        if (lowerEducation.contains("phd") || lowerEducation.contains("doctorate")) {
            return "Doctorate";
        } else if (lowerEducation.contains("master") || lowerEducation.contains("mba") || 
                   lowerEducation.contains("mtech") || lowerEducation.contains("mca")) {
            return "Post-graduate";
        } else if (lowerEducation.contains("bachelor") || lowerEducation.contains("btech") || 
                   lowerEducation.contains("bca") || lowerEducation.contains("degree")) {
            return "Graduate";
        } else if (lowerEducation.contains("diploma")) {
            return "Diploma";
        } else {
            return "Other";
        }
    }

    /**
     * Get formatted income for display
     */
    public String getFormattedIncome() {
        if (incomePerYear == null) {
            return "Not specified";
        }
        
        if (incomePerYear >= 10000000) {
            return String.format("₹%.1f Cr", incomePerYear / 10000000.0);
        } else if (incomePerYear >= 100000) {
            return String.format("₹%.1f L", incomePerYear / 100000.0);
        } else {
            return String.format("₹%,d", incomePerYear);
        }
    }
}