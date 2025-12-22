package com.spring.jwt.CompleteProfile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for profile analytics and reporting.
 * Used by admin endpoints for generating profile completion reports.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for profile analytics and reporting")
public class ProfileAnalyticsRequest {

    @Schema(description = "Start date for analytics period", example = "2024-01-01")
    private LocalDate startDate;

    @Schema(description = "End date for analytics period", example = "2024-12-31")
    private LocalDate endDate;

    @Schema(description = "Minimum completion percentage filter", example = "50")
    @Min(value = 0, message = "Minimum completion percentage must be at least 0")
    @Max(value = 100, message = "Minimum completion percentage must be at most 100")
    private Integer minCompletionPercentage;

    @Schema(description = "Maximum completion percentage filter", example = "100")
    @Min(value = 0, message = "Maximum completion percentage must be at least 0")
    @Max(value = 100, message = "Maximum completion percentage must be at most 100")
    private Integer maxCompletionPercentage;

    @Schema(description = "Profile quality filters", 
            example = "[\"GOOD\", \"VERY_GOOD\", \"EXCELLENT\"]")
    private List<String> profileQualities;

    @Schema(description = "Verification status filters", 
            example = "[\"VERIFIED\", \"PENDING\"]")
    private List<String> verificationStatuses;

    @Schema(description = "Include only profiles with missing sections", example = "false")
    private Boolean onlyIncomplete;

    @Schema(description = "Group results by time period", 
            example = "MONTHLY", 
            allowableValues = {"DAILY", "WEEKLY", "MONTHLY", "QUARTERLY", "YEARLY"})
    private String groupBy;

    @Schema(description = "Include detailed section-wise analytics", example = "true")
    private Boolean includeDetailedAnalytics;

    @Schema(description = "Include user demographic breakdown", example = "false")
    private Boolean includeDemographics;
}