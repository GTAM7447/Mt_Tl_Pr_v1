package com.spring.jwt.subscription.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for purchasing a subscription.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Purchase Subscription Request")
public class PurchaseSubscriptionRequest {

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
    @Schema(description = "Amount paid", example = "39.00", required = true)
    private BigDecimal amountPaid;

    @NotBlank(message = "Payment method is required")
    @Schema(description = "Payment method", example = "CREDIT_CARD", required = true)
    private String paymentMethod;

    @NotBlank(message = "Transaction ID is required")
    @Size(min = 5, max = 100, message = "Transaction ID must be between 5 and 100 characters")
    @Schema(description = "Transaction ID from payment gateway", example = "TXN123456789", required = true)
    private String transactionId;

    @Schema(description = "Enable auto renewal", example = "false")
    @Builder.Default
    private Boolean autoRenewal = false;
}
