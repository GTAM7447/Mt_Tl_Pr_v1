package com.spring.jwt.admin;

import com.spring.jwt.admin.dto.AdminCompleteRegistrationRequest;
import com.spring.jwt.admin.dto.AdminCompleteRegistrationResponse;
import com.spring.jwt.admin.service.AdminRegistrationOrchestrationService;
import com.spring.jwt.admin.validation.AdminValidation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Admin Registration Controller - Complete User Registration Orchestration
 * 
 * This controller provides a single endpoint for admins to register a complete user
 * with all profile sections in one operation. It orchestrates the creation across
 * multiple domain services while maintaining transactional integrity.
 * 
 * Design Principles:
 * - Single endpoint for complete registration
 * - Transactional all-or-nothing operation
 * - Delegates to existing domain services (no logic duplication)
 * - Admin-specific validation rules
 * - Comprehensive response with operation results
 * - Backward compatible (doesn't modify existing APIs)
 * 
 * Current Limitations:
 * - Only Education & Profession section is fully supported in orchestration
 * - Other sections should be created using individual admin endpoints after user creation
 * - This is due to most services expecting current user context
 * 
 * Future Enhancements:
 * - Add createForUser methods to all domain services
 * - Support full orchestration of all profile sections
 * - Add bulk user import capability
 * - Add validation preview mode
 */
@RestController
@RequestMapping("/api/v1/admin/registration")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Registration", description = "Admin-driven complete user registration orchestration")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRegistrationController {

    private final AdminRegistrationOrchestrationService orchestrationService;

    /**
     * Register a complete user with all profile sections in a single operation.
     * All operations are wrapped in a single transaction. If user creation fails,
     * nothing is saved. If a profile section fails, user is still created but
     * the response indicates which sections failed.
     *
     * 
     * @param request the complete registration request with user and optional profile sections
     * @return comprehensive response with created entity IDs and profile completion status
     */
    @PostMapping("/complete")
    @Operation(
            summary = "Register complete user (Admin only)",
            description = "Create a user account and optionally all 7 profile sections in a single transactional operation. " +
                    "All profile sections are now fully supported in orchestration!"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = AdminCompleteRegistrationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or validation failure"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User with email or mobile already exists"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - Admin role required"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error during registration"
            )
    })
    public ResponseEntity<AdminCompleteRegistrationResponse> registerCompleteUser(
            @Valid @Validated(AdminValidation.class) @RequestBody AdminCompleteRegistrationRequest request) {
        
        log.info("Admin registration request received for email: {}", request.getEmail());
        
        try {
            AdminCompleteRegistrationResponse response = orchestrationService.registerCompleteUser(request);
            
            log.info("User registration completed successfully. User ID: {}, Completion: {}%",
                    response.getUserId(), response.getCompletionPercentage());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Error during complete user registration for email: {}", request.getEmail(), e);
            throw e;
        }
    }
}
