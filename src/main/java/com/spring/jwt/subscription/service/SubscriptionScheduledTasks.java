package com.spring.jwt.subscription.service;

import com.spring.jwt.subscription.entity.UserSubscription;
import com.spring.jwt.subscription.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled tasks for subscription management.
 * Handles daily/monthly credit resets and subscription expiry.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionScheduledTasks {

    private final UserSubscriptionRepository subscriptionRepository;

    /**
     * Reset daily credits at midnight every day
     * Cron: 0 0 0 * * * (Every day at 00:00:00)
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetDailyCredits() {
        log.info("Starting daily credit reset task");
        
        try {
            List<UserSubscription> subscriptions = subscriptionRepository.findSubscriptionsNeedingDailyReset();
            log.info("Found {} subscriptions needing daily credit reset", subscriptions.size());

            for (UserSubscription subscription : subscriptions) {
                subscription.resetDailyCredits();
            }

            subscriptionRepository.saveAll(subscriptions);
            log.info("Successfully reset daily credits for {} subscriptions", subscriptions.size());
        } catch (Exception e) {
            log.error("Error during daily credit reset: {}", e.getMessage(), e);
        }
    }

    /**
     * Reset monthly credits on 1st of every month
     * Cron: 0 0 0 1 * * (1st day of month at 00:00:00)
     */
    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void resetMonthlyCredits() {
        log.info("Starting monthly credit reset task");
        
        try {
            List<UserSubscription> subscriptions = subscriptionRepository.findSubscriptionsNeedingMonthlyReset();
            log.info("Found {} subscriptions needing monthly credit reset", subscriptions.size());

            for (UserSubscription subscription : subscriptions) {
                subscription.resetMonthlyCredits();
            }

            subscriptionRepository.saveAll(subscriptions);
            log.info("Successfully reset monthly credits for {} subscriptions", subscriptions.size());
        } catch (Exception e) {
            log.error("Error during monthly credit reset: {}", e.getMessage(), e);
        }
    }

    /**
     * Check and expire subscriptions every hour
     * Cron: 0 0 * * * * (Every hour at minute 0)
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void expireSubscriptions() {
        log.info("Starting subscription expiry check task");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            List<UserSubscription> expiredSubscriptions = subscriptionRepository.findExpiredSubscriptions(now);
            log.info("Found {} expired subscriptions", expiredSubscriptions.size());

            for (UserSubscription subscription : expiredSubscriptions) {
                subscription.markExpired();
                log.debug("Marked subscription {} as expired for user {}", 
                        subscription.getUserSubscriptionId(), 
                        subscription.getUser().getId());
            }

            subscriptionRepository.saveAll(expiredSubscriptions);
            log.info("Successfully marked {} subscriptions as expired", expiredSubscriptions.size());
        } catch (Exception e) {
            log.error("Error during subscription expiry check: {}", e.getMessage(), e);
        }
    }

    /**
     * Send expiry reminders for subscriptions expiring in 7 days
     * Cron: 0 0 9 * * * (Every day at 09:00:00)
     */
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional(readOnly = true)
    public void sendExpiryReminders() {
        log.info("Starting subscription expiry reminder task");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime sevenDaysLater = now.plusDays(7);
            
            List<UserSubscription> expiringSoon = subscriptionRepository
                    .findSubscriptionsExpiringSoon(now, sevenDaysLater);
            
            log.info("Found {} subscriptions expiring in next 7 days", expiringSoon.size());

            for (UserSubscription subscription : expiringSoon) {
                // TODO: Send email/notification to user
                log.debug("Subscription {} for user {} expires on {}", 
                        subscription.getUserSubscriptionId(),
                        subscription.getUser().getId(),
                        subscription.getEndDate());
            }

            log.info("Successfully processed expiry reminders for {} subscriptions", expiringSoon.size());
        } catch (Exception e) {
            log.error("Error during expiry reminder task: {}", e.getMessage(), e);
        }
    }

    /**
     * Process auto-renewals for subscriptions
     * Cron: 0 0 2 * * * (Every day at 02:00:00)
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional(readOnly = true)
    public void processAutoRenewals() {
        log.info("Starting auto-renewal processing task");
        
        try {
            List<UserSubscription> autoRenewalSubscriptions = subscriptionRepository.findAutoRenewalSubscriptions();
            log.info("Found {} subscriptions with auto-renewal enabled", autoRenewalSubscriptions.size());

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime threeDaysLater = now.plusDays(3);

            for (UserSubscription subscription : autoRenewalSubscriptions) {
                if (subscription.getEndDate() != null && 
                    subscription.getEndDate().isBefore(threeDaysLater) &&
                    subscription.getEndDate().isAfter(now)) {
                    
                    // TODO: Process payment and renew subscription
                    log.debug("Subscription {} for user {} needs auto-renewal", 
                            subscription.getUserSubscriptionId(),
                            subscription.getUser().getId());
                }
            }

            log.info("Successfully processed auto-renewal check");
        } catch (Exception e) {
            log.error("Error during auto-renewal processing: {}", e.getMessage(), e);
        }
    }

    /**
     * Clean up old expired subscriptions (older than 1 year)
     * Cron: 0 0 3 1 * * (1st day of month at 03:00:00)
     */
    @Scheduled(cron = "0 0 3 1 * *")
    @Transactional
    public void cleanupOldSubscriptions() {
        log.info("Starting old subscription cleanup task");
        
        try {
            LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
            List<UserSubscription> oldExpired = subscriptionRepository.findExpiredSubscriptions(oneYearAgo);
            
            log.info("Found {} old expired subscriptions to archive", oldExpired.size());

            // TODO: Archive to separate table or mark for deletion
            for (UserSubscription subscription : oldExpired) {
                log.debug("Old subscription {} can be archived", subscription.getUserSubscriptionId());
            }

            log.info("Successfully processed old subscription cleanup");
        } catch (Exception e) {
            log.error("Error during old subscription cleanup: {}", e.getMessage(), e);
        }
    }
}
