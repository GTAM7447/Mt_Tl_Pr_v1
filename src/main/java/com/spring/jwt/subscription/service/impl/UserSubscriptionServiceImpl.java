package com.spring.jwt.subscription.service.impl;

import com.spring.jwt.entity.User;
import com.spring.jwt.exception.ResourceAlreadyExistsException;
import com.spring.jwt.exception.ResourceNotFoundException;
import com.spring.jwt.exception.SubscriptionRequiredException;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.subscription.dto.AdminPurchaseSubscriptionRequest;
import com.spring.jwt.subscription.dto.PurchaseSubscriptionRequest;
import com.spring.jwt.subscription.dto.SubscriptionReceiptDTO;
import com.spring.jwt.subscription.dto.UserCreditsDTO;
import com.spring.jwt.subscription.dto.UserSubscriptionDTO;
import com.spring.jwt.subscription.entity.SubscriptionPlan;
import com.spring.jwt.subscription.entity.UserSubscription;
import com.spring.jwt.subscription.mapper.UserSubscriptionMapper;
import com.spring.jwt.subscription.repository.SubscriptionPlanRepository;
import com.spring.jwt.subscription.repository.UserSubscriptionRepository;
import com.spring.jwt.subscription.service.SubscriptionReceiptService;
import com.spring.jwt.subscription.service.TransactionIdGenerator;
import com.spring.jwt.subscription.service.UserSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of UserSubscriptionService.
 * Handles subscription purchases, credit management, and feature access.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserSubscriptionServiceImpl implements UserSubscriptionService
{

    private final UserSubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final UserRepository userRepository;
    private final UserSubscriptionMapper subscriptionMapper;
    private final TransactionIdGenerator transactionIdGenerator;
    private final SubscriptionReceiptService receiptService;

    @Override
    @Transactional
    @CacheEvict(value = "userSubscriptions", key = "#userId")
    public UserSubscriptionDTO purchaseSubscription(Integer userId, PurchaseSubscriptionRequest request)
    {
        log.info("Processing subscription purchase for user ID: {}", userId);

        // Check if user already has active subscription
        if (hasActiveSubscription(userId))
        {
            throw new ResourceAlreadyExistsException("User already has an active subscription");
        }

        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Get subscription plan
        SubscriptionPlan plan = planRepository.findById(request.getSubscriptionPlanId())
                .filter(p -> !p.getDeleted() && "ACTIVE".equals(p.getStatus()))
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found or inactive"));

        // Calculate subscription dates
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = calculateEndDate(startDate, plan, request.getBillingPeriod());

        // Calculate credits
        Integer allocatedCredits = calculateAllocatedCredits(plan, request.getBillingPeriod());

        // Create subscription
        UserSubscription subscription = UserSubscription.builder()
                .user(user)
                .subscriptionPlan(plan)
                .subscriptionStatus(UserSubscription.SubscriptionStatus.ACTIVE)
                .startDate(startDate)
                .endDate(endDate)
                .billingPeriod(request.getBillingPeriod())
                .allocatedCredits(allocatedCredits)
                .usedCredits(0)
                .remainingCredits(allocatedCredits)
                .dailyCreditsUsed(0)
                .monthlyCreditsUsed(0)
                .profileViewsUsed(0)
                .contactRevealsUsed(0)
                .interestsSentUsed(0)
                .amountPaid(request.getAmountPaid())
                .paymentMethod(request.getPaymentMethod())
                .transactionId(request.getTransactionId())
                .paymentDate(LocalDateTime.now())
                .autoRenewal(request.getAutoRenewal())
                .lastCreditReset(LocalDateTime.now())
                .build();

        UserSubscription savedSubscription = subscriptionRepository.save(subscription);
        log.info("Successfully created subscription ID: {} for user ID: {}", 
                savedSubscription.getUserSubscriptionId(), userId);

        return subscriptionMapper.toDTO(savedSubscription);
    }

    @Override
    @Transactional
    @CacheEvict(value = "userSubscriptions", key = "#request.userId")
    public SubscriptionReceiptDTO adminPurchaseSubscription(AdminPurchaseSubscriptionRequest request, String adminEmail) {
        log.info("Admin {} processing offline subscription purchase for user ID: {}", adminEmail, request.getUserId());

        Integer userId = request.getUserId();

        // Check if user already has active subscription
        if (hasActiveSubscription(userId)) {
            throw new ResourceAlreadyExistsException("User already has an active subscription");
        }

        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Get subscription plan
        SubscriptionPlan plan = planRepository.findById(request.getSubscriptionPlanId())
                .filter(p -> !p.getDeleted() && "ACTIVE".equals(p.getStatus()))
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found or inactive"));

        // Generate transaction ID automatically
        String transactionId = transactionIdGenerator.generateTransactionId(request.getPaymentMode());
        log.info("Generated transaction ID: {} for payment mode: {}", transactionId, request.getPaymentMode());

        // Calculate subscription dates
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = calculateEndDate(startDate, plan, request.getBillingPeriod());

        // Calculate credits
        Integer allocatedCredits = calculateAllocatedCredits(plan, request.getBillingPeriod());

        // Create subscription
        UserSubscription subscription = UserSubscription.builder()
                .user(user)
                .subscriptionPlan(plan)
                .subscriptionStatus(UserSubscription.SubscriptionStatus.ACTIVE)
                .startDate(startDate)
                .endDate(endDate)
                .billingPeriod(request.getBillingPeriod())
                .allocatedCredits(allocatedCredits)
                .usedCredits(0)
                .remainingCredits(allocatedCredits)
                .dailyCreditsUsed(0)
                .monthlyCreditsUsed(0)
                .profileViewsUsed(0)
                .contactRevealsUsed(0)
                .interestsSentUsed(0)
                .amountPaid(request.getAmountPaid())
                .paymentMethod(request.getPaymentMode())
                .transactionId(transactionId)
                .paymentDate(LocalDateTime.now())
                .autoRenewal(request.getAutoRenewal())
                .lastCreditReset(LocalDateTime.now())
                .build();

        UserSubscription savedSubscription = subscriptionRepository.save(subscription);
        log.info("Successfully created subscription ID: {} for user ID: {} by admin", 
                savedSubscription.getUserSubscriptionId(), userId);

        // Generate receipt
        SubscriptionReceiptDTO receipt = receiptService.generateReceipt(
                savedSubscription,
                request.getPaymentMode(),
                request.getPaymentNotes(),
                request.getExternalReferenceNumber(),
                request.getAdminRemarks(),
                adminEmail
        );

        log.info("Receipt generated: {} for subscription ID: {}", 
                receipt.getReceiptNumber(), savedSubscription.getUserSubscriptionId());

        return receipt;
    }

    @Override
    @Cacheable(value = "userSubscriptions", key = "#userId")
    public UserSubscriptionDTO getCurrentSubscription(Integer userId)
    {
        log.info("Fetching current subscription for user ID: {}", userId);
        
        UserSubscription subscription = subscriptionRepository
                .findActiveSubscriptionByUserId(userId, LocalDateTime.now())
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found for user"));

        return subscriptionMapper.toDTO(subscription);
    }

    @Override
    public boolean hasActiveSubscription(Integer userId)
    {
        return subscriptionRepository.hasActiveSubscription(userId, LocalDateTime.now());
    }

    @Override
    public boolean hasFeatureAccess(Integer userId, String featureName)
    {
        try
        {
            UserSubscription subscription = subscriptionRepository
                    .findActiveSubscriptionByUserId(userId, LocalDateTime.now())
                    .orElse(null);

            if (subscription == null || subscription.getSubscriptionPlan() == null) {
                return false;
            }

            return subscription.getSubscriptionPlan().hasFeature(featureName);
        } catch (Exception e)
        {
            log.error("Error checking feature access for user {}: {}", userId, e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "userSubscriptions", key = "#userId")
    public void cancelSubscription(Integer userId) {
        log.info("Cancelling subscription for user ID: {}", userId);

        UserSubscription subscription = subscriptionRepository
                .findActiveSubscriptionByUserId(userId, LocalDateTime.now())
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found"));

        subscription.cancel();
        subscriptionRepository.save(subscription);
        
        log.info("Successfully cancelled subscription for user ID: {}", userId);
    }

    @Override
    public List<UserSubscriptionDTO> getSubscriptionHistory(Integer userId)
    {
        log.info("Fetching subscription history for user ID: {}", userId);
        List<UserSubscription> subscriptions = subscriptionRepository.findAllByUserId(userId);
        return subscriptionMapper.toDTOList(subscriptions);
    }

    @Override
    @Transactional
    @CacheEvict(value = "userSubscriptions", key = "#userId")
    public boolean deductCredits(Integer userId, int amount, String reason)
    {
        log.info("Deducting {} credits from user ID: {} for reason: {}", amount, userId, reason);

        UserSubscription subscription = subscriptionRepository
                .findActiveSubscriptionByUserId(userId, LocalDateTime.now())
                .orElseThrow(() -> new SubscriptionRequiredException("Active subscription required"));

        // Check if unlimited feature
        SubscriptionPlan plan = subscription.getSubscriptionPlan();
        if (Boolean.TRUE.equals(plan.getUnlimitedProfileViews()) && "PROFILE_VIEW".equals(reason))
        {
            log.info("User has unlimited profile views, no credits deducted");
            return true;
        }

        // Check daily limit
        if (subscription.isDailyLimitReached())
        {
            log.warn("Daily limit reached for user ID: {}", userId);
            return false;
        }

        // Check monthly limit
        if (subscription.isMonthlyLimitReached())
        {
            log.warn("Monthly limit reached for user ID: {}", userId);
            return false;
        }

        // Deduct credits
        boolean success = subscription.deductCredits(amount);
        if (success)
        {
            subscriptionRepository.save(subscription);
            log.info("Successfully deducted {} credits from user ID: {}", amount, userId);
        } else {
            log.warn("Insufficient credits for user ID: {}", userId);
        }

        return success;
    }

    @Override
    @Transactional
    @CacheEvict(value = "userSubscriptions", key = "#userId")
    public void addCredits(Integer userId, int credits)
    {
        log.info("Adding {} credits to user ID: {}", credits, userId);

        UserSubscription subscription = subscriptionRepository
                .findActiveSubscriptionByUserId(userId, LocalDateTime.now())
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found"));

        subscription.addCredits(credits);
        subscriptionRepository.save(subscription);
        
        log.info("Successfully added {} credits to user ID: {}", credits, userId);
    }

    @Override
    public boolean canPerformAction(Integer userId, String actionType)
    {
        try
        {
            UserSubscription subscription = subscriptionRepository
                    .findActiveSubscriptionByUserId(userId, LocalDateTime.now())
                    .orElse(null);

            if (subscription == null)
            {
                return false;
            }

            SubscriptionPlan plan = subscription.getSubscriptionPlan();

            return switch (actionType.toUpperCase())
            {
                case "PROFILE_VIEW" -> {
                    if (Boolean.TRUE.equals(plan.getUnlimitedProfileViews()))
                    {
                        yield true;
                    }
                    yield !subscription.isDailyLimitReached() && 
                           !subscription.isMonthlyLimitReached() && 
                           subscription.getRemainingCredits() > 0;
                }
                case "CONTACT_REVEAL" ->
                {
                    if (Boolean.TRUE.equals(plan.getUnlimitedContactReveals()))
                    {
                        yield true;
                    }
                    yield subscription.getRemainingCredits() >= 5; // Contact reveal costs 5 credits
                }
                case "VIDEO_CALL" -> Boolean.TRUE.equals(plan.getVideoCalling());
                case "ADVANCED_SEARCH" -> Boolean.TRUE.equals(plan.getAdvancedSearch());
                default -> false;
            };
        } catch (Exception e)
        {
            log.error("Error checking action permission for user {}: {}", userId, e.getMessage());
            return false;
        }
    }

    @Override
    public int getRemainingCredits(Integer userId)
    {
        try
        {
            UserSubscription subscription = subscriptionRepository
                    .findActiveSubscriptionByUserId(userId, LocalDateTime.now())
                    .orElse(null);

            return subscription != null ? subscription.getRemainingCredits() : 0;
        } catch (Exception e)
        {
            log.error("Error getting remaining credits for user {}: {}", userId, e.getMessage());
            return 0;
        }
    }

    @Override
    public List<UserSubscriptionDTO> getAllSubscriptions()
    {
        log.info("Fetching all subscriptions for admin");
        List<UserSubscription> subscriptions = subscriptionRepository.findAll();
        return subscriptionMapper.toDTOList(subscriptions);
    }

    @Override
    @Transactional
    @CacheEvict(value = "userSubscriptions", key = "#userId")
    public UserSubscriptionDTO extendSubscription(Integer userId, int days)

    {
        log.info("Extending subscription for user ID: {} by {} days", userId, days);

        UserSubscription subscription = subscriptionRepository
                .findActiveSubscriptionByUserId(userId, LocalDateTime.now())
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found"));

        subscription.extendSubscription(days);
        UserSubscription updated = subscriptionRepository.save(subscription);
        
        log.info("Successfully extended subscription for user ID: {}", userId);
        return subscriptionMapper.toDTO(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = "userSubscriptions", key = "#userId")
    public void suspendSubscription(Integer userId)
    {
        log.info("Suspending subscription for user ID: {}", userId);

        UserSubscription subscription = subscriptionRepository
                .findActiveSubscriptionByUserId(userId, LocalDateTime.now())
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found"));

        subscription.suspend();
        subscriptionRepository.save(subscription);
        
        log.info("Successfully suspended subscription for user ID: {}", userId);
    }

    @Override
    @Transactional
    @CacheEvict(value = "userSubscriptions", key = "#userId")
    public UserSubscriptionDTO reactivateSubscription(Integer userId)
    {
        log.info("Reactivating subscription for user ID: {}", userId);

        List<UserSubscription> subscriptions = subscriptionRepository.findAllByUserId(userId);
        UserSubscription subscription = subscriptions.stream()
                .filter(s -> s.getSubscriptionStatus() == UserSubscription.SubscriptionStatus.SUSPENDED)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No suspended subscription found"));

        subscription.reactivate();
        UserSubscription updated = subscriptionRepository.save(subscription);
        
        log.info("Successfully reactivated subscription for user ID: {}", userId);
        return subscriptionMapper.toDTO(updated);
    }

    @Override
    public SubscriptionStatistics getStatistics()
    {
        log.info("Fetching subscription statistics");
        
        List<Object[]> stats = subscriptionRepository.getSubscriptionStatistics();
        long total = 0, active = 0, expired = 0, cancelled = 0, suspended = 0;

        for (Object[] stat : stats) {
            UserSubscription.SubscriptionStatus status = (UserSubscription.SubscriptionStatus) stat[0];
            long count = ((Number) stat[1]).longValue();
            total += count;

            switch (status) {
                case ACTIVE -> active = count;
                case EXPIRED -> expired = count;
                case CANCELLED -> cancelled = count;
                case SUSPENDED -> suspended = count;
            }
        }

        return new SubscriptionStatistics(total, active, expired, cancelled, suspended);
    }

    @Override
    public UserCreditsDTO getUserCredits(Integer userId)
    {
        log.info("Fetching credit information for user ID: {}", userId);
        
        try {
            // Get active subscription
            UserSubscription subscription = subscriptionRepository
                    .findActiveSubscriptionByUserId(userId, LocalDateTime.now())
                    .orElse(null);
            
            if (subscription == null) {
                // No active subscription - return empty credits
                return UserCreditsDTO.builder()
                        .hasActiveSubscription(false)
                        .canViewProfiles(false)
                        .canRevealContacts(false)
                        .canSendInterests(false)
                        .warningMessage("No active subscription. Please purchase a subscription to access profiles.")
                        .recommendation("Subscribe to a plan to start viewing profiles and connecting with matches.")
                        .build();
            }
            
            SubscriptionPlan plan = subscription.getSubscriptionPlan();
            
            // Calculate percentages
            double usagePercentage = subscription.getAllocatedCredits() > 0 
                    ? (subscription.getUsedCredits() * 100.0) / subscription.getAllocatedCredits()
                    : 0.0;
            
            // Calculate daily remaining
            Integer dailyRemaining = plan.getDailyCreditLimit() != null
                    ? Math.max(0, plan.getDailyCreditLimit() - subscription.getDailyCreditsUsed())
                    : null;
            
            // Calculate monthly remaining
            Integer monthlyRemaining = plan.getMonthlyCreditLimit() != null
                    ? Math.max(0, plan.getMonthlyCreditLimit() - subscription.getMonthlyCreditsUsed())
                    : null;
            
            // Determine warnings
            boolean lowCredits = subscription.getRemainingCredits() < (subscription.getAllocatedCredits() * 0.2);
            boolean criticalCredits = subscription.getRemainingCredits() < (subscription.getAllocatedCredits() * 0.1);
            
            String warningMsg = null;
            if (criticalCredits) {
                warningMsg = "Critical: Only " + subscription.getRemainingCredits() + " credits remaining!";
            } else if (lowCredits) {
                warningMsg = "Warning: Running low on credits (" + subscription.getRemainingCredits() + " remaining)";
            }
            
            // Determine recommendation
            String recommendation = null;
            if (subscription.getRemainingCredits() < 10) {
                recommendation = "Consider upgrading to a higher plan for more credits";
            } else if (subscription.isDailyLimitReached()) {
                recommendation = "Daily limit reached. Try again tomorrow or upgrade for higher limits";
            }
            
            // Calculate next reset (assuming daily reset at midnight)
            LocalDateTime nextReset = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0);
            
            return UserCreditsDTO.builder()
                    // Subscription Status
                    .hasActiveSubscription(true)
                    .subscriptionPlanName(plan.getName())
                    .subscriptionPlanCode(plan.getPlanCode())
                    .subscriptionStartDate(subscription.getStartDate())
                    .subscriptionEndDate(subscription.getEndDate())
                    .subscriptionStatus(subscription.getSubscriptionStatus().name())
                    
                    // Credit Information
                    .allocatedCredits(subscription.getAllocatedCredits())
                    .usedCredits(subscription.getUsedCredits())
                    .remainingCredits(subscription.getRemainingCredits())
                    .creditUsagePercentage(usagePercentage)
                    
                    // Daily Limits
                    .dailyLimit(plan.getDailyCreditLimit())
                    .dailyCreditsUsed(subscription.getDailyCreditsUsed())
                    .dailyCreditsRemaining(dailyRemaining)
                    .dailyLimitReached(subscription.isDailyLimitReached())
                    
                    // Monthly Limits
                    .monthlyLimit(plan.getMonthlyCreditLimit())
                    .monthlyCreditsUsed(subscription.getMonthlyCreditsUsed())
                    .monthlyCreditsRemaining(monthlyRemaining)
                    .monthlyLimitReached(subscription.isMonthlyLimitReached())
                    
                    // Usage Breakdown
                    .profileViewsUsed(subscription.getProfileViewsUsed())
                    .contactRevealsUsed(subscription.getContactRevealsUsed())
                    .interestsSentUsed(subscription.getInterestsSentUsed())
                    
                    // Feature Access
                    .canViewProfiles(canPerformAction(userId, "PROFILE_VIEW"))
                    .canRevealContacts(canPerformAction(userId, "CONTACT_REVEAL"))
                    .canSendInterests(canPerformAction(userId, "SEND_INTEREST"))
                    .hasUnlimitedProfileViews(Boolean.TRUE.equals(plan.getUnlimitedProfileViews()))
                    .hasUnlimitedContactReveals(Boolean.TRUE.equals(plan.getUnlimitedContactReveals()))
                    
                    // Credit Costs (set to 1 as default since fields don't exist in SubscriptionPlan)
                    .profileViewCost(1)
                    .contactRevealCost(1)
                    .interestSendCost(1)
                    
                    // Reset Information
                    .lastCreditReset(subscription.getLastCreditReset())
                    .nextCreditReset(nextReset)
                    
                    // Warnings
                    .lowCreditsWarning(lowCredits)
                    .criticalCreditsWarning(criticalCredits)
                    .warningMessage(warningMsg)
                    
                    // Recommendations
                    .recommendation(recommendation)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error fetching credits for user {}: {}", userId, e.getMessage());
            return UserCreditsDTO.builder()
                    .hasActiveSubscription(false)
                    .warningMessage("Error loading credit information")
                    .build();
        }
    }

    @Override
    public boolean canPerformActionWithCheck(Integer userId, String actionType) {
        log.debug("Checking if user {} can perform action: {}", userId, actionType);
        
        try {
            UserSubscription subscription = subscriptionRepository
                    .findActiveSubscriptionByUserId(userId, LocalDateTime.now())
                    .orElse(null);
            
            if (subscription == null) {
                log.warn("User {} has no active subscription", userId);
                return false;
            }
            
            SubscriptionPlan plan = subscription.getSubscriptionPlan();
            
            // Check based on action type
            switch (actionType.toUpperCase()) {
                case "PROFILE_VIEW":
                    // Check if unlimited
                    if (Boolean.TRUE.equals(plan.getUnlimitedProfileViews())) {
                        return true;
                    }
                    // Check daily limit
                    if (subscription.isDailyLimitReached()) {
                        log.warn("User {} has reached daily limit", userId);
                        return false;
                    }
                    // Check monthly limit
                    if (subscription.isMonthlyLimitReached()) {
                        log.warn("User {} has reached monthly limit", userId);
                        return false;
                    }
                    // Check remaining credits (using 1 credit as default cost)
                    if (subscription.getRemainingCredits() < 1) {
                        log.warn("User {} has insufficient credits", userId);
                        return false;
                    }
                    return true;
                    
                case "CONTACT_REVEAL":
                    if (Boolean.TRUE.equals(plan.getUnlimitedContactReveals())) {
                        return true;
                    }
                    return subscription.getRemainingCredits() >= 1;
                    
                case "SEND_INTEREST":
                    return subscription.getRemainingCredits() >= 1;
                    
                default:
                    return false;
            }
        } catch (Exception e) {
            log.error("Error checking action permission for user {}: {}", userId, e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "userSubscriptions", key = "#viewerId")
    public boolean recordProfileView(Integer viewerId, Integer viewedProfileId) {
        log.info("Recording profile view: viewer={}, viewed={}", viewerId, viewedProfileId);
        
        try {
            // Check if user can view profiles
            if (!canPerformActionWithCheck(viewerId, "PROFILE_VIEW")) {
                log.warn("User {} cannot view profiles (no subscription or insufficient credits)", viewerId);
                return false;
            }
            
            // Get subscription
            UserSubscription subscription = subscriptionRepository
                    .findActiveSubscriptionByUserId(viewerId, LocalDateTime.now())
                    .orElseThrow(() -> new SubscriptionRequiredException("Active subscription required"));
            
            SubscriptionPlan plan = subscription.getSubscriptionPlan();
            
            // Deduct credits (unless unlimited) - using 1 credit as default cost
            if (!Boolean.TRUE.equals(plan.getUnlimitedProfileViews())) {
                boolean success = deductCredits(viewerId, 1, "PROFILE_VIEW");
                if (!success) {
                    log.warn("Failed to deduct credits for user {}", viewerId);
                    return false;
                }
            }
            
            // Increment profile views counter
            subscription.setProfileViewsUsed(subscription.getProfileViewsUsed() + 1);
            subscriptionRepository.save(subscription);
            
            log.info("Profile view recorded successfully for user {}", viewerId);
            return true;
            
        } catch (Exception e) {
            log.error("Error recording profile view: {}", e.getMessage());
            return false;
        }
    }

    // ========== PRIVATE HELPER METHODS ==========

    private LocalDateTime calculateEndDate(LocalDateTime startDate, SubscriptionPlan plan, String billingPeriod) {
        if ("YEARLY".equalsIgnoreCase(billingPeriod)) {
            return startDate.plusMonths(12);
        } else {
            return startDate.plusMonths(plan.getDurationMonths() != null ? plan.getDurationMonths() : 1);
        }
    }

    private Integer calculateAllocatedCredits(SubscriptionPlan plan, String billingPeriod) {
        if (Boolean.TRUE.equals(plan.getUnlimitedProfileViews())) {
            return Integer.MAX_VALUE; // Unlimited
        }
        
        if ("YEARLY".equalsIgnoreCase(billingPeriod)) {
            return plan.getTotalCredits() != null ? plan.getTotalCredits() * 12 : 0;
        } else {
            return plan.getTotalCredits() != null ? plan.getTotalCredits() : 0;
        }
    }
}
