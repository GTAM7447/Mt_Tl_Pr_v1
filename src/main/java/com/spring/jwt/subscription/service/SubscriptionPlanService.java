package com.spring.jwt.subscription.service;

import com.spring.jwt.subscription.dto.CreatePlanRequest;
import com.spring.jwt.subscription.dto.SubscriptionPlanDTO;
import com.spring.jwt.subscription.dto.UpdatePlanRequest;

import java.util.List;

/**
 * Service interface for Subscription Plan operations.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
public interface SubscriptionPlanService {

    // ========== PUBLIC APIs ==========
    
    /**
     * Get all active subscription plans
     */
    List<SubscriptionPlanDTO> getAllActivePlans();

    /**
     * Get plan by ID
     */
    SubscriptionPlanDTO getPlanById(Integer planId);

    /**
     * Get plan by code
     */
    SubscriptionPlanDTO getPlanByCode(String planCode);

    /**
     * Get popular plans
     */
    List<SubscriptionPlanDTO> getPopularPlans();

    /**
     * Get recommended plans
     */
    List<SubscriptionPlanDTO> getRecommendedPlans();

    // ========== ADMIN APIs ==========
    
    /**
     * Create new subscription plan
     */
    SubscriptionPlanDTO createPlan(CreatePlanRequest request);

    /**
     * Update existing plan
     */
    SubscriptionPlanDTO updatePlan(Integer planId, UpdatePlanRequest request);

    /**
     * Toggle plan status (ACTIVE/INACTIVE)
     */
    SubscriptionPlanDTO togglePlanStatus(Integer planId);

    /**
     * Soft delete plan
     */
    void deletePlan(Integer planId);

    /**
     * Get all plans including inactive (for admin)
     */
    List<SubscriptionPlanDTO> getAllPlansForAdmin();

    /**
     * Get plan statistics
     */
    PlanStatistics getPlanStatistics();

    /**
     * Nested class for statistics
     */
    record PlanStatistics(
            long totalPlans,
            long activePlans,
            long inactivePlans,
            long popularPlans,
            long recommendedPlans
    ) {}
}
