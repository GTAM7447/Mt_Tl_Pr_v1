package com.spring.jwt.subscription.repository;

import com.spring.jwt.subscription.entity.UserSubscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for UserSubscription entity.
 * Provides database operations for user subscriptions.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Integer> {

    /**
     * Find active subscription for a user
     */
    @Query("SELECT us FROM UserSubscription us " +
           "WHERE us.user.id = :userId " +
           "AND us.subscriptionStatus = 'ACTIVE' " +
           "AND us.startDate <= :currentDate " +
           "AND (us.endDate IS NULL OR us.endDate >= :currentDate) " +
           "AND us.deleted = false")
    Optional<UserSubscription> findActiveSubscriptionByUserId(
            @Param("userId") Integer userId,
            @Param("currentDate") LocalDateTime currentDate
    );

    /**
     * Find all subscriptions for a user
     */
    @Query("SELECT us FROM UserSubscription us WHERE us.user.id = :userId AND us.deleted = false ORDER BY us.createdAt DESC")
    List<UserSubscription> findAllByUserId(@Param("userId") Integer userId);

    /**
     * Find subscriptions by status
     */
    List<UserSubscription> findBySubscriptionStatusAndDeletedFalse(UserSubscription.SubscriptionStatus status);

    /**
     * Find expired subscriptions that need to be marked
     */
    @Query("SELECT us FROM UserSubscription us " +
           "WHERE us.subscriptionStatus = 'ACTIVE' " +
           "AND us.endDate < :currentDate " +
           "AND us.deleted = false")
    List<UserSubscription> findExpiredSubscriptions(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find subscriptions expiring soon
     */
    @Query("SELECT us FROM UserSubscription us " +
           "WHERE us.subscriptionStatus = 'ACTIVE' " +
           "AND us.endDate BETWEEN :startDate AND :endDate " +
           "AND us.deleted = false")
    List<UserSubscription> findSubscriptionsExpiringSoon(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find subscriptions with auto-renewal enabled
     */
    @Query("SELECT us FROM UserSubscription us " +
           "WHERE us.autoRenewal = true " +
           "AND us.subscriptionStatus = 'ACTIVE' " +
           "AND us.deleted = false")
    List<UserSubscription> findAutoRenewalSubscriptions();

    /**
     * Count active subscriptions
     */
    @Query("SELECT COUNT(us) FROM UserSubscription us " +
           "WHERE us.subscriptionStatus = 'ACTIVE' " +
           "AND us.deleted = false")
    long countActiveSubscriptions();

    /**
     * Count subscriptions by plan
     */
    @Query("SELECT COUNT(us) FROM UserSubscription us " +
           "WHERE us.subscriptionPlan.subscriptionId = :planId " +
           "AND us.subscriptionStatus = 'ACTIVE' " +
           "AND us.deleted = false")
    long countActiveSubscriptionsByPlan(@Param("planId") Integer planId);

    /**
     * Find all subscriptions with pagination (for admin)
     */
    @Query("SELECT us FROM UserSubscription us WHERE us.deleted = false")
    Page<UserSubscription> findAllSubscriptions(Pageable pageable);

    /**
     * Find subscriptions by plan
     */
    @Query("SELECT us FROM UserSubscription us " +
           "WHERE us.subscriptionPlan.subscriptionId = :planId " +
           "AND us.deleted = false " +
           "ORDER BY us.createdAt DESC")
    List<UserSubscription> findByPlanId(@Param("planId") Integer planId);

    /**
     * Find subscriptions needing daily credit reset
     */
    @Query("SELECT us FROM UserSubscription us " +
           "WHERE us.subscriptionStatus = 'ACTIVE' " +
           "AND us.dailyCreditsUsed > 0 " +
           "AND us.deleted = false")
    List<UserSubscription> findSubscriptionsNeedingDailyReset();

    /**
     * Find subscriptions needing monthly credit reset
     */
    @Query("SELECT us FROM UserSubscription us " +
           "WHERE us.subscriptionStatus = 'ACTIVE' " +
           "AND us.monthlyCreditsUsed > 0 " +
           "AND us.deleted = false")
    List<UserSubscription> findSubscriptionsNeedingMonthlyReset();

    /**
     * Get subscription statistics
     */
    @Query("SELECT us.subscriptionStatus, COUNT(us) FROM UserSubscription us " +
           "WHERE us.deleted = false " +
           "GROUP BY us.subscriptionStatus")
    List<Object[]> getSubscriptionStatistics();

    /**
     * Find users with low credits
     */
    @Query("SELECT us FROM UserSubscription us " +
           "WHERE us.subscriptionStatus = 'ACTIVE' " +
           "AND us.remainingCredits < :threshold " +
           "AND us.deleted = false")
    List<UserSubscription> findUsersWithLowCredits(@Param("threshold") Integer threshold);

    /**
     * Check if user has any active subscription
     */
    @Query("SELECT CASE WHEN COUNT(us) > 0 THEN true ELSE false END FROM UserSubscription us " +
           "WHERE us.user.id = :userId " +
           "AND us.subscriptionStatus = 'ACTIVE' " +
           "AND (us.endDate IS NULL OR us.endDate >= :currentDate) " +
           "AND us.deleted = false")
    boolean hasActiveSubscription(@Param("userId") Integer userId, @Param("currentDate") LocalDateTime currentDate);
}
