package com.spring.jwt.subscription.repository;

import com.spring.jwt.subscription.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for SubscriptionPlan entity.
 * Provides database operations for subscription plans.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Integer> {

    /**
     * Find plan by plan code
     */
    Optional<SubscriptionPlan> findByPlanCodeIgnoreCase(String planCode);

    /**
     * Find all active plans ordered by display order
     */
    @Query("SELECT sp FROM SubscriptionPlan sp WHERE sp.status = 'ACTIVE' AND sp.deleted = false ORDER BY sp.displayOrder ASC")
    List<SubscriptionPlan> findAllActivePlans();

    /**
     * Find popular plans
     */
    @Query("SELECT sp FROM SubscriptionPlan sp WHERE sp.status = 'ACTIVE' AND sp.isPopular = true AND sp.deleted = false ORDER BY sp.displayOrder ASC")
    List<SubscriptionPlan> findPopularPlans();

    /**
     * Find recommended plans
     */
    @Query("SELECT sp FROM SubscriptionPlan sp WHERE sp.status = 'ACTIVE' AND sp.isRecommended = true AND sp.deleted = false ORDER BY sp.displayOrder ASC")
    List<SubscriptionPlan> findRecommendedPlans();

    /**
     * Find plans by status
     */
    List<SubscriptionPlan> findByStatusAndDeletedFalseOrderByDisplayOrderAsc(String status);

    /**
     * Check if plan code exists
     */
    boolean existsByPlanCodeIgnoreCaseAndDeletedFalse(String planCode);

    /**
     * Find all plans including inactive (for admin)
     */
    @Query("SELECT sp FROM SubscriptionPlan sp WHERE sp.deleted = false ORDER BY sp.displayOrder ASC")
    List<SubscriptionPlan> findAllPlansForAdmin();

    /**
     * Get plan statistics
     */
    @Query("SELECT COUNT(sp) FROM SubscriptionPlan sp WHERE sp.status = 'ACTIVE' AND sp.deleted = false")
    long countActivePlans();

    /**
     * Find plans by price range
     */
    @Query("SELECT sp FROM SubscriptionPlan sp WHERE sp.status = 'ACTIVE' AND sp.deleted = false " +
           "AND sp.monthlyPrice BETWEEN :minPrice AND :maxPrice ORDER BY sp.monthlyPrice ASC")
    List<SubscriptionPlan> findByPriceRange(@Param("minPrice") java.math.BigDecimal minPrice, 
                                            @Param("maxPrice") java.math.BigDecimal maxPrice);

    /**
     * Find plans with specific feature enabled
     */
    @Query("SELECT sp FROM SubscriptionPlan sp WHERE sp.status = 'ACTIVE' AND sp.deleted = false " +
           "AND (:featureName = 'VIDEO_CALLING' AND sp.videoCalling = true " +
           "OR :featureName = 'ADVANCED_SEARCH' AND sp.advancedSearch = true " +
           "OR :featureName = 'PRIORITY_SUPPORT' AND sp.prioritySupport = true)")
    List<SubscriptionPlan> findByFeature(@Param("featureName") String featureName);
}
