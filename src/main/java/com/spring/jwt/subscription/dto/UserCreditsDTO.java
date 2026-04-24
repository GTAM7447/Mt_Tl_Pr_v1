package com.spring.jwt.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for user's credit information and usage statistics.
 * Shows user how many credits they have and how they're being used.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreditsDTO {

    private Boolean hasActiveSubscription;
    private String subscriptionPlanName;
    private String subscriptionPlanCode;
    private LocalDateTime subscriptionStartDate;
    private LocalDateTime subscriptionEndDate;
    private String subscriptionStatus;

    private Integer allocatedCredits;
    private Integer usedCredits;
    private Integer remainingCredits;
    private Double creditUsagePercentage;

    private Integer dailyLimit;
    private Integer dailyCreditsUsed;
    private Integer dailyCreditsRemaining;
    private Boolean dailyLimitReached;

    private Integer monthlyLimit;
    private Integer monthlyCreditsUsed;
    private Integer monthlyCreditsRemaining;
    private Boolean monthlyLimitReached;

    private Integer profileViewsUsed;
    private Integer contactRevealsUsed;
    private Integer interestsSentUsed;

    private Boolean canViewProfiles;
    private Boolean canRevealContacts;
    private Boolean canSendInterests;
    private Boolean hasUnlimitedProfileViews;
    private Boolean hasUnlimitedContactReveals;

    private Integer profileViewCost;
    private Integer contactRevealCost;
    private Integer interestSendCost;

    private LocalDateTime lastCreditReset;
    private LocalDateTime nextCreditReset;

    private Boolean lowCreditsWarning;
    private Boolean criticalCreditsWarning;
    private String warningMessage;

    private String recommendation;
}
