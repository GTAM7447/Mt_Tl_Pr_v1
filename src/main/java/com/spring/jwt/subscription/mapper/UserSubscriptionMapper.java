package com.spring.jwt.subscription.mapper;

import com.spring.jwt.subscription.dto.UserSubscriptionDTO;
import com.spring.jwt.subscription.entity.UserSubscription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Mapper for UserSubscription entity and DTOs.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class UserSubscriptionMapper {

    private final SubscriptionPlanMapper planMapper;

    /**
     * Convert entity to DTO
     */
    public UserSubscriptionDTO toDTO(UserSubscription entity) {
        if (entity == null) {
            return null;
        }

        Long daysRemaining = null;
        if (entity.getEndDate() != null) {
            daysRemaining = ChronoUnit.DAYS.between(LocalDateTime.now(), entity.getEndDate());
            if (daysRemaining < 0) daysRemaining = 0L;
        }

        return UserSubscriptionDTO.builder()
                .userSubscriptionId(entity.getUserSubscriptionId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .subscriptionPlan(planMapper.toDTO(entity.getSubscriptionPlan()))
                .subscriptionStatus(entity.getSubscriptionStatus().name())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .autoRenewal(entity.getAutoRenewal())
                .billingPeriod(entity.getBillingPeriod())
                .allocatedCredits(entity.getAllocatedCredits())
                .usedCredits(entity.getUsedCredits())
                .remainingCredits(entity.getRemainingCredits())
                .dailyCreditsUsed(entity.getDailyCreditsUsed())
                .monthlyCreditsUsed(entity.getMonthlyCreditsUsed())
                .profileViewsUsed(entity.getProfileViewsUsed())
                .contactRevealsUsed(entity.getContactRevealsUsed())
                .interestsSentUsed(entity.getInterestsSentUsed())
                .amountPaid(entity.getAmountPaid())
                .paymentMethod(entity.getPaymentMethod())
                .transactionId(entity.getTransactionId())
                .paymentDate(entity.getPaymentDate())
                .daysRemaining(daysRemaining)
                .isActive(entity.isActive())
                .isExpired(entity.isExpired())
                .build();
    }

    /**
     * Convert list of entities to DTOs
     */
    public List<UserSubscriptionDTO> toDTOList(List<UserSubscription> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toDTO)
                .toList();
    }
}
