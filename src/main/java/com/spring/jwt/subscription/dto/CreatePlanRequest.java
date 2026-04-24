package com.spring.jwt.subscription.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for creating a subscription plan (Admin).
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create Subscription Plan Request")
public class CreatePlanRequest {

    @NotBlank(message = "Plan name is required")
    @Size(min = 3, max = 100, message = "Plan name must be between 3 and 100 characters")
    @Schema(description = "Plan name", example = "Premium", required = true)
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Schema(description = "Plan description", example = "Best value for serious seekers")
    private String description;

    @NotBlank(message = "Plan code is required")
    @Pattern(regexp = "^[A-Z_]+$", message = "Plan code must be uppercase letters and underscores only")
    @Schema(description = "Plan code", example = "PREMIUM", required = true)
    private String planCode;

    // Pricing
    @NotNull(message = "Monthly price is required")
    @DecimalMin(value = "0.00", message = "Monthly price must be non-negative")
    @Schema(description = "Monthly price", example = "39.00", required = true)
    private BigDecimal monthlyPrice;

    @NotNull(message = "Yearly price is required")
    @DecimalMin(value = "0.00", message = "Yearly price must be non-negative")
    @Schema(description = "Yearly price", example = "468.00", required = true)
    private BigDecimal yearlyPrice;

    @Min(value = 0, message = "Discount percentage must be between 0 and 100")
    @Max(value = 100, message = "Discount percentage must be between 0 and 100")
    @Schema(description = "Discount percentage for yearly plan", example = "20")
    private Integer discountPercentage;

    // Credits and Limits
    @Schema(description = "Total credits allocated", example = "500")
    private Integer totalCredits;

    @Schema(description = "Daily credit limit", example = "25")
    private Integer dailyCreditLimit;

    @Schema(description = "Monthly credit limit", example = "500")
    private Integer monthlyCreditLimit;

    @Schema(description = "Duration in months", example = "1")
    private Integer durationMonths;

    @Schema(description = "Validity in days", example = "30")
    private Integer validityDays;

    // Usage Limits
    @Schema(description = "Maximum profile views per month", example = "100")
    private Integer maxProfileViews;

    @Schema(description = "Maximum contact reveals per month", example = "20")
    private Integer maxContactReveals;

    @Schema(description = "Maximum interests per month", example = "50")
    private Integer maxInterestsPerMonth;

    // Feature Flags
    @Schema(description = "Unlimited profile views", example = "true")
    @Builder.Default
    private Boolean unlimitedProfileViews = false;

    @Schema(description = "Unlimited contact reveals", example = "false")
    @Builder.Default
    private Boolean unlimitedContactReveals = false;

    @Schema(description = "Priority support", example = "true")
    @Builder.Default
    private Boolean prioritySupport = false;

    @Schema(description = "Video calling feature", example = "true")
    @Builder.Default
    private Boolean videoCalling = false;

    @Schema(description = "Advanced search filters", example = "true")
    @Builder.Default
    private Boolean advancedSearch = false;

    @Schema(description = "Profile highlighting", example = "true")
    @Builder.Default
    private Boolean profileHighlighting = false;

    @Schema(description = "Personal matchmaker assistance", example = "false")
    @Builder.Default
    private Boolean matchmakerAssistance = false;

    @Schema(description = "Horoscope matching", example = "true")
    @Builder.Default
    private Boolean horoscopeMatching = false;

    @Schema(description = "Verified badge", example = "true")
    @Builder.Default
    private Boolean verifiedBadge = false;

    @Schema(description = "Privacy controls", example = "true")
    @Builder.Default
    private Boolean privacyControls = false;

    @Schema(description = "Chat messaging", example = "true")
    @Builder.Default
    private Boolean chatMessaging = false;

    @Schema(description = "Photo gallery", example = "true")
    @Builder.Default
    private Boolean photoGallery = false;

    @Schema(description = "Background verification", example = "false")
    @Builder.Default
    private Boolean backgroundVerification = false;

    @Schema(description = "Exclusive profiles access", example = "false")
    @Builder.Default
    private Boolean exclusiveProfiles = false;

    @Schema(description = "VIP concierge service", example = "false")
    @Builder.Default
    private Boolean vipConcierge = false;

    @Schema(description = "Professional photography", example = "false")
    @Builder.Default
    private Boolean professionalPhotography = false;

    // Display
    @Schema(description = "Mark as popular", example = "true")
    @Builder.Default
    private Boolean isPopular = false;

    @Schema(description = "Mark as recommended", example = "true")
    @Builder.Default
    private Boolean isRecommended = false;

    @Schema(description = "Display order", example = "2")
    private Integer displayOrder;
}
