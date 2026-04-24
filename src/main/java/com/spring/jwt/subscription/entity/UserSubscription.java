package com.spring.jwt.subscription.entity;

import com.spring.jwt.entity.User;
import com.spring.jwt.entity.base.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a user's active subscription.
 * Tracks credits, usage, and subscription lifecycle.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Entity
@Table(name = "user_subscriptions", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_subscription_status", columnList = "subscription_status"),
    @Index(name = "idx_end_date", columnList = "end_date"),
    @Index(name = "idx_user_status", columnList = "user_id, subscription_status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSubscription extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_subscription_id")
    private Integer userSubscriptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_subscription_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_plan_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_subscription_plan"))
    private SubscriptionPlan subscriptionPlan;

    // ========== SUBSCRIPTION LIFECYCLE ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status", nullable = false, length = 20)
    @Builder.Default
    private SubscriptionStatus subscriptionStatus = SubscriptionStatus.ACTIVE;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "auto_renewal")
    @Builder.Default
    private Boolean autoRenewal = false;

    @Column(name = "billing_period", length = 20)
    private String billingPeriod; // MONTHLY, YEARLY

    // ========== CREDIT MANAGEMENT ==========
    @Column(name = "allocated_credits")
    @Builder.Default
    private Integer allocatedCredits = 0;

    @Column(name = "used_credits")
    @Builder.Default
    private Integer usedCredits = 0;

    @Column(name = "remaining_credits")
    @Builder.Default
    private Integer remainingCredits = 0;

    @Column(name = "daily_credits_used")
    @Builder.Default
    private Integer dailyCreditsUsed = 0;

    @Column(name = "monthly_credits_used")
    @Builder.Default
    private Integer monthlyCreditsUsed = 0;

    @Column(name = "last_credit_reset")
    private LocalDateTime lastCreditReset;

    // ========== USAGE TRACKING ==========
    @Column(name = "profile_views_used")
    @Builder.Default
    private Integer profileViewsUsed = 0;

    @Column(name = "contact_reveals_used")
    @Builder.Default
    private Integer contactRevealsUsed = 0;

    @Column(name = "interests_sent_used")
    @Builder.Default
    private Integer interestsSentUsed = 0;

    // ========== PAYMENT INFORMATION ==========
    @Column(name = "amount_paid", precision = 10, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    // ========== ENUMS ==========
    public enum SubscriptionStatus {
        ACTIVE,
        EXPIRED,
        CANCELLED,
        SUSPENDED,
        PENDING
    }

    // ========== BUSINESS METHODS ==========

    /**
     * Check if subscription is currently active
     */
    public boolean isActive() {
        if (subscriptionStatus != SubscriptionStatus.ACTIVE) {
            return false;
        }
        if (endDate == null) {
            return true;
        }
        return LocalDateTime.now().isBefore(endDate);
    }

    /**
     * Check if subscription has expired
     */
    public boolean isExpired() {
        if (endDate == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(endDate);
    }

    /**
     * Deduct credits from subscription
     */
    public boolean deductCredits(int amount) {
        if (remainingCredits < amount) {
            return false;
        }
        this.usedCredits += amount;
        this.remainingCredits -= amount;
        this.dailyCreditsUsed += amount;
        this.monthlyCreditsUsed += amount;
        return true;
    }

    /**
     * Add credits to subscription
     */
    public void addCredits(int amount) {
        this.allocatedCredits += amount;
        this.remainingCredits += amount;
    }

    /**
     * Reset daily credits
     */
    public void resetDailyCredits() {
        this.dailyCreditsUsed = 0;
        this.lastCreditReset = LocalDateTime.now();
    }

    /**
     * Reset monthly credits and usage
     */
    public void resetMonthlyCredits() {
        this.monthlyCreditsUsed = 0;
        this.profileViewsUsed = 0;
        this.contactRevealsUsed = 0;
        this.interestsSentUsed = 0;
    }

    /**
     * Check if daily limit reached
     */
    public boolean isDailyLimitReached() {
        if (subscriptionPlan == null || subscriptionPlan.getDailyCreditLimit() == null) {
            return false;
        }
        return dailyCreditsUsed >= subscriptionPlan.getDailyCreditLimit();
    }

    /**
     * Check if monthly limit reached
     */
    public boolean isMonthlyLimitReached() {
        if (subscriptionPlan == null || subscriptionPlan.getMonthlyCreditLimit() == null) {
            return false;
        }
        return monthlyCreditsUsed >= subscriptionPlan.getMonthlyCreditLimit();
    }

    /**
     * Increment profile view count
     */
    public void incrementProfileView() {
        this.profileViewsUsed++;
    }

    /**
     * Increment contact reveal count
     */
    public void incrementContactReveal() {
        this.contactRevealsUsed++;
    }

    /**
     * Increment interest sent count
     */
    public void incrementInterestSent() {
        this.interestsSentUsed++;
    }

    /**
     * Extend subscription by days
     */
    public void extendSubscription(int days) {
        if (endDate == null) {
            endDate = LocalDateTime.now().plusDays(days);
        } else {
            endDate = endDate.plusDays(days);
        }
    }

    /**
     * Cancel subscription
     */
    public void cancel() {
        this.subscriptionStatus = SubscriptionStatus.CANCELLED;
        this.autoRenewal = false;
    }

    /**
     * Suspend subscription
     */
    public void suspend() {
        this.subscriptionStatus = SubscriptionStatus.SUSPENDED;
    }

    /**
     * Reactivate subscription
     */
    public void reactivate() {
        this.subscriptionStatus = SubscriptionStatus.ACTIVE;
    }

    /**
     * Mark as expired
     */
    public void markExpired() {
        this.subscriptionStatus = SubscriptionStatus.EXPIRED;
    }
}
