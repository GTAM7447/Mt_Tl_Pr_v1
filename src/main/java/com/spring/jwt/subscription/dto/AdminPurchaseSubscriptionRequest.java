package com.spring.jwt.subscription.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for admin to purchase subscription for a user (Offline Payment).
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Admin Purchase Subscription Request (Offline Payment)")
public class AdminPurchaseSubscriptionRequest {

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    @Schema(description = "User ID for whom subscription is being purchased", example = "123", required = true)
    private Integer userId;

    @NotNull(message = "Subscription plan ID is required")
    @Positive(message = "Subscription plan ID must be positive")
    @Schema(description = "Subscription plan ID", example = "2", required = true)
    private Integer subscriptionPlanId;

    @NotBlank(message = "Billing period is required")
    @Pattern(regexp = "MONTHLY|YEARLY", message = "Billing period must be MONTHLY or YEARLY")
    @Schema(description = "Billing period", example = "MONTHLY", required = true, allowableValues = {"MONTHLY", "YEARLY"})
    private String billingPeriod;

    @NotNull(message = "Amount paid is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Schema(description = "Amount paid by user", example = "39.00", required = true)
    private BigDecimal amountPaid;

    @NotBlank(message = "Payment mode is required")
    @Pattern(regexp = "OFFLINE|CASH|CHEQUE|BANK_TRANSFER|UPI|CARD|ONLINE", 
             message = "Invalid payment mode")
    @Schema(description = "Payment mode", example = "OFFLINE", required = true, 
            allowableValues = {"OFFLINE", "CASH", "CHEQUE", "BANK_TRANSFER", "UPI", "CARD", "ONLINE"})
    private String paymentMode;

    @Size(max = 500, message = "Payment notes cannot exceed 500 characters")
    @Schema(description = "Additional payment notes/remarks", example = "Paid via cash at office")
    private String paymentNotes;

    @Size(max = 100, message = "Reference number cannot exceed 100 characters")
    @Schema(description = "External reference number (cheque number, bank ref, etc.)", example = "CHQ123456")
    private String externalReferenceNumber;

    @Schema(description = "Enable auto renewal", example = "false")
    @Builder.Default
    private Boolean autoRenewal = false;

    @Size(max = 200, message = "Remarks cannot exceed 200 characters")
    @Schema(description = "Admin remarks", example = "Special discount applied")
    private String adminRemarks;
}
