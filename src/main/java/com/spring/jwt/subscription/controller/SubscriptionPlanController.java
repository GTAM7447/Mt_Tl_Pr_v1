package com.spring.jwt.subscription.controller;

import com.spring.jwt.subscription.dto.SubscriptionPlanDTO;
import com.spring.jwt.subscription.service.SubscriptionPlanService;
import com.spring.jwt.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for public subscription plan operations.
 * Handles user-facing subscription plan endpoints.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@RestController
@RequestMapping("/api/subscription-plans")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Subscription Plans", description = "Public subscription plan APIs for users")
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    @GetMapping
    @Operation(
        summary = "Get all active subscription plans",
        description = "Retrieve all active subscription plans with pricing and features. No authentication required."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved subscription plans",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<List<SubscriptionPlanDTO>>> getAllActivePlans() {
        log.info("GET /api/subscription-plans - Fetching all active plans");
        
        List<SubscriptionPlanDTO> plans = subscriptionPlanService.getAllActivePlans();
        
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
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved subscription plan"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Subscription plan not found"
        )
    })
    public ResponseEntity<ApiResponse<SubscriptionPlanDTO>> getPlanById(
            @Parameter(description = "Subscription plan ID", required = true, example = "1")
            @PathVariable Integer planId) {
        
        log.info("GET /api/subscription-plans/{} - Fetching plan details", planId);
        
        SubscriptionPlanDTO plan = subscriptionPlanService.getPlanById(planId);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Subscription plan retrieved successfully",
                plan
        ));
    }

    @GetMapping("/code/{planCode}")
    @Operation(
        summary = "Get subscription plan by code",
        description = "Retrieve subscription plan by its unique code (e.g., PREMIUM, BASIC, ELITE, PLATINUM)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved subscription plan"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Subscription plan not found"
        )
    })
    public ResponseEntity<ApiResponse<SubscriptionPlanDTO>> getPlanByCode(
            @Parameter(description = "Plan code", required = true, example = "PREMIUM")
            @PathVariable String planCode) {
        
        log.info("GET /api/subscription-plans/code/{} - Fetching plan by code", planCode);
        
        SubscriptionPlanDTO plan = subscriptionPlanService.getPlanByCode(planCode);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Subscription plan retrieved successfully",
                plan
        ));
    }

    @GetMapping("/popular")
    @Operation(
        summary = "Get popular subscription plans",
        description = "Retrieve plans marked as popular, typically displayed prominently on the pricing page"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved popular plans"
        )
    })
    public ResponseEntity<ApiResponse<List<SubscriptionPlanDTO>>> getPopularPlans() {
        log.info("GET /api/subscription-plans/popular - Fetching popular plans");
        
        List<SubscriptionPlanDTO> plans = subscriptionPlanService.getPopularPlans();
        
        return ResponseEntity.ok(ApiResponse.success(
                "Popular plans retrieved successfully",
                plans
        ));
    }

    @GetMapping("/recommended")
    @Operation(
        summary = "Get recommended subscription plans",
        description = "Retrieve plans marked as recommended for users"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved recommended plans"
        )
    })
    public ResponseEntity<ApiResponse<List<SubscriptionPlanDTO>>> getRecommendedPlans() {
        log.info("GET /api/subscription-plans/recommended - Fetching recommended plans");
        
        List<SubscriptionPlanDTO> plans = subscriptionPlanService.getRecommendedPlans();
        
        return ResponseEntity.ok(ApiResponse.success(
                "Recommended plans retrieved successfully",
                plans
        ));
    }
}
