package com.spring.jwt.subscription.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for updating a subscription plan (Admin).
 * All fields are optional for partial updates.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Update Subscription Plan Request")
public class UpdatePlanRequest {

    @Size(min = 3, max = 100, message = "Plan name must be between 3 and 100 characters")
    @Schema(description = "Plan name", example = "Premium Plus")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Schema(description = "Plan description")
    private String description;

    // Pricing
    @DecimalMin(value = "0.00", message = "Monthly price must be non-negative")
    @Schema(description = "Monthly price", example = "49.00")
    private BigDecimal monthlyPrice;

    @DecimalMin(value = "0.00", message = "Yearly price must be non-negative")
    @Schema(description = "Yearly price", example = "588.00")
    private BigDecimal yearlyPrice;

    @Min(value = 0, message = "Discount percentage must be between 0 and 100")
    @Max(value = 100, message = "Discount percentage must be between 0 and 100")
    @Schema(description = "Discount percentage", example = "25")
    private Integer discountPercentage;

    // Credits and Limits
    @Schema(description = "Total credits")
    private Integer totalCredits;

    @Schema(description = "Daily credit limit")
    private Integer dailyCreditLimit;

    @Schema(description = "Monthly credit limit")
    private Integer monthlyCreditLimit;

    @Schema(description = "Duration in months")
    private Integer durationMonths;

    @Schema(description = "Validity in days")
    private Integer validityDays;

    // Usage Limits
    @Schema(description = "Maximum profile views")
    private Integer maxProfileViews;

    @Schema(description = "Maximum contact reveals")
    private Integer maxContactReveals;

    @Schema(description = "Maximum interests per month")
    private Integer maxInterestsPerMonth;

    // Feature Flags
    @Schema(description = "Unlimited profile views")
    private Boolean unlimitedProfileViews;

    @Schema(description = "Unlimited contact reveals")
    private Boolean unlimitedContactReveals;

    @Schema(description = "Priority support")
    private Boolean prioritySupport;

    @Schema(description = "Video calling")
    private Boolean videoCalling;

    @Schema(description = "Advanced search")
    private Boolean advancedSearch;

    @Schema(description = "Profile highlighting")
    private Boolean profileHighlighting;

    @Schema(description = "Matchmaker assistance")
    private Boolean matchmakerAssistance;

    @Schema(description = "Horoscope matching")
    private Boolean horoscopeMatching;

    @Schema(description = "Verified badge")
    private Boolean verifiedBadge;

    @Schema(description = "Privacy controls")
    private Boolean privacyControls;

    @Schema(description = "Chat messaging")
    private Boolean chatMessaging;

    @Schema(description = "Photo gallery")
    private Boolean photoGallery;

    @Schema(description = "Background verification")
    private Boolean backgroundVerification;

    @Schema(description = "Exclusive profiles")
    private Boolean exclusiveProfiles;

    @Schema(description = "VIP concierge")
    private Boolean vipConcierge;

    @Schema(description = "Professional photography")
    private Boolean professionalPhotography;

    // Display
    @Schema(description = "Mark as popular")
    private Boolean isPopular;

    @Schema(description = "Mark as recommended")
    private Boolean isRecommended;

    @Schema(description = "Display order")
    private Integer displayOrder;

    @Schema(description = "Plan status")
    @Pattern(regexp = "ACTIVE|INACTIVE", message = "Status must be ACTIVE or INACTIVE")
    private String status;
}
