package com.spring.jwt.admin;

import com.spring.jwt.CompleteProfile.dto.CompleteProfileResponse;
import com.spring.jwt.CompleteProfile.dto.ProfileAnalyticsRequest;
import com.spring.jwt.CompleteProfile.MissingProfileDTO;
import com.spring.jwt.CompleteProfile.CompleteProfileService;
import com.spring.jwt.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/complete-profile")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Admin Complete Profile Management", description = "Admin operations for complete profile workflow and analytics")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCompleteProfileController {

    private final CompleteProfileService completeProfileService;

    @Operation(
        summary = "Get complete profile by user ID (Admin)",
        description = "Admin can retrieve any user's complete profile with all modules"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Complete profile retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<CompleteProfileResponse> getCompleteProfileByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin retrieving complete profile for user ID: {}", userId);
        CompleteProfileResponse response = completeProfileService.getByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get missing profile sections (Admin)",
        description = "Admin can check which profile sections are missing for any user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Missing profile sections retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/user/{userId}/missing")
    public ResponseEntity<MissingProfileDTO> getMissingProfileSections(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin checking missing profile sections for user ID: {}", userId);
        MissingProfileDTO response = completeProfileService.checkMissingSections(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get all complete profiles (Admin)",
        description = "Admin can retrieve all complete profiles with pagination, sorting, and filtering. " +
                     "Valid sort fields: completeProfileId, userId, completionPercentage, completenessScore, " +
                     "profileQuality, verificationStatus, profileCompleted, createdAt, updatedAt"
    )
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Complete profiles retrieved successfully"), @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"), @ApiResponse(responseCode = "200", description = "Successfully retrieved complete profiles"), @ApiResponse(responseCode = "400", description = "Invalid request parameters"), @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")})
    @GetMapping("/all")
    public ResponseEntity<List<CompleteProfileResponse>> getAllCompleteProfiles(
            @Parameter(
                description = "Page number (0-based)",
                example = "0"
            )
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number cannot be negative") int page,
            
            @Parameter(
                description = "Number of records per page",
                example = "10"
            )
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be positive") int size,
            
            @Parameter(
                description = "Field to sort by. Valid values: completeProfileId, userId, completionPercentage, " +
                             "completenessScore, profileQuality, verificationStatus, profileCompleted, createdAt, updatedAt",
                example = "updatedAt",
                schema = @Schema(
                    allowableValues = {"completeProfileId", "userId", "completionPercentage", "completenessScore", 
                                      "profileQuality", "verificationStatus", "profileCompleted", "createdAt", "updatedAt"}
                )
            )
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            
            @Parameter(
                description = "Sort direction",
                example = "desc",
                schema = @Schema(allowableValues = {"asc", "desc"})
            )
            @RequestParam(defaultValue = "desc") String sortDir,
            
            @Parameter(
                description = "Filter by profile completion status (true for complete profiles only, false for incomplete, null for all)",
                example = "true"
            )
            @RequestParam(required = false) Boolean isComplete) {
        
        log.info("Admin retrieving all complete profiles - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                page, size, sortBy, sortDir);

        // Validate and sanitize sortBy to prevent SQL injection and invalid field names
        String validatedSortBy = validateAndSanitizeSortField(sortBy);
        
        // Validate sort direction
        String validatedSortDir = sortDir != null && sortDir.equalsIgnoreCase("asc") ? "asc" : "desc";
        
        Sort sort = validatedSortDir.equalsIgnoreCase("desc")
            ? Sort.by(validatedSortBy).descending()
            : Sort.by(validatedSortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<CompleteProfileResponse> profilePage = completeProfileService.getAllCompleteProfiles(pageable);
        return ResponseEntity.ok(profilePage.getContent());
    }
    
    /**
     * Validate and sanitize sort field to prevent invalid field names.
     * Only allows whitelisted fields that exist in CompleteProfile entity.
     */
    private String validateAndSanitizeSortField(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "updatedAt";
        }
        
        // Whitelist of valid sort fields
        java.util.Set<String> validFields = java.util.Set.of(
            "completeProfileId", "userId", "completionPercentage", "completenessScore",
            "profileQuality", "verificationStatus", "profileCompleted", "createdAt", "updatedAt"
        );
        
        String sanitized = sortBy.trim().toLowerCase();
        
        // Check if it's a valid field
        if (validFields.contains(sanitized)) {
            return sanitized;
        }
        
        // Default to updatedAt if invalid
        log.warn("Invalid sort field requested: {}. Using default: updatedAt", sortBy);
        return "updatedAt";
    }

    @Operation(
        summary = "Get profile analytics (Admin)",
        description = "Admin can get comprehensive profile completion analytics"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile analytics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/analytics")
    public ResponseEntity<CompleteProfileService.ProfileAnalyticsResponse> getProfileAnalytics(
            @Valid @RequestBody ProfileAnalyticsRequest request) {
        
        log.info("Admin retrieving profile analytics with filters: {}", request);
        CompleteProfileService.ProfileAnalyticsResponse response = completeProfileService.getProfileAnalytics(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get profile completion statistics (Admin)",
        description = "Admin can get overall profile completion statistics"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile statistics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/statistics")
    public ResponseEntity<CompleteProfileService.ProfileCompletionStats> getProfileStatistics() {
        
        log.info("Admin retrieving profile completion statistics");
        CompleteProfileService.ProfileCompletionStats response = completeProfileService.getCompletionStatistics();
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Force profile completion calculation (Admin)",
        description = "Admin can force recalculation of profile completion for a user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile completion recalculated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/user/{userId}/recalculate")
    public ResponseEntity<ResponseDto<String>> recalculateProfileCompletion(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin forcing profile completion recalculation for user ID: {}", userId);
        completeProfileService.forceRecalculateProfile(userId);
        ResponseDto<String> response = ResponseDto.success("Profile completion recalculated successfully", 
            "Profile completion has been recalculated for user " + userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get top profiles by completion score (Admin)",
        description = "Admin can get top profiles ordered by completion score"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Top profiles retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/top-profiles")
    public ResponseEntity<List<CompleteProfileResponse>> getTopProfilesByScore(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number cannot be negative") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be positive") int size) {
        
        log.info("Admin retrieving top profiles by completion score - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CompleteProfileResponse> profilePage = completeProfileService.getTopProfilesByScore(pageable);
        return ResponseEntity.ok(profilePage.getContent());
    }
}