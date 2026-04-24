package com.spring.jwt.subscription.controller;

import com.spring.jwt.subscription.dto.PurchaseSubscriptionRequest;
import com.spring.jwt.subscription.dto.UserCreditsDTO;
import com.spring.jwt.subscription.dto.UserSubscriptionDTO;
import com.spring.jwt.subscription.service.UserSubscriptionService;
import com.spring.jwt.utils.ApiResponse;
import com.spring.jwt.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for user subscription operations.
 * Handles subscription purchases and management for authenticated users.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Subscriptions", description = "User subscription management APIs")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('USER')")
public class UserSubscriptionController {

    private final UserSubscriptionService subscriptionService;

    @PostMapping("/purchase")
    @Operation(
        summary = "Purchase a subscription",
        description = "Purchase a new subscription plan. User must not have an active subscription."
    )
    public ResponseEntity<ApiResponse<UserSubscriptionDTO>> purchaseSubscription(
            @Valid @RequestBody PurchaseSubscriptionRequest request) {
        
        Integer userId = SecurityUtil.getCurrentUserId();
        log.info("POST /api/subscriptions/purchase - User {} purchasing subscription", userId);
        
        UserSubscriptionDTO subscription = subscriptionService.purchaseSubscription(userId, request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Subscription purchased successfully",
                subscription
        ));
    }

    @GetMapping("/my-subscription")
    @Operation(
        summary = "Get current subscription",
        description = "Retrieve the current active subscription for the authenticated user"
    )
    public ResponseEntity<ApiResponse<UserSubscriptionDTO>> getCurrentSubscription() {
        Integer userId = SecurityUtil.getCurrentUserId();
        log.info("GET /api/subscriptions/my-subscription - User {} fetching subscription", userId);
        
        UserSubscriptionDTO subscription = subscriptionService.getCurrentSubscription(userId);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Subscription retrieved successfully",
                subscription
        ));
    }

    @GetMapping("/history")
    @Operation(
        summary = "Get subscription history",
        description = "Retrieve all past and current subscriptions for the authenticated user"
    )
    public ResponseEntity<ApiResponse<List<UserSubscriptionDTO>>> getSubscriptionHistory() {
        Integer userId = SecurityUtil.getCurrentUserId();
        log.info("GET /api/subscriptions/history - User {} fetching history", userId);
        
        List<UserSubscriptionDTO> history = subscriptionService.getSubscriptionHistory(userId);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Subscription history retrieved successfully",
                history
        ));
    }

    @DeleteMapping("/cancel")
    @Operation(
        summary = "Cancel subscription",
        description = "Cancel the current active subscription. Auto-renewal will be disabled."
    )
    public ResponseEntity<ApiResponse<Void>> cancelSubscription() {
        Integer userId = SecurityUtil.getCurrentUserId();
        log.info("DELETE /api/subscriptions/cancel - User {} cancelling subscription", userId);
        
        subscriptionService.cancelSubscription(userId);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Subscription cancelled successfully",
                null
        ));
    }

    @GetMapping("/credits/remaining")
    @Operation(
        summary = "Get remaining credits",
        description = "Get the number of remaining credits for the authenticated user"
    )
    public ResponseEntity<ApiResponse<Integer>> getRemainingCredits() {
        Integer userId = SecurityUtil.getCurrentUserId();
        log.info("GET /api/subscriptions/credits/remaining - User {} checking credits", userId);
        
        int credits = subscriptionService.getRemainingCredits(userId);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Remaining credits retrieved successfully",
                credits
        ));
    }

    @GetMapping("/credits")
    @Operation(
        summary = "Get detailed credit information",
        description = "Get comprehensive credit information including usage statistics, limits, and warnings"
    )
    public ResponseEntity<ApiResponse<UserCreditsDTO>> getUserCredits() {
        Integer userId = SecurityUtil.getCurrentUserId();
        log.info("GET /api/subscriptions/credits - User {} fetching credit details", userId);
        
        UserCreditsDTO credits = subscriptionService.getUserCredits(userId);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Credit information retrieved successfully",
                credits
        ));
    }

    @GetMapping("/can-perform/{actionType}")
    @Operation(
        summary = "Check if action can be performed",
        description = "Check if user can perform a specific action (PROFILE_VIEW, CONTACT_REVEAL, SEND_INTEREST, etc.)"
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> canPerformAction(@PathVariable String actionType) {
        Integer userId = SecurityUtil.getCurrentUserId();
        log.info("GET /api/subscriptions/can-perform/{} - User {} checking permission", actionType, userId);
        
        boolean canPerform = subscriptionService.canPerformActionWithCheck(userId, actionType);
        
        Map<String, Object> response = Map.of(
                "canPerform", canPerform,
                "actionType", actionType.toUpperCase()
        );
        
        return ResponseEntity.ok(ApiResponse.success(
                "Permission check completed",
                response
        ));
    }
}
