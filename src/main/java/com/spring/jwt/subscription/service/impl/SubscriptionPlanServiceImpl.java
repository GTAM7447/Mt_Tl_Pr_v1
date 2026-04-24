package com.spring.jwt.subscription.service.impl;

import com.spring.jwt.exception.ResourceAlreadyExistsException;
import com.spring.jwt.exception.ResourceNotFoundException;
import com.spring.jwt.subscription.dto.CreatePlanRequest;
import com.spring.jwt.subscription.dto.SubscriptionPlanDTO;
import com.spring.jwt.subscription.dto.UpdatePlanRequest;
import com.spring.jwt.subscription.entity.SubscriptionPlan;
import com.spring.jwt.subscription.mapper.SubscriptionPlanMapper;
import com.spring.jwt.subscription.repository.SubscriptionPlanRepository;
import com.spring.jwt.subscription.service.SubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of SubscriptionPlanService.
 * Handles all subscription plan operations with caching.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionPlanMapper planMapper;

    @Override
    @Cacheable(value = "subscriptionPlans", key = "'activePlans'")
    public List<SubscriptionPlanDTO> getAllActivePlans() {
        log.info("Fetching all active subscription plans");
        List<SubscriptionPlan> plans = planRepository.findAllActivePlans();
        log.info("Found {} active plans", plans.size());
        return planMapper.toDTOList(plans);
    }

    @Override
    @Cacheable(value = "subscriptionPlans", key = "'plan_' + #planId")
    public SubscriptionPlanDTO getPlanById(Integer planId) {
        log.info("Fetching subscription plan by ID: {}", planId);
        SubscriptionPlan plan = planRepository.findById(planId)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with ID: " + planId));
        return planMapper.toDTO(plan);
    }

    @Override
    @Cacheable(value = "subscriptionPlans", key = "'code_' + #planCode")
    public SubscriptionPlanDTO getPlanByCode(String planCode) {
        log.info("Fetching subscription plan by code: {}", planCode);
        SubscriptionPlan plan = planRepository.findByPlanCodeIgnoreCase(planCode)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with code: " + planCode));
        return planMapper.toDTO(plan);
    }

    @Override
    @Cacheable(value = "subscriptionPlans", key = "'popularPlans'")
    public List<SubscriptionPlanDTO> getPopularPlans() {
        log.info("Fetching popular subscription plans");
        List<SubscriptionPlan> plans = planRepository.findPopularPlans();
        log.info("Found {} popular plans", plans.size());
        return planMapper.toDTOList(plans);
    }

    @Override
    @Cacheable(value = "subscriptionPlans", key = "'recommendedPlans'")
    public List<SubscriptionPlanDTO> getRecommendedPlans() {
        log.info("Fetching recommended subscription plans");
        List<SubscriptionPlan> plans = planRepository.findRecommendedPlans();
        log.info("Found {} recommended plans", plans.size());
        return planMapper.toDTOList(plans);
    }

    @Override
    @Transactional
    @CacheEvict(value = "subscriptionPlans", allEntries = true)
    public SubscriptionPlanDTO createPlan(CreatePlanRequest request) {
        log.info("Creating new subscription plan: {}", request.getPlanCode());

        // Check if plan code already exists
        if (planRepository.existsByPlanCodeIgnoreCaseAndDeletedFalse(request.getPlanCode())) {
            throw new ResourceAlreadyExistsException("Subscription plan already exists with code: " + request.getPlanCode());
        }

        SubscriptionPlan plan = planMapper.toEntity(request);
        SubscriptionPlan savedPlan = planRepository.save(plan);
        
        log.info("Successfully created subscription plan with ID: {}", savedPlan.getSubscriptionId());
        return planMapper.toDTO(savedPlan);
    }

    @Override
    @Transactional
    @CacheEvict(value = "subscriptionPlans", allEntries = true)
    public SubscriptionPlanDTO updatePlan(Integer planId, UpdatePlanRequest request) {
        log.info("Updating subscription plan ID: {}", planId);

        SubscriptionPlan plan = planRepository.findById(planId)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with ID: " + planId));

        planMapper.updateEntity(plan, request);
        SubscriptionPlan updatedPlan = planRepository.save(plan);
        
        log.info("Successfully updated subscription plan ID: {}", planId);
        return planMapper.toDTO(updatedPlan);
    }

    @Override
    @Transactional
    @CacheEvict(value = "subscriptionPlans", allEntries = true)
    public SubscriptionPlanDTO togglePlanStatus(Integer planId) {
        log.info("Toggling status for subscription plan ID: {}", planId);

        SubscriptionPlan plan = planRepository.findById(planId)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with ID: " + planId));

        String newStatus = "ACTIVE".equals(plan.getStatus()) ? "INACTIVE" : "ACTIVE";
        plan.setStatus(newStatus);
        SubscriptionPlan updatedPlan = planRepository.save(plan);
        
        log.info("Successfully toggled plan status to: {}", newStatus);
        return planMapper.toDTO(updatedPlan);
    }

    @Override
    @Transactional
    @CacheEvict(value = "subscriptionPlans", allEntries = true)
    public void deletePlan(Integer planId) {
        log.info("Soft deleting subscription plan ID: {}", planId);

        SubscriptionPlan plan = planRepository.findById(planId)
                .filter(p -> !p.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found with ID: " + planId));

        plan.setDeleted(true);
        plan.setStatus("INACTIVE");
        planRepository.save(plan);
        
        log.info("Successfully soft deleted subscription plan ID: {}", planId);
    }

    @Override
    public List<SubscriptionPlanDTO> getAllPlansForAdmin() {
        log.info("Fetching all subscription plans for admin");
        List<SubscriptionPlan> plans = planRepository.findAllPlansForAdmin();
        log.info("Found {} total plans", plans.size());
        return planMapper.toDTOList(plans);
    }

    @Override
    public PlanStatistics getPlanStatistics() {
        log.info("Fetching subscription plan statistics");
        
        List<SubscriptionPlan> allPlans = planRepository.findAllPlansForAdmin();
        long totalPlans = allPlans.size();
        long activePlans = allPlans.stream().filter(p -> "ACTIVE".equals(p.getStatus())).count();
        long inactivePlans = totalPlans - activePlans;
        long popularPlans = allPlans.stream().filter(p -> Boolean.TRUE.equals(p.getIsPopular())).count();
        long recommendedPlans = allPlans.stream().filter(p -> Boolean.TRUE.equals(p.getIsRecommended())).count();

        return new PlanStatistics(totalPlans, activePlans, inactivePlans, popularPlans, recommendedPlans);
    }
}
