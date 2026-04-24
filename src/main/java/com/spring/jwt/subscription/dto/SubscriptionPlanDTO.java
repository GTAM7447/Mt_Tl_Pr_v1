package com.spring.jwt.subscription.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for Subscription Plan with pricing and features.
 * Used for public API responses.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Subscription Plan Details")
public class SubscriptionPlanDTO {

    @Schema(description = "Plan ID", example = "1")
    private Integer subscriptionId;

    @Schema(description = "Plan name", example = "Premium")
    private String name;

    @Schema(description = "Plan description", example = "Best value for serious seekers")
    private String description;

    @Schema(description = "Plan code", example = "PREMIUM")
    private String planCode;

    @Schema(description = "Monthly pricing details")
    private PricingDTO monthly;

    @Schema(description = "Yearly pricing details")
    private PricingDTO yearly;

    @Schema(description = "List of features included in this plan")
    private List<FeatureDTO> features;

    @Schema(description = "Is this plan marked as popular", example = "true")
    private Boolean isPopular;

    @Schema(description = "Is this plan recommended", example = "true")
    private Boolean isRecommended;

    @Schema(description = "Display order", example = "2")
    private Integer displayOrder;

    @Schema(description = "Plan status", example = "ACTIVE")
    private String status;

    /**
     * Nested DTO for pricing information
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Pricing Information")
    public static class PricingDTO {
        
        @Schema(description = "Original price", example = "39.00")
        private BigDecimal price;

        @Schema(description = "Discounted price", example = "31.20")
        private BigDecimal discountedPrice;

        @Schema(description = "Discount percentage", example = "20")
        private Integer discountPercentage;

        @Schema(description = "Currency code", example = "INR")
        private String currency;

        @Schema(description = "Billing description", example = "Billed monthly")
        private String billedAs;

        @Schema(description = "Has discount", example = "true")
        private Boolean hasDiscount;
    }

    /**
     * Nested DTO for feature information
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Feature Information")
    public static class FeatureDTO {
        
        @Schema(description = "Feature name", example = "Profile Views")
        private String name;

        @Schema(description = "Feature description", example = "Unlimited profile views")
        private String description;

        @Schema(description = "Is feature included", example = "true")
        private Boolean included;

        @Schema(description = "Feature value/limit", example = "Unlimited")
        private String value;

        @Schema(description = "Feature icon/type", example = "eye")
        private String icon;
    }
}
