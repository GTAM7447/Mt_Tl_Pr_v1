package com.spring.jwt.subscription.mapper;

import com.spring.jwt.subscription.dto.CreatePlanRequest;
import com.spring.jwt.subscription.dto.SubscriptionPlanDTO;
import com.spring.jwt.subscription.dto.UpdatePlanRequest;
import com.spring.jwt.subscription.entity.SubscriptionPlan;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper for SubscriptionPlan entity and DTOs.
 * Handles conversion between entity and DTO representations.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Component
public class SubscriptionPlanMapper {

    /**
     * Convert entity to DTO with full details
     */
    public SubscriptionPlanDTO toDTO(SubscriptionPlan entity) {
        if (entity == null) {
            return null;
        }

        return SubscriptionPlanDTO.builder()
                .subscriptionId(entity.getSubscriptionId())
                .name(entity.getName())
                .description(entity.getDescription())
                .planCode(entity.getPlanCode())
                .monthly(buildMonthlyPricing(entity))
                .yearly(buildYearlyPricing(entity))
                .features(buildFeatureList(entity))
                .isPopular(entity.getIsPopular())
                .isRecommended(entity.getIsRecommended())
                .displayOrder(entity.getDisplayOrder())
                .status(entity.getStatus())
                .build();
    }

    /**
     * Convert list of entities to DTOs
     */
    public List<SubscriptionPlanDTO> toDTOList(List<SubscriptionPlan> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Convert CreatePlanRequest to entity
     */
    public SubscriptionPlan toEntity(CreatePlanRequest request) {
        if (request == null) {
            return null;
        }

        SubscriptionPlan entity = SubscriptionPlan.builder()
                .name(request.getName())
                .description(request.getDescription())
                .planCode(request.getPlanCode().toUpperCase())
                .monthlyPrice(request.getMonthlyPrice())
                .yearlyPrice(request.getYearlyPrice())
                .discountPercentage(request.getDiscountPercentage())
                .currency("INR")
                .totalCredits(request.getTotalCredits())
                .dailyCreditLimit(request.getDailyCreditLimit())
                .monthlyCreditLimit(request.getMonthlyCreditLimit())
                .durationMonths(request.getDurationMonths())
                .validityDays(request.getValidityDays())
                .maxProfileViews(request.getMaxProfileViews())
                .maxContactReveals(request.getMaxContactReveals())
                .maxInterestsPerMonth(request.getMaxInterestsPerMonth())
                .unlimitedProfileViews(request.getUnlimitedProfileViews())
                .unlimitedContactReveals(request.getUnlimitedContactReveals())
                .prioritySupport(request.getPrioritySupport())
                .videoCalling(request.getVideoCalling())
                .advancedSearch(request.getAdvancedSearch())
                .profileHighlighting(request.getProfileHighlighting())
                .matchmakerAssistance(request.getMatchmakerAssistance())
                .horoscopeMatching(request.getHoroscopeMatching())
                .verifiedBadge(request.getVerifiedBadge())
                .privacyControls(request.getPrivacyControls())
                .chatMessaging(request.getChatMessaging())
                .photoGallery(request.getPhotoGallery())
                .backgroundVerification(request.getBackgroundVerification())
                .exclusiveProfiles(request.getExclusiveProfiles())
                .vipConcierge(request.getVipConcierge())
                .professionalPhotography(request.getProfessionalPhotography())
                .isPopular(request.getIsPopular())
                .isRecommended(request.getIsRecommended())
                .displayOrder(request.getDisplayOrder())
                .status("ACTIVE")
                .build();

        // Calculate discounted prices
        calculateDiscountedPrices(entity);

        return entity;
    }

    /**
     * Update entity from UpdatePlanRequest
     */
    public void updateEntity(SubscriptionPlan entity, UpdatePlanRequest request) {
        if (entity == null || request == null) {
            return;
        }

        if (request.getName() != null) entity.setName(request.getName());
        if (request.getDescription() != null) entity.setDescription(request.getDescription());
        if (request.getMonthlyPrice() != null) entity.setMonthlyPrice(request.getMonthlyPrice());
        if (request.getYearlyPrice() != null) entity.setYearlyPrice(request.getYearlyPrice());
        if (request.getDiscountPercentage() != null) entity.setDiscountPercentage(request.getDiscountPercentage());
        if (request.getTotalCredits() != null) entity.setTotalCredits(request.getTotalCredits());
        if (request.getDailyCreditLimit() != null) entity.setDailyCreditLimit(request.getDailyCreditLimit());
        if (request.getMonthlyCreditLimit() != null) entity.setMonthlyCreditLimit(request.getMonthlyCreditLimit());
        if (request.getDurationMonths() != null) entity.setDurationMonths(request.getDurationMonths());
        if (request.getValidityDays() != null) entity.setValidityDays(request.getValidityDays());
        if (request.getMaxProfileViews() != null) entity.setMaxProfileViews(request.getMaxProfileViews());
        if (request.getMaxContactReveals() != null) entity.setMaxContactReveals(request.getMaxContactReveals());
        if (request.getMaxInterestsPerMonth() != null) entity.setMaxInterestsPerMonth(request.getMaxInterestsPerMonth());
        
        // Update feature flags
        if (request.getUnlimitedProfileViews() != null) entity.setUnlimitedProfileViews(request.getUnlimitedProfileViews());
        if (request.getUnlimitedContactReveals() != null) entity.setUnlimitedContactReveals(request.getUnlimitedContactReveals());
        if (request.getPrioritySupport() != null) entity.setPrioritySupport(request.getPrioritySupport());
        if (request.getVideoCalling() != null) entity.setVideoCalling(request.getVideoCalling());
        if (request.getAdvancedSearch() != null) entity.setAdvancedSearch(request.getAdvancedSearch());
        if (request.getProfileHighlighting() != null) entity.setProfileHighlighting(request.getProfileHighlighting());
        if (request.getMatchmakerAssistance() != null) entity.setMatchmakerAssistance(request.getMatchmakerAssistance());
        if (request.getHoroscopeMatching() != null) entity.setHoroscopeMatching(request.getHoroscopeMatching());
        if (request.getVerifiedBadge() != null) entity.setVerifiedBadge(request.getVerifiedBadge());
        if (request.getPrivacyControls() != null) entity.setPrivacyControls(request.getPrivacyControls());
        if (request.getChatMessaging() != null) entity.setChatMessaging(request.getChatMessaging());
        if (request.getPhotoGallery() != null) entity.setPhotoGallery(request.getPhotoGallery());
        if (request.getBackgroundVerification() != null) entity.setBackgroundVerification(request.getBackgroundVerification());
        if (request.getExclusiveProfiles() != null) entity.setExclusiveProfiles(request.getExclusiveProfiles());
        if (request.getVipConcierge() != null) entity.setVipConcierge(request.getVipConcierge());
        if (request.getProfessionalPhotography() != null) entity.setProfessionalPhotography(request.getProfessionalPhotography());
        
        // Update display settings
        if (request.getIsPopular() != null) entity.setIsPopular(request.getIsPopular());
        if (request.getIsRecommended() != null) entity.setIsRecommended(request.getIsRecommended());
        if (request.getDisplayOrder() != null) entity.setDisplayOrder(request.getDisplayOrder());
        if (request.getStatus() != null) entity.setStatus(request.getStatus());

        // Recalculate discounted prices
        calculateDiscountedPrices(entity);
    }

    /**
     * Build monthly pricing DTO
     */
    private SubscriptionPlanDTO.PricingDTO buildMonthlyPricing(SubscriptionPlan entity) {
        BigDecimal price = entity.getMonthlyPrice();
        BigDecimal discountedPrice = entity.getMonthlyDiscountPrice() != null ? 
                entity.getMonthlyDiscountPrice() : price;
        
        return SubscriptionPlanDTO.PricingDTO.builder()
                .price(price)
                .discountedPrice(discountedPrice)
                .discountPercentage(0) // Monthly typically has no discount
                .currency(entity.getCurrency())
                .billedAs("Billed monthly")
                .hasDiscount(false)
                .build();
    }

    /**
     * Build yearly pricing DTO
     */
    private SubscriptionPlanDTO.PricingDTO buildYearlyPricing(SubscriptionPlan entity) {
        BigDecimal price = entity.getYearlyPrice();
        BigDecimal discountedPrice = entity.getYearlyDiscountPrice() != null ? 
                entity.getYearlyDiscountPrice() : price;
        Integer discountPercentage = entity.getDiscountPercentage() != null ? 
                entity.getDiscountPercentage() : 0;
        
        return SubscriptionPlanDTO.PricingDTO.builder()
                .price(price)
                .discountedPrice(discountedPrice)
                .discountPercentage(discountPercentage)
                .currency(entity.getCurrency())
                .billedAs("Billed annually")
                .hasDiscount(discountPercentage > 0)
                .build();
    }

    /**
     * Build feature list from entity
     */
    private List<SubscriptionPlanDTO.FeatureDTO> buildFeatureList(SubscriptionPlan entity) {
        List<SubscriptionPlanDTO.FeatureDTO> features = new ArrayList<>();

        // Profile Views
        features.add(SubscriptionPlanDTO.FeatureDTO.builder()
                .name("Profile Views")
                .description(Boolean.TRUE.equals(entity.getUnlimitedProfileViews()) ? 
                        "Unlimited profile views" : 
                        "View up to " + entity.getMaxProfileViews() + " profiles")
                .included(true)
                .value(Boolean.TRUE.equals(entity.getUnlimitedProfileViews()) ? 
                        "Unlimited" : 
                        entity.getMaxProfileViews() + " per month")
                .icon("eye")
                .build());

        // Contact Reveals
        if (entity.getMaxContactReveals() != null || Boolean.TRUE.equals(entity.getUnlimitedContactReveals())) {
            features.add(SubscriptionPlanDTO.FeatureDTO.builder()
                    .name("Contact Reveals")
                    .description(Boolean.TRUE.equals(entity.getUnlimitedContactReveals()) ? 
                            "Unlimited contact reveals" : 
                            "Reveal up to " + entity.getMaxContactReveals() + " contacts")
                    .included(true)
                    .value(Boolean.TRUE.equals(entity.getUnlimitedContactReveals()) ? 
                            "Unlimited" : 
                            entity.getMaxContactReveals() + " per month")
                    .icon("phone")
                    .build());
        }

        // Video Calling
        if (Boolean.TRUE.equals(entity.getVideoCalling())) {
            features.add(SubscriptionPlanDTO.FeatureDTO.builder()
                    .name("Video Calling")
                    .description("Video calling feature")
                    .included(true)
                    .value("Enabled")
                    .icon("video")
                    .build());
        }

        // Advanced Search
        if (Boolean.TRUE.equals(entity.getAdvancedSearch())) {
            features.add(SubscriptionPlanDTO.FeatureDTO.builder()
                    .name("Advanced Search")
                    .description("Advanced search filters")
                    .included(true)
                    .value("Enabled")
                    .icon("search")
                    .build());
        }

        // Priority Support
        if (Boolean.TRUE.equals(entity.getPrioritySupport())) {
            features.add(SubscriptionPlanDTO.FeatureDTO.builder()
                    .name("Priority Support")
                    .description("24/7 priority customer support")
                    .included(true)
                    .value("24/7")
                    .icon("support")
                    .build());
        }

        // Profile Highlighting
        if (Boolean.TRUE.equals(entity.getProfileHighlighting())) {
            features.add(SubscriptionPlanDTO.FeatureDTO.builder()
                    .name("Profile Highlighting")
                    .description("Your profile gets highlighted")
                    .included(true)
                    .value("Enabled")
                    .icon("star")
                    .build());
        }

        // Matchmaker Assistance
        if (Boolean.TRUE.equals(entity.getMatchmakerAssistance())) {
            features.add(SubscriptionPlanDTO.FeatureDTO.builder()
                    .name("Personal Matchmaker")
                    .description("Dedicated matchmaker assistance")
                    .included(true)
                    .value("Enabled")
                    .icon("user-tie")
                    .build());
        }

        // Horoscope Matching
        if (Boolean.TRUE.equals(entity.getHoroscopeMatching())) {
            features.add(SubscriptionPlanDTO.FeatureDTO.builder()
                    .name("Horoscope Matching")
                    .description("Astrological compatibility")
                    .included(true)
                    .value("Enabled")
                    .icon("moon")
                    .build());
        }

        // Verified Badge
        if (Boolean.TRUE.equals(entity.getVerifiedBadge())) {
            features.add(SubscriptionPlanDTO.FeatureDTO.builder()
                    .name("Verified Badge")
                    .description("Get verified badge on profile")
                    .included(true)
                    .value("Enabled")
                    .icon("badge-check")
                    .build());
        }

        // Background Verification
        if (Boolean.TRUE.equals(entity.getBackgroundVerification())) {
            features.add(SubscriptionPlanDTO.FeatureDTO.builder()
                    .name("Background Verification")
                    .description("Professional background check")
                    .included(true)
                    .value("Enabled")
                    .icon("shield-check")
                    .build());
        }

        // Exclusive Profiles
        if (Boolean.TRUE.equals(entity.getExclusiveProfiles())) {
            features.add(SubscriptionPlanDTO.FeatureDTO.builder()
                    .name("Exclusive Profiles")
                    .description("Access to elite profiles")
                    .included(true)
                    .value("Enabled")
                    .icon("crown")
                    .build());
        }

        // VIP Concierge
        if (Boolean.TRUE.equals(entity.getVipConcierge())) {
            features.add(SubscriptionPlanDTO.FeatureDTO.builder()
                    .name("VIP Concierge")
                    .description("Personal concierge service")
                    .included(true)
                    .value("Enabled")
                    .icon("concierge-bell")
                    .build());
        }

        // Professional Photography
        if (Boolean.TRUE.equals(entity.getProfessionalPhotography())) {
            features.add(SubscriptionPlanDTO.FeatureDTO.builder()
                    .name("Professional Photography")
                    .description("Free professional photo shoot")
                    .included(true)
                    .value("Enabled")
                    .icon("camera")
                    .build());
        }

        return features;
    }

    /**
     * Calculate and set discounted prices
     */
    private void calculateDiscountedPrices(SubscriptionPlan entity) {
        // Monthly discount (usually 0)
        entity.setMonthlyDiscountPrice(entity.getMonthlyPrice());

        // Yearly discount
        if (entity.getYearlyPrice() != null && entity.getDiscountPercentage() != null && entity.getDiscountPercentage() > 0) {
            BigDecimal discount = BigDecimal.valueOf(100 - entity.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP);
            entity.setYearlyDiscountPrice(
                    entity.getYearlyPrice().multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP)
            );
        } else {
            entity.setYearlyDiscountPrice(entity.getYearlyPrice());
        }
    }
}
