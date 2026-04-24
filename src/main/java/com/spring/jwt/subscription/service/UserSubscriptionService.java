package com.spring.jwt.subscription.service;

import com.spring.jwt.subscription.dto.AdminPurchaseSubscriptionRequest;
import com.spring.jwt.subscription.dto.PurchaseSubscriptionRequest;
import com.spring.jwt.subscription.dto.SubscriptionReceiptDTO;
import com.spring.jwt.subscription.dto.UserCreditsDTO;
import com.spring.jwt.subscription.dto.UserSubscriptionDTO;

import java.util.List;

/**
 * Service interface for User Subscription operations.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
public interface UserSubscriptionService {

    // ========== USER APIs ==========
    
    /**
     * Purchase a subscription
     */
    UserSubscriptionDTO purchaseSubscription(Integer userId, PurchaseSubscriptionRequest request);

    /**
     * Admin purchase subscription for user (Offline Payment)
     * Returns subscription details with receipt
     */
    SubscriptionReceiptDTO adminPurchaseSubscription(AdminPurchaseSubscriptionRequest request, String adminEmail);

    /**
     * Get current active subscription for user
     */
    UserSubscriptionDTO getCurrentSubscription(Integer userId);

    /**
     * Check if user has active subscription
     */
    boolean hasActiveSubscription(Integer userId);

    /**
     * Check if user has specific feature access
     */
    boolean hasFeatureAccess(Integer userId, String featureName);

    /**
     * Cancel subscription
     */
    void cancelSubscription(Integer userId);

    /**
     * Get subscription history for user
     */
    List<UserSubscriptionDTO> getSubscriptionHistory(Integer userId);

    // ========== CREDIT MANAGEMENT ==========
    
    /**
     * Deduct credits from user subscription
     */
    boolean deductCredits(Integer userId, int amount, String reason);

    /**
     * Add credits to user subscription
     */
    void addCredits(Integer userId, int credits);

    /**
     * Check if user can perform action (has credits/limits)
     */
    boolean canPerformAction(Integer userId, String actionType);

    /**
     * Get remaining credits for user
     */
    int getRemainingCredits(Integer userId);

    // ========== ADMIN APIs ==========
    
    /**
     * Get all subscriptions (paginated)
     */
    List<UserSubscriptionDTO> getAllSubscriptions();

    /**
     * Extend subscription by days
     */
    UserSubscriptionDTO extendSubscription(Integer userId, int days);

    /**
     * Suspend subscription
     */
    void suspendSubscription(Integer userId);

    /**
     * Reactivate subscription
     */
    UserSubscriptionDTO reactivateSubscription(Integer userId);

    /**
     * Get subscription statistics
     */
    SubscriptionStatistics getStatistics();
    
    /**
     * Get user's credit information and usage statistics
     * Shows remaining credits, usage breakdown, and warnings
     */
    UserCreditsDTO getUserCredits(Integer userId);
    
    /**
     * Check if user can perform a specific action (with detailed check)
     * Returns true only if user has subscription AND sufficient credits
     */
    boolean canPerformActionWithCheck(Integer userId, String actionType);
    
    /**
     * Record profile view and deduct credits
     * Returns true if view was recorded successfully
     */
    boolean recordProfileView(Integer viewerId, Integer viewedProfileId);

    /**
     * Nested record for statistics
     */
    record SubscriptionStatistics(
            long totalSubscriptions,
            long activeSubscriptions,
            long expiredSubscriptions,
            long cancelledSubscriptions,
            long suspendedSubscriptions
    ) {}
}
