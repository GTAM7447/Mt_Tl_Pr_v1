package com.spring.jwt.subscription.entity;

import com.spring.jwt.entity.base.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entity representing a subscription plan/package.
 * Supports admin-customizable features, pricing, and limits.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Entity
@Table(name = "subscription_plans", indexes = {
    @Index(name = "idx_plan_code", columnList = "plan_code"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_display_order", columnList = "display_order")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Integer subscriptionId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "plan_code", unique = true, nullable = false, length = 50)
    private String planCode; // BASIC, PREMIUM, ELITE, PLATINUM

    // ========== PRICING ==========
    @Column(name = "monthly_price", precision = 10, scale = 2)
    private BigDecimal monthlyPrice;

    @Column(name = "yearly_price", precision = 10, scale = 2)
    private BigDecimal yearlyPrice;

    @Column(name = "monthly_discount_price", precision = 10, scale = 2)
    private BigDecimal monthlyDiscountPrice;

    @Column(name = "yearly_discount_price", precision = 10, scale = 2)
    private BigDecimal yearlyDiscountPrice;

    @Column(name = "discount_percentage")
    private Integer discountPercentage;

    @Column(name = "currency", length = 10)
    @Builder.Default
    private String currency = "INR";

    // ========== CREDITS & LIMITS ==========
    @Column(name = "total_credits")
    private Integer totalCredits;

    @Column(name = "daily_credit_limit")
    private Integer dailyCreditLimit;

    @Column(name = "monthly_credit_limit")
    private Integer monthlyCreditLimit;

    @Column(name = "duration_months")
    private Integer durationMonths;

    @Column(name = "validity_days")
    private Integer validityDays;

    // ========== USAGE LIMITS ==========
    @Column(name = "max_profile_views")
    private Integer maxProfileViews;

    @Column(name = "max_contact_reveals")
    private Integer maxContactReveals;

    @Column(name = "max_interests_per_month")
    private Integer maxInterestsPerMonth;

    // ========== FEATURE FLAGS ==========
    @Column(name = "unlimited_profile_views")
    @Builder.Default
    private Boolean unlimitedProfileViews = false;

    @Column(name = "unlimited_contact_reveals")
    @Builder.Default
    private Boolean unlimitedContactReveals = false;

    @Column(name = "priority_support")
    @Builder.Default
    private Boolean prioritySupport = false;

    @Column(name = "video_calling")
    @Builder.Default
    private Boolean videoCalling = false;

    @Column(name = "advanced_search")
    @Builder.Default
    private Boolean advancedSearch = false;

    @Column(name = "profile_highlighting")
    @Builder.Default
    private Boolean profileHighlighting = false;

    @Column(name = "matchmaker_assistance")
    @Builder.Default
    private Boolean matchmakerAssistance = false;

    @Column(name = "horoscope_matching")
    @Builder.Default
    private Boolean horoscopeMatching = false;

    @Column(name = "verified_badge")
    @Builder.Default
    private Boolean verifiedBadge = false;

    @Column(name = "privacy_controls")
    @Builder.Default
    private Boolean privacyControls = false;

    @Column(name = "chat_messaging")
    @Builder.Default
    private Boolean chatMessaging = false;

    @Column(name = "photo_gallery")
    @Builder.Default
    private Boolean photoGallery = false;

    @Column(name = "background_verification")
    @Builder.Default
    private Boolean backgroundVerification = false;

    @Column(name = "exclusive_profiles")
    @Builder.Default
    private Boolean exclusiveProfiles = false;

    @Column(name = "vip_concierge")
    @Builder.Default
    private Boolean vipConcierge = false;

    @Column(name = "professional_photography")
    @Builder.Default
    private Boolean professionalPhotography = false;

    // ========== DISPLAY & STATUS ==========
    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "is_popular")
    @Builder.Default
    private Boolean isPopular = false;

    @Column(name = "is_recommended")
    @Builder.Default
    private Boolean isRecommended = false;

    @Column(name = "display_order")
    private Integer displayOrder;

    // ========== BUSINESS METHODS ==========
    
    /**
     * Calculate yearly price with discount
     */
    public BigDecimal calculateYearlyDiscountedPrice() {
        if (yearlyPrice == null || discountPercentage == null) {
            return yearlyPrice;
        }
        return yearlyPrice.multiply(BigDecimal.valueOf(100 - discountPercentage))
                .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Check if plan is active
     */
    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status);
    }

    /**
     * Check if feature is enabled
     */
    public boolean hasFeature(String featureName) {
        return switch (featureName.toUpperCase()) {
            case "UNLIMITED_PROFILE_VIEWS" -> Boolean.TRUE.equals(unlimitedProfileViews);
            case "UNLIMITED_CONTACT_REVEALS" -> Boolean.TRUE.equals(unlimitedContactReveals);
            case "PRIORITY_SUPPORT" -> Boolean.TRUE.equals(prioritySupport);
            case "VIDEO_CALLING" -> Boolean.TRUE.equals(videoCalling);
            case "ADVANCED_SEARCH" -> Boolean.TRUE.equals(advancedSearch);
            case "PROFILE_HIGHLIGHTING" -> Boolean.TRUE.equals(profileHighlighting);
            case "MATCHMAKER_ASSISTANCE" -> Boolean.TRUE.equals(matchmakerAssistance);
            case "HOROSCOPE_MATCHING" -> Boolean.TRUE.equals(horoscopeMatching);
            case "VERIFIED_BADGE" -> Boolean.TRUE.equals(verifiedBadge);
            case "PRIVACY_CONTROLS" -> Boolean.TRUE.equals(privacyControls);
            case "CHAT_MESSAGING" -> Boolean.TRUE.equals(chatMessaging);
            case "PHOTO_GALLERY" -> Boolean.TRUE.equals(photoGallery);
            case "BACKGROUND_VERIFICATION" -> Boolean.TRUE.equals(backgroundVerification);
            case "EXCLUSIVE_PROFILES" -> Boolean.TRUE.equals(exclusiveProfiles);
            case "VIP_CONCIERGE" -> Boolean.TRUE.equals(vipConcierge);
            case "PROFESSIONAL_PHOTOGRAPHY" -> Boolean.TRUE.equals(professionalPhotography);
            default -> false;
        };
    }
}
