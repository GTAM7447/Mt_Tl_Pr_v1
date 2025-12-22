package com.spring.jwt.admin;

import com.spring.jwt.admin.dto.*;
import com.spring.jwt.admin.service.AdminWorkflowService;
import com.spring.jwt.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/workflow")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Admin Registration Workflow", description = "Admin operations for complete user registration workflow management")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminWorkflowController {

    private final AdminWorkflowService adminWorkflowService;

    @Operation(
        summary = "Complete user registration workflow (Admin) - DEPRECATED",
        description = "DEPRECATED: Use step-by-step APIs instead. This monolithic approach doesn't match the 7-form UI workflow.",
        deprecated = true
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Complete user registration successful"),
        @ApiResponse(responseCode = "400", description = "Invalid input data - Use step-by-step APIs for better validation"),
        @ApiResponse(responseCode = "409", description = "User already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/complete-registration")
    @Deprecated
    public ResponseEntity<Map<String, Object>> completeUserRegistration(
            @Valid @RequestBody CompleteUserRegistrationRequest request) {
        
        log.warn("DEPRECATED API called: complete-registration. Use step-by-step workflow APIs instead.");
        log.info("Admin initiating complete user registration workflow for email: {}", request.getUserDetails().getEmail());
        Map<String, Object> response = adminWorkflowService.completeUserRegistration(request);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(
        summary = "Step 1: User Registration (Admin)",
        description = "Admin creates a new user account - First step in the 7-step registration workflow"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid user data"),
        @ApiResponse(responseCode = "409", description = "User already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/step/user-registration")
    public ResponseEntity<ResponseDto> stepUserRegistration(
            @Valid @RequestBody UserRegistrationStepRequest request) {
        
        log.info("Admin executing Step 1: User Registration for email: {}", request.getEmail());
        ResponseDto response = adminWorkflowService.executeUserRegistrationStep(request);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(
        summary = "Step 2: Profile Details (Admin)",
        description = "Admin adds profile details for a user - Second step in the 7-step registration workflow"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Profile details saved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid profile data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/step/profile-details/{userId}")
    public ResponseEntity<ResponseDto> stepProfileDetails(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Valid @RequestBody ProfileDetailsStepRequest request) {
        
        log.info("Admin executing Step 2: Profile Details for user ID: {}", userId);
        ResponseDto response = adminWorkflowService.executeProfileDetailsStep(userId, request);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(
        summary = "Step 3: Horoscope Details (Admin)",
        description = "Admin adds horoscope details for a user - Third step in the 7-step registration workflow"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Horoscope details saved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid horoscope data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/step/horoscope-details/{userId}")
    public ResponseEntity<ResponseDto> stepHoroscopeDetails(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Valid @RequestBody HoroscopeDetailsStepRequest request) {
        
        log.info("Admin executing Step 3: Horoscope Details for user ID: {}", userId);
        ResponseDto response = adminWorkflowService.executeHoroscopeDetailsStep(userId, request);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(
        summary = "Step 4: Education & Profession (Admin)",
        description = "Admin adds education and profession details for a user - Fourth step in the 7-step registration workflow"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Education details saved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid education data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/step/education-profession/{userId}")
    public ResponseEntity<ResponseDto> stepEducationProfession(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Valid @RequestBody EducationProfessionStepRequest request) {
        
        log.info("Admin executing Step 4: Education & Profession for user ID: {}", userId);
        ResponseDto response = adminWorkflowService.executeEducationProfessionStep(userId, request);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(
        summary = "Step 5: Family Background (Admin)",
        description = "Admin adds family background details for a user - Fifth step in the 7-step registration workflow"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Family background saved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid family background data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/step/family-background/{userId}")
    public ResponseEntity<ResponseDto> stepFamilyBackground(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Valid @RequestBody FamilyBackgroundStepRequest request) {
        
        log.info("Admin executing Step 5: Family Background for user ID: {}", userId);
        ResponseDto response = adminWorkflowService.executeFamilyBackgroundStep(userId, request);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(
        summary = "Step 6: Partner Preferences (Admin)",
        description = "Admin adds partner preferences for a user - Sixth step in the 7-step registration workflow"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Partner preferences saved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid partner preference data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/step/partner-preferences/{userId}")
    public ResponseEntity<ResponseDto> stepPartnerPreferences(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Valid @RequestBody PartnerPreferencesStepRequest request) {
        
        log.info("Admin executing Step 6: Partner Preferences for user ID: {}", userId);
        ResponseDto response = adminWorkflowService.executePartnerPreferencesStep(userId, request);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(
        summary = "Step 7: Contact Details (Admin)",
        description = "Admin adds contact details for a user - Final step in the 7-step registration workflow"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Contact details saved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid contact data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/step/contact-details/{userId}")
    public ResponseEntity<ResponseDto> stepContactDetails(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Valid @RequestBody ContactDetailsStepRequest request) {
        
        log.info("Admin executing Step 7: Contact Details for user ID: {}", userId);
        ResponseDto response = adminWorkflowService.executeContactDetailsStep(userId, request);
        return ResponseEntity.status(201).body(response);
    }
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Workflow steps retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/user/{userId}/steps")
    public ResponseEntity<List<WorkflowStepResponse>> getWorkflowSteps(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin retrieving workflow steps for user ID: {}", userId);
        List<WorkflowStepResponse> response = adminWorkflowService.getWorkflowSteps(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Execute specific workflow step (Admin)",
        description = "Admin can execute a specific workflow step for a user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Workflow step executed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid step or input data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/user/{userId}/step/{stepName}")
    public ResponseEntity<WorkflowStepResponse> executeWorkflowStep(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Parameter(description = "Workflow step name", required = true)
            @PathVariable String stepName,
            @RequestBody(required = false) Map<String, Object> stepData) {
        
        log.info("Admin executing workflow step '{}' for user ID: {}", stepName, userId);
        WorkflowStepResponse response = adminWorkflowService.executeWorkflowStep(userId, stepName, stepData);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Skip workflow step (Admin)",
        description = "Admin can skip a specific workflow step for a user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Workflow step skipped successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid step"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PatchMapping("/user/{userId}/step/{stepName}/skip")
    public ResponseEntity<ResponseDto> skipWorkflowStep(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Parameter(description = "Workflow step name", required = true)
            @PathVariable String stepName,
            @Parameter(description = "Reason for skipping")
            @RequestParam(required = false) String reason) {
        
        log.info("Admin skipping workflow step '{}' for user ID: {} with reason: {}", stepName, userId, reason);
        ResponseDto response = adminWorkflowService.skipWorkflowStep(userId, stepName, reason);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Reset workflow step (Admin)",
        description = "Admin can reset a specific workflow step for a user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Workflow step reset successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid step"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PatchMapping("/user/{userId}/step/{stepName}/reset")
    public ResponseEntity<ResponseDto> resetWorkflowStep(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Parameter(description = "Workflow step name", required = true)
            @PathVariable String stepName) {
        
        log.info("Admin resetting workflow step '{}' for user ID: {}", stepName, userId);
        ResponseDto response = adminWorkflowService.resetWorkflowStep(userId, stepName);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get workflow progress (Admin)",
        description = "Admin can get the overall workflow progress for a user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Workflow progress retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/user/{userId}/progress")
    public ResponseEntity<Map<String, Object>> getWorkflowProgress(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin retrieving workflow progress for user ID: {}", userId);
        Map<String, Object> response = adminWorkflowService.getWorkflowProgress(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get workflow analytics (Admin)",
        description = "Admin can get comprehensive workflow analytics across all users"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Workflow analytics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getWorkflowAnalytics(
            @Parameter(description = "Start date for analytics (YYYY-MM-DD)")
            @RequestParam(required = false) String startDate,
            @Parameter(description = "End date for analytics (YYYY-MM-DD)")
            @RequestParam(required = false) String endDate,
            @Parameter(description = "Group by period (day/week/month)")
            @RequestParam(defaultValue = "day") String groupBy) {
        
        log.info("Admin retrieving workflow analytics from {} to {} grouped by {}", startDate, endDate, groupBy);
        Map<String, Object> response = adminWorkflowService.getWorkflowAnalytics(startDate, endDate, groupBy);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get users by workflow status (Admin)",
        description = "Admin can get list of users filtered by their workflow completion status"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/users/by-status")
    public ResponseEntity<Map<String, Object>> getUsersByWorkflowStatus(
            @Parameter(description = "Workflow status filter")
            @RequestParam(required = false) String status,
            @Parameter(description = "Specific step filter")
            @RequestParam(required = false) String currentStep,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number cannot be negative") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be positive") int size) {
        
        log.info("Admin retrieving users by workflow status: {}, currentStep: {}, page: {}, size: {}", 
                status, currentStep, page, size);
        Map<String, Object> response = adminWorkflowService.getUsersByWorkflowStatus(status, currentStep, page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Bulk workflow operations (Admin)",
        description = "Admin can perform bulk operations on multiple users' workflows"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bulk operation completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid operation or user IDs"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/bulk-operation")
    public ResponseEntity<Map<String, Object>> bulkWorkflowOperation(
            @Parameter(description = "List of user IDs", required = true)
            @RequestParam List<Integer> userIds,
            @Parameter(description = "Operation type (complete/reset/skip)", required = true)
            @RequestParam String operation,
            @Parameter(description = "Specific step for operation")
            @RequestParam(required = false) String stepName,
            @Parameter(description = "Reason for operation")
            @RequestParam(required = false) String reason) {
        
        log.info("Admin performing bulk workflow operation '{}' on {} users", operation, userIds.size());
        Map<String, Object> response = adminWorkflowService.bulkWorkflowOperation(userIds, operation, stepName, reason);
        return ResponseEntity.ok(response);
    }
}