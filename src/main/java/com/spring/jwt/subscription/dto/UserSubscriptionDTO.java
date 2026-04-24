package com.spring.jwt.subscription.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for User Subscription details.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "User Subscription Details")
public class UserSubscriptionDTO {

    @Schema(description = "Subscription ID", example = "1")
    private Integer userSubscriptionId;

    @Schema(description = "User ID", example = "123")
    private Integer userId;

    @Schema(description = "Subscription plan details")
    private SubscriptionPlanDTO subscriptionPlan;

    @Schema(description = "Subscription status", example = "ACTIVE")
    private String subscriptionStatus;

    @Schema(description = "Start date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @Schema(description = "End date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    @Schema(description = "Auto renewal enabled", example = "false")
    private Boolean autoRenewal;

    @Schema(description = "Billing period", example = "MONTHLY")
    private String billingPeriod;

    @Schema(description = "Allocated credits", example = "500")
    private Integer allocatedCredits;

    @Schema(description = "Used credits", example = "150")
    private Integer usedCredits;

    @Schema(description = "Remaining credits", example = "350")
    private Integer remainingCredits;

    @Schema(description = "Daily credits used", example = "10")
    private Integer dailyCreditsUsed;

    @Schema(description = "Monthly credits used", example = "150")
    private Integer monthlyCreditsUsed;

    @Schema(description = "Profile views used", example = "45")
    private Integer profileViewsUsed;

    @Schema(description = "Contact reveals used", example = "5")
    private Integer contactRevealsUsed;

    @Schema(description = "Interests sent", example = "20")
    private Integer interestsSentUsed;

    @Schema(description = "Amount paid", example = "39.00")
    private BigDecimal amountPaid;

    @Schema(description = "Payment method", example = "CREDIT_CARD")
    private String paymentMethod;

    @Schema(description = "Transaction ID", example = "TXN123456789")
    private String transactionId;

    @Schema(description = "Payment date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentDate;

    @Schema(description = "Days remaining", example = "25")
    private Long daysRemaining;

    @Schema(description = "Is subscription active", example = "true")
    private Boolean isActive;

    @Schema(description = "Is subscription expired", example = "false")
    private Boolean isExpired;
}
