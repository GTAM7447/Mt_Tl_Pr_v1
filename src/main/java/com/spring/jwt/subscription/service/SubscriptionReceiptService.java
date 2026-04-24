package com.spring.jwt.subscription.service;

import com.spring.jwt.entity.User;
import com.spring.jwt.entity.UserProfile;
import com.spring.jwt.repository.UserProfileRepository;
import com.spring.jwt.subscription.dto.SubscriptionReceiptDTO;
import com.spring.jwt.subscription.entity.SubscriptionPlan;
import com.spring.jwt.subscription.entity.UserSubscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for generating subscription receipts.
 * Creates complete receipt data for invoice/receipt generation.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionReceiptService
{

    private final TransactionIdGenerator transactionIdGenerator;
    
    private final UserProfileRepository userProfileRepository;

    @Value("${app.company.name:Matrimony Platform Pvt Ltd}")
    private String companyName;

    @Value("${app.company.address:123 Business Street, City, State - 123456}")
    private String companyAddress;

    @Value("${app.company.phone:+91-1234567890}")
    private String companyPhone;

    @Value("${app.company.email:support@matrimony.com}")
    private String companyEmail;

    @Value("${app.company.website:www.matrimony.com}")
    private String companyWebsite;

    @Value("${app.company.gst:29ABCDE1234F1Z5}")
    private String companyGst;

    @Value("${app.company.pan:ABCDE1234F}")
    private String companyPan;

    @Value("${app.tax.gst.percentage:18}")
    private Integer gstPercentage;

    /**
     * Generate complete receipt for subscription purchase
     */
    public SubscriptionReceiptDTO generateReceipt(
            UserSubscription subscription,
            String paymentMode,
            String paymentNotes,
            String externalReference,
            String adminRemarks,
            String processedByAdmin) {
        
        log.info("Generating receipt for subscription ID: {}", subscription.getUserSubscriptionId());

        User user = subscription.getUser();
        SubscriptionPlan plan = subscription.getSubscriptionPlan();

        // Generate receipt number
        String receiptNumber = transactionIdGenerator.generateReceiptNumber();

        // Build customer info
        SubscriptionReceiptDTO.CustomerInfo customerInfo = buildCustomerInfo(user);

        // Build subscription info
        SubscriptionReceiptDTO.SubscriptionInfo subscriptionInfo = buildSubscriptionInfo(subscription, plan);

        // Build payment info
        SubscriptionReceiptDTO.PaymentInfo paymentInfo = buildPaymentInfo(
                subscription, paymentMode, paymentNotes, externalReference, processedByAdmin);

        // Build pricing breakdown
        SubscriptionReceiptDTO.PricingBreakdown pricingBreakdown = buildPricingBreakdown(subscription);

        // Build company info
        SubscriptionReceiptDTO.CompanyInfo companyInfo = buildCompanyInfo();

        // Build terms and conditions
        List<String> termsAndConditions = buildTermsAndConditions();

        // Build receipt
        SubscriptionReceiptDTO receipt = SubscriptionReceiptDTO.builder()
                .receiptNumber(receiptNumber)
                .transactionId(subscription.getTransactionId())
                .receiptDate(LocalDateTime.now())
                .status("PAID")
                .customer(customerInfo)
                .subscription(subscriptionInfo)
                .payment(paymentInfo)
                .pricing(pricingBreakdown)
                .company(companyInfo)
                .termsAndConditions(termsAndConditions)
                .notes("Thank you for your subscription!")
                .adminRemarks(adminRemarks)
                .build();

        log.info("Receipt generated successfully: {}", receiptNumber);
        return receipt;
    }

    /**
     * Build customer information.
     * Handles cases where UserProfile may not exist yet.
     * Falls back to User entity data if profile is not created.
     */
    private SubscriptionReceiptDTO.CustomerInfo buildCustomerInfo(User user)
    {
        Optional<UserProfile> profileOpt = userProfileRepository.findByUser_Id(user.getId());
        
        String fullName;
        String address;
        Long phone = user.getMobileNumber();
        
        if (profileOpt.isPresent())
        {

            UserProfile profile = profileOpt.get();
            fullName = profile.getFirstName() + " " + profile.getLastName();
            address = buildUserAddressFromProfile(profile);
            log.debug("Using profile data for user ID: {}", user.getId());
        } else
        {

            fullName = extractNameFromEmail(user.getEmail());
            address = "Address not available (Profile not created)";
            log.warn("UserProfile not found for user ID: {}. Using fallback data from User entity.", user.getId());
        }

        return SubscriptionReceiptDTO.CustomerInfo.builder()
                .userId(user.getId())
                .name(fullName)
                .email(user.getEmail())
                .phone(phone != null ? "+91-" + phone : "Not provided")
                .address(address)
                .build();
    }
    
    /**
     * Extract a display name from email address.
     * Used as fallback when profile doesn't exist.
     */
    private String extractNameFromEmail(String email)
    {
        if (email == null || !email.contains("@"))
        {
            return "User";
        }
        String localPart = email.substring(0, email.indexOf("@"));

        String name = localPart.replace(".", " ").replace("_", " ");
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
    
    /**
     * Build complete address from UserProfile
     */
    private String buildUserAddressFromProfile(UserProfile profile)
    {
        StringBuilder address = new StringBuilder();
        address.append(profile.getAddress());
        address.append(", ").append(profile.getTaluka());
        address.append(", ").append(profile.getDistrict());
        address.append(" - ").append(profile.getPinCode());
        return address.toString();
    }

    /**
     * Build subscription information
     */
    private SubscriptionReceiptDTO.SubscriptionInfo buildSubscriptionInfo(
            UserSubscription subscription, SubscriptionPlan plan)
    {
        
        Long validityDays = null;
        if (subscription.getEndDate() != null)
        {
            validityDays = ChronoUnit.DAYS.between(subscription.getStartDate(), subscription.getEndDate());
        }

        List<String> features = buildFeatureList(plan);

        return SubscriptionReceiptDTO.SubscriptionInfo.builder()
                .subscriptionId(subscription.getUserSubscriptionId())
                .planName(plan.getName())
                .planCode(plan.getPlanCode())
                .billingPeriod(subscription.getBillingPeriod())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .validityDays(validityDays)
                .creditsAllocated(subscription.getAllocatedCredits())
                .features(features)
                .build();
    }

    /**
     * Build payment information
     */
    private SubscriptionReceiptDTO.PaymentInfo buildPaymentInfo(
            UserSubscription subscription,
            String paymentMode,
            String paymentNotes,
            String externalReference,
            String processedBy) {
        
        return SubscriptionReceiptDTO.PaymentInfo.builder()
                .paymentMode(paymentMode)
                .paymentMethod(subscription.getPaymentMethod())
                .paymentDate(subscription.getPaymentDate())
                .externalReference(externalReference)
                .paymentNotes(paymentNotes)
                .processedBy(processedBy)
                .build();
    }

    /**
     * Build pricing breakdown with tax calculation
     */
    private SubscriptionReceiptDTO.PricingBreakdown buildPricingBreakdown(UserSubscription subscription) {
        BigDecimal amountPaid = subscription.getAmountPaid();
        
        // Calculate base price (reverse calculate from amount paid including tax)
        BigDecimal taxMultiplier = BigDecimal.valueOf(100 + gstPercentage).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal basePrice = amountPaid.divide(taxMultiplier, 2, RoundingMode.HALF_UP);
        
        // Calculate tax
        BigDecimal taxAmount = amountPaid.subtract(basePrice);
        
        // For display purposes
        BigDecimal subtotal = basePrice;
        BigDecimal discountAmount = BigDecimal.ZERO;
        Integer discountPercentage = 0;

        return SubscriptionReceiptDTO.PricingBreakdown.builder()
                .basePrice(basePrice)
                .discountAmount(discountAmount)
                .discountPercentage(discountPercentage)
                .taxAmount(taxAmount)
                .taxPercentage(gstPercentage)
                .subtotal(subtotal)
                .totalAmount(amountPaid)
                .amountPaid(amountPaid)
                .currency("INR")
                .build();
    }

    /**
     * Build company information
     */
    private SubscriptionReceiptDTO.CompanyInfo buildCompanyInfo()
    {
        return SubscriptionReceiptDTO.CompanyInfo.builder()
                .name(companyName)
                .address(companyAddress)
                .phone(companyPhone)
                .email(companyEmail)
                .website(companyWebsite)
                .gstNumber(companyGst)
                .panNumber(companyPan)
                .build();
    }

    /**
     * Build feature list from plan
     */
    private List<String> buildFeatureList(SubscriptionPlan plan)
    {
        List<String> features = new ArrayList<>();

        if (Boolean.TRUE.equals(plan.getUnlimitedProfileViews()))
        {
            features.add("Unlimited Profile Views");
        } else if (plan.getMaxProfileViews() != null)
        {
            features.add(plan.getMaxProfileViews() + " Profile Views per month");
        }

        if (Boolean.TRUE.equals(plan.getUnlimitedContactReveals()))
        {
            features.add("Unlimited Contact Reveals");
        } else if (plan.getMaxContactReveals() != null)
        {
            features.add(plan.getMaxContactReveals() + " Contact Reveals per month");
        }

        if (Boolean.TRUE.equals(plan.getVideoCalling()))
        {
            features.add("Video Calling");
        }

        if (Boolean.TRUE.equals(plan.getAdvancedSearch()))
        {
            features.add("Advanced Search Filters");
        }

        if (Boolean.TRUE.equals(plan.getPrioritySupport()))
        {
            features.add("24/7 Priority Support");
        }

        if (Boolean.TRUE.equals(plan.getProfileHighlighting()))
        {
            features.add("Profile Highlighting");
        }

        if (Boolean.TRUE.equals(plan.getMatchmakerAssistance()))
        {
            features.add("Personal Matchmaker");
        }

        if (Boolean.TRUE.equals(plan.getVerifiedBadge()))
        {
            features.add("Verified Badge");
        }

        if (Boolean.TRUE.equals(plan.getBackgroundVerification()))
        {
            features.add("Background Verification");
        }

        if (Boolean.TRUE.equals(plan.getExclusiveProfiles()))
        {
            features.add("Access to Exclusive Profiles");
        }

        if (Boolean.TRUE.equals(plan.getVipConcierge()))
        {
            features.add("VIP Concierge Service");
        }

        if (Boolean.TRUE.equals(plan.getProfessionalPhotography()))
        {
            features.add("Professional Photography Session");
        }

        return features;
    }

    /**
     * Build terms and conditions
     */
    private List<String> buildTermsAndConditions()
    {
        List<String> terms = new ArrayList<>();
        terms.add("1. This subscription is non-refundable and non-transferable.");
        terms.add("2. Subscription will be valid for the period mentioned above.");
        terms.add("3. Credits will be allocated as per the selected plan.");
        terms.add("4. Daily and monthly limits apply as per plan features.");
        terms.add("5. Auto-renewal can be enabled/disabled from account settings.");
        terms.add("6. Company reserves the right to modify features with prior notice.");
        terms.add("7. For support, contact: " + companyEmail);
        return terms;
    }
}
