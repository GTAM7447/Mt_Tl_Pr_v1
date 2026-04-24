package com.spring.jwt.subscription.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Subscription Receipt/Invoice.
 * Contains all data needed for receipt generation.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Subscription Receipt/Invoice Data")
public class SubscriptionReceiptDTO {

    @Schema(description = "Receipt number (auto-generated)", example = "RCP-2026-0001234")
    private String receiptNumber;

    @Schema(description = "Transaction ID (auto-generated)", example = "TXN-20260122-ABC123")
    private String transactionId;

    @Schema(description = "Receipt date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receiptDate;

    @Schema(description = "Receipt status", example = "PAID")
    private String status; // PAID, PENDING, CANCELLED

    @Schema(description = "Customer details")
    private CustomerInfo customer;

    @Schema(description = "Subscription details")
    private SubscriptionInfo subscription;

    @Schema(description = "Payment details")
    private PaymentInfo payment;

    @Schema(description = "Pricing breakdown")
    private PricingBreakdown pricing;

    @Schema(description = "Company/Platform details")
    private CompanyInfo company;

    @Schema(description = "Terms and conditions")
    private List<String> termsAndConditions;

    @Schema(description = "Additional notes")
    private String notes;

    @Schema(description = "Admin remarks (internal)")
    private String adminRemarks;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Customer Information")
    public static class CustomerInfo {
        @Schema(description = "Customer ID", example = "123")
        private Integer userId;

        @Schema(description = "Customer name", example = "John Doe")
        private String name;

        @Schema(description = "Customer email", example = "john.doe@example.com")
        private String email;

        @Schema(description = "Customer phone", example = "+91-9876543210")
        private String phone;

        @Schema(description = "Customer address")
        private String address;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Subscription Information")
    public static class SubscriptionInfo {
        @Schema(description = "Subscription ID", example = "456")
        private Integer subscriptionId;

        @Schema(description = "Plan name", example = "Premium")
        private String planName;

        @Schema(description = "Plan code", example = "PREMIUM")
        private String planCode;

        @Schema(description = "Billing period", example = "MONTHLY")
        private String billingPeriod;

        @Schema(description = "Start date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime startDate;

        @Schema(description = "End date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDateTime endDate;

        @Schema(description = "Validity in days", example = "30")
        private Long validityDays;

        @Schema(description = "Credits allocated", example = "500")
        private Integer creditsAllocated;

        @Schema(description = "Features included")
        private List<String> features;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Payment Information")
    public static class PaymentInfo {
        @Schema(description = "Payment mode", example = "OFFLINE")
        private String paymentMode;

        @Schema(description = "Payment method", example = "CASH")
        private String paymentMethod;

        @Schema(description = "Payment date")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime paymentDate;

        @Schema(description = "External reference number", example = "CHQ123456")
        private String externalReference;

        @Schema(description = "Payment notes")
        private String paymentNotes;

        @Schema(description = "Processed by (admin name)", example = "Admin User")
        private String processedBy;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Pricing Breakdown")
    public static class PricingBreakdown {
        @Schema(description = "Base price", example = "39.00")
        private BigDecimal basePrice;

        @Schema(description = "Discount amount", example = "0.00")
        private BigDecimal discountAmount;

        @Schema(description = "Discount percentage", example = "0")
        private Integer discountPercentage;

        @Schema(description = "Tax amount (GST)", example = "7.02")
        private BigDecimal taxAmount;

        @Schema(description = "Tax percentage", example = "18")
        private Integer taxPercentage;

        @Schema(description = "Subtotal", example = "39.00")
        private BigDecimal subtotal;

        @Schema(description = "Total amount", example = "46.02")
        private BigDecimal totalAmount;

        @Schema(description = "Amount paid", example = "46.02")
        private BigDecimal amountPaid;

        @Schema(description = "Currency", example = "INR")
        private String currency;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Company Information")
    public static class CompanyInfo {
        @Schema(description = "Company name", example = "Matrimony Platform Pvt Ltd")
        private String name;

        @Schema(description = "Company address")
        private String address;

        @Schema(description = "Company phone", example = "+91-1234567890")
        private String phone;

        @Schema(description = "Company email", example = "support@matrimony.com")
        private String email;

        @Schema(description = "Company website", example = "www.matrimony.com")
        private String website;

        @Schema(description = "GST number", example = "29ABCDE1234F1Z5")
        private String gstNumber;

        @Schema(description = "PAN number", example = "ABCDE1234F")
        private String panNumber;
    }
}
