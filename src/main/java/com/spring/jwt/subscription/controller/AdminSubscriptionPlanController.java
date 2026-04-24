package com.spring.jwt.subscription.controller;

import com.spring.jwt.subscription.dto.CreatePlanRequest;
import com.spring.jwt.subscription.dto.SubscriptionPlanDTO;
import com.spring.jwt.subscription.dto.UpdatePlanRequest;
import com.spring.jwt.subscription.service.SubscriptionPlanService;
import com.spring.jwt.utils.ApiResponse;
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
 * REST Controller for admin subscription plan management.
 * Handles CRUD operations for subscription plans.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@RestController
@RequestMapping("/api/admin/subscription-plans")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin - Subscription Plans", description = "Admin APIs for managing subscription plans")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSubscriptionPlanController
{

    private final SubscriptionPlanService subscriptionPlanService;

    @PostMapping
    @Operation(
        summary = "Create subscription plan",
        description = "Create a new subscription plan with pricing and features"
    )
    public ResponseEntity<ApiResponse<SubscriptionPlanDTO>> createPlan(
            @Valid @RequestBody CreatePlanRequest request)
    {
        
        log.info("POST /api/admin/subscription-plans - Creating new plan: {}", request.getPlanCode());
        
        SubscriptionPlanDTO plan = subscriptionPlanService.createPlan(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Subscription plan created successfully",
                plan
        ));
    }

    @GetMapping
    @Operation(
        summary = "Get all subscription plans",
        description = "Retrieve all subscription plans including inactive ones (Admin only)"
    )
    public ResponseEntity<ApiResponse<List<SubscriptionPlanDTO>>> getAllPlans()
    {
        log.info("GET /api/admin/subscription-plans - Fetching all plans");
        
        List<SubscriptionPlanDTO> plans = subscriptionPlanService.getAllPlansForAdmin();
        
        return ResponseEntity.ok(ApiResponse.success(
                "Subscription plans retrieved successfully",
                plans
        ));
    }

    @GetMapping("/{planId}")
    @Operation(
        summary = "Get subscription plan by ID",
        description = "Retrieve detailed information about a specific subscription plan"
    )
    public ResponseEntity<ApiResponse<SubscriptionPlanDTO>> getPlanById(
            @Parameter(description = "Plan ID", required = true)
            @PathVariable Integer planId)
    {
        
        log.info("GET /api/admin/subscription-plans/{} - Fetching plan", planId);
        
        SubscriptionPlanDTO plan = subscriptionPlanService.getPlanById(planId);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Subscription plan retrieved successfully",
                plan
        ));
    }

    @PutMapping("/{planId}")
    @Operation(
        summary = "Update subscription plan",
        description = "Update an existing subscription plan. All fields are optional for partial updates."
    )
    public ResponseEntity<ApiResponse<SubscriptionPlanDTO>> updatePlan(
            @Parameter(description = "Plan ID", required = true)
            @PathVariable Integer planId,
            @Valid @RequestBody UpdatePlanRequest request)
    {
        
        log.info("PUT /api/admin/subscription-plans/{} - Updating plan", planId);
        
        SubscriptionPlanDTO plan = subscriptionPlanService.updatePlan(planId, request);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Subscription plan updated successfully",
                plan
        ));
    }

    @PatchMapping("/{planId}/toggle-status")
    @Operation(
        summary = "Toggle plan status",
        description = "Toggle subscription plan status between ACTIVE and INACTIVE"
    )
    public ResponseEntity<ApiResponse<SubscriptionPlanDTO>> togglePlanStatus(
            @Parameter(description = "Plan ID", required = true)
            @PathVariable Integer planId)
    {
        
        log.info("PATCH /api/admin/subscription-plans/{}/toggle-status - Toggling status", planId);
        
        SubscriptionPlanDTO plan = subscriptionPlanService.togglePlanStatus(planId);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Plan status toggled successfully",
                plan
        ));
    }

    @DeleteMapping("/{planId}")
    @Operation(
        summary = "Delete subscription plan",
        description = "Soft delete a subscription plan. Plan will be marked as deleted but not removed from database."
    )
    public ResponseEntity<ApiResponse<Void>> deletePlan(
            @Parameter(description = "Plan ID", required = true)
            @PathVariable Integer planId) {
        
        log.info("DELETE /api/admin/subscription-plans/{} - Deleting plan", planId);
        
        subscriptionPlanService.deletePlan(planId);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Subscription plan deleted successfully",
                null
        ));
    }

    @GetMapping("/statistics")
    @Operation(
        summary = "Get plan statistics",
        description = "Retrieve statistics about subscription plans"
    )
    public ResponseEntity<ApiResponse<SubscriptionPlanService.PlanStatistics>> getPlanStatistics()
    {
        log.info("GET /api/admin/subscription-plans/statistics - Fetching statistics");
        
        SubscriptionPlanService.PlanStatistics stats = subscriptionPlanService.getPlanStatistics();
        
        return ResponseEntity.ok(ApiResponse.success(
                "Statistics retrieved successfully",
                stats
        ));
    }
}
