package com.spring.jwt.subscription.controller;

import com.spring.jwt.subscription.dto.AdminPurchaseSubscriptionRequest;
import com.spring.jwt.subscription.dto.SubscriptionReceiptDTO;
import com.spring.jwt.subscription.dto.UserSubscriptionDTO;
import com.spring.jwt.subscription.service.UserSubscriptionService;
import com.spring.jwt.utils.ApiResponse;
import com.spring.jwt.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

/**
 * REST Controller for admin user subscription management.
 * Handles admin operations on user subscriptions.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@RestController
@RequestMapping("/api/admin/user-subscriptions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin - User Subscriptions", description = "Admin APIs for managing user subscriptions")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserSubscriptionController
{

    private final UserSubscriptionService subscriptionService;

    @PostMapping("/purchase-for-user")
    @Operation(
        summary = "Admin purchase subscription for user (Offline Payment)",
        description = "Admin can purchase subscription for any user with offline payment. Transaction ID is auto-generated. Returns receipt data."
    )
    public ResponseEntity<ApiResponse<SubscriptionReceiptDTO>> adminPurchaseSubscription(
            @Valid @RequestBody AdminPurchaseSubscriptionRequest request)
    {
        
        String adminEmail = SecurityUtil.getCurrentUserEmail();
        log.info("POST /api/admin/user-subscriptions/purchase-for-user - Admin {} purchasing for user {}", 
                adminEmail, request.getUserId());
        
        SubscriptionReceiptDTO receipt = subscriptionService.adminPurchaseSubscription(request, adminEmail);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Subscription purchased successfully. Receipt generated.",
                receipt
        ));
    }

    @GetMapping
    @Operation(
        summary = "Get all user subscriptions",
        description = "Retrieve all user subscriptions (Admin only)"
    )
    public ResponseEntity<ApiResponse<List<UserSubscriptionDTO>>> getAllSubscriptions()
    {
        log.info("GET /api/admin/user-subscriptions - Fetching all subscriptions");
        
        List<UserSubscriptionDTO> subscriptions = subscriptionService.getAllSubscriptions();
        
        return ResponseEntity.ok(ApiResponse.success(
                "User subscriptions retrieved successfully",
                subscriptions
        ));
    }

    @GetMapping("/user/{userId}")
    @Operation(
        summary = "Get user's subscription",
        description = "Retrieve current active subscription for a specific user"
    )
    public ResponseEntity<ApiResponse<UserSubscriptionDTO>> getUserSubscription(
            @Parameter(description = "User ID", required = true)
            @PathVariable Integer userId)
    {
        
        log.info("GET /api/admin/user-subscriptions/user/{} - Fetching subscription", userId);
        
        UserSubscriptionDTO subscription = subscriptionService.getCurrentSubscription(userId);
        
        return ResponseEntity.ok(ApiResponse.success(
                "User subscription retrieved successfully",
                subscription
        ));
    }

    @GetMapping("/user/{userId}/history")
    @Operation(
        summary = "Get user's subscription history",
        description = "Retrieve all subscriptions for a specific user"
    )
    public ResponseEntity<ApiResponse<List<UserSubscriptionDTO>>> getUserSubscriptionHistory(
            @Parameter(description = "User ID", required = true)
            @PathVariable Integer userId)
    {
        
        log.info("GET /api/admin/user-subscriptions/user/{}/history - Fetching history", userId);
        
        List<UserSubscriptionDTO> history = subscriptionService.getSubscriptionHistory(userId);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Subscription history retrieved successfully",
                history
        ));
    }

    @PostMapping("/user/{userId}/extend")
    @Operation(
        summary = "Extend user subscription",
        description = "Extend a user's subscription by specified number of days"
    )
    public ResponseEntity<ApiResponse<UserSubscriptionDTO>> extendSubscription(
            @Parameter(description = "User ID", required = true)
            @PathVariable Integer userId,
            @Parameter(description = "Number of days to extend", required = true)
            @RequestParam int days)
    {
        
        log.info("POST /api/admin/user-subscriptions/user/{}/extend - Extending by {} days", userId, days);
        
        UserSubscriptionDTO subscription = subscriptionService.extendSubscription(userId, days);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Subscription extended successfully",
                subscription
        ));
    }

    @PostMapping("/user/{userId}/credits/add")
    @Operation(
        summary = "Add credits to user",
        description = "Add credits to a user's subscription"
    )
    public ResponseEntity<ApiResponse<Void>> addCredits(
            @Parameter(description = "User ID", required = true)
            @PathVariable Integer userId,
            @Parameter(description = "Number of credits to add", required = true)
            @RequestParam int credits)
    {
        
        log.info("POST /api/admin/user-subscriptions/user/{}/credits/add - Adding {} credits", userId, credits);
        
        subscriptionService.addCredits(userId, credits);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Credits added successfully",
                null
        ));
    }

    @PostMapping("/user/{userId}/suspend")
    @Operation(
        summary = "Suspend user subscription",
        description = "Suspend a user's active subscription"
    )
    public ResponseEntity<ApiResponse<Void>> suspendSubscription(
            @Parameter(description = "User ID", required = true)
            @PathVariable Integer userId)
    {
        
        log.info("POST /api/admin/user-subscriptions/user/{}/suspend - Suspending subscription", userId);
        
        subscriptionService.suspendSubscription(userId);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Subscription suspended successfully",
                null
        ));
    }

    @PostMapping("/user/{userId}/reactivate")
    @Operation(
        summary = "Reactivate user subscription",
        description = "Reactivate a suspended subscription"
    )
    public ResponseEntity<ApiResponse<UserSubscriptionDTO>> reactivateSubscription(
            @Parameter(description = "User ID", required = true)
            @PathVariable Integer userId)
    {
        
        log.info("POST /api/admin/user-subscriptions/user/{}/reactivate - Reactivating subscription", userId);
        
        UserSubscriptionDTO subscription = subscriptionService.reactivateSubscription(userId);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Subscription reactivated successfully",
                subscription
        ));
    }

    @DeleteMapping("/user/{userId}/cancel")
    @Operation(
        summary = "Cancel user subscription",
        description = "Cancel a user's active subscription (Admin action)"
    )
    public ResponseEntity<ApiResponse<Void>> cancelSubscription(
            @Parameter(description = "User ID", required = true)
            @PathVariable Integer userId)
    {
        
        log.info("DELETE /api/admin/user-subscriptions/user/{}/cancel - Cancelling subscription", userId);
        
        subscriptionService.cancelSubscription(userId);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Subscription cancelled successfully",
                null
        ));
    }

    @GetMapping("/statistics")
    @Operation(
        summary = "Get subscription statistics",
        description = "Retrieve statistics about user subscriptions"
    )
    public ResponseEntity<ApiResponse<UserSubscriptionService.SubscriptionStatistics>> getStatistics()
    {
        log.info("GET /api/admin/user-subscriptions/statistics - Fetching statistics");
        
        UserSubscriptionService.SubscriptionStatistics stats = subscriptionService.getStatistics();
        
        return ResponseEntity.ok(ApiResponse.success(
                "Statistics retrieved successfully",
                stats
        ));
    }
}
