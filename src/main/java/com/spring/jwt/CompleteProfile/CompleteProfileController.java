package com.spring.jwt.CompleteProfile;

import com.spring.jwt.CompleteProfile.dto.CompleteProfileResponse;
import com.spring.jwt.CompleteProfile.dto.ProfileAnalyticsRequest;
import com.spring.jwt.dto.ResponseDto;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * REST Controller for complete profile management.
 * Implements secure endpoints with proper authorization and comprehensive API documentation.
 * Provides profile aggregation, completeness tracking, and analytics capabilities.
 */
@RestController
@RequestMapping("/api/v1/complete-profile")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Complete Profile", description = "Complete profile aggregation and analytics endpoints")
@SecurityRequirement(name = "bearerAuth")
public class CompleteProfileController {

    private final CompleteProfileService completeProfileService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "completeProfileApi")
    @Operation(summary = "Get current user's complete profile", 
               description = "Retrieve aggregated profile information for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Complete profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CompleteProfileResponse.class))),
        @ApiResponse(responseCode = "404", description = "Complete profile not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "429", description = "Too many requests - Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<CompleteProfileResponse>> getCurrentUserCompleteProfile() {
        
        log.debug("Fetching complete profile for authenticated user");
        
        CompleteProfileResponse response = completeProfileService.getCurrentUserCompleteProfile();
        
        return ResponseEntity.ok(ResponseDto.success("Complete profile retrieved successfully", response));
    }

    @GetMapping("/me/missing-sections")
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "completeProfileApi")
    @Operation(summary = "Check missing profile sections", 
               description = "Get detailed analysis of missing profile sections for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Missing sections analysis retrieved successfully",
                    content = @Content(schema = @Schema(implementation = MissingProfileDTO.class))),
        @ApiResponse(responseCode = "404", description = "Complete profile not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "429", description = "Too many requests - Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<MissingProfileDTO>> getCurrentUserMissingSections() {
        
        log.debug("Checking missing sections for authenticated user");
        
        MissingProfileDTO response = completeProfileService.checkCurrentUserMissingSections();
        
        return ResponseEntity.ok(ResponseDto.success("Missing sections analysis retrieved successfully", response));
    }

    @PostMapping("/me/recalculate")
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "completeProfileApi")
    @Operation(summary = "Force recalculate profile completeness", 
               description = "Manually trigger profile completeness recalculation for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile recalculation triggered successfully"),
        @ApiResponse(responseCode = "404", description = "Complete profile not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "429", description = "Too many requests - Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<Object>> forceRecalculateCurrentUserProfile() {
        
        log.info("Force recalculating profile for authenticated user");

        completeProfileService.getCurrentUserCompleteProfile();
        Integer currentUserId = getCurrentUserId();
        
        completeProfileService.forceRecalculateProfile(currentUserId);
        
        return ResponseEntity.ok(ResponseDto.success("Profile recalculation triggered successfully", null));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get complete profile by user ID", 
               description = "Retrieve complete profile for a specific user (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Complete profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CompleteProfileResponse.class))),
        @ApiResponse(responseCode = "404", description = "Complete profile not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<CompleteProfileResponse>> getCompleteProfileByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable Integer userId) {
        
        log.debug("Admin fetching complete profile for user ID: {}", userId);
        
        CompleteProfileResponse response = completeProfileService.getByUserId(userId);
        
        return ResponseEntity.ok(ResponseDto.success("Complete profile retrieved successfully", response));
    }

    @GetMapping("/user/{userId}/missing-sections")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Check missing sections by user ID", 
               description = "Get missing sections analysis for a specific user (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Missing sections analysis retrieved successfully",
                    content = @Content(schema = @Schema(implementation = MissingProfileDTO.class))),
        @ApiResponse(responseCode = "404", description = "Complete profile not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<MissingProfileDTO>> getMissingSectionsByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable Integer userId) {
        
        log.debug("Admin checking missing sections for user ID: {}", userId);
        
        MissingProfileDTO response = completeProfileService.checkMissingSections(userId);
        
        return ResponseEntity.ok(ResponseDto.success("Missing sections analysis retrieved successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all complete profiles", 
               description = "Retrieve all complete profiles with pagination (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Complete profiles retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<Page<CompleteProfileResponse>>> getAllCompleteProfiles(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            
            @Parameter(description = "Sort field", example = "updatedAt")
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            
            @Parameter(description = "Sort direction", example = "DESC")
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDir) {
        
        log.debug("Admin fetching all complete profiles - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir, sortBy));
        Page<CompleteProfileResponse> response = completeProfileService.getAllCompleteProfiles(pageable);
        
        return ResponseEntity.ok(ResponseDto.success("Complete profiles retrieved successfully", response));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Search complete profiles", 
               description = "Search complete profiles by completion criteria (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<Page<CompleteProfileResponse>>> searchCompleteProfiles(
            @Parameter(description = "Minimum completion percentage", example = "50")
            @RequestParam(required = false) @Min(0) @Max(100) Integer minPercentage,
            
            @Parameter(description = "Maximum completion percentage", example = "100")
            @RequestParam(required = false) @Min(0) @Max(100) Integer maxPercentage,
            
            @Parameter(description = "Profile quality filter", example = "GOOD")
            @RequestParam(required = false) String profileQuality,
            
            @Parameter(description = "Verification status filter", example = "VERIFIED")
            @RequestParam(required = false) String verificationStatus,
            
            @Parameter(description = "Profile completion status", example = "true")
            @RequestParam(required = false) Boolean profileCompleted,
            
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            
            @Parameter(description = "Sort field", example = "completionPercentage")
            @RequestParam(defaultValue = "completionPercentage") String sortBy,
            
            @Parameter(description = "Sort direction", example = "DESC")
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDir) {
        
        log.debug("Admin searching complete profiles with criteria");
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir, sortBy));
        Page<CompleteProfileResponse> response = completeProfileService.searchByCompletionCriteria(
                minPercentage, maxPercentage, profileQuality, verificationStatus, 
                profileCompleted, pageable);
        
        return ResponseEntity.ok(ResponseDto.success("Search results retrieved successfully", response));
    }

    @GetMapping("/top-profiles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get top profiles by score", 
               description = "Retrieve profiles with highest completion scores (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Top profiles retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<Page<CompleteProfileResponse>>> getTopProfilesByScore(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {
        
        log.debug("Admin fetching top profiles by score");
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CompleteProfileResponse> response = completeProfileService.getTopProfilesByScore(pageable);
        
        return ResponseEntity.ok(ResponseDto.success("Top profiles retrieved successfully", response));
    }

    @GetMapping("/updated-between")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get profiles updated within date range", 
               description = "Retrieve profiles updated between specified dates (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profiles retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<Page<CompleteProfileResponse>>> getProfilesUpdatedBetween(
            @Parameter(description = "Start date", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            
            @Parameter(description = "End date", required = true, example = "2024-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            
            @Parameter(description = "Sort field", example = "updatedAt")
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            
            @Parameter(description = "Sort direction", example = "DESC")
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDir) {
        
        log.debug("Admin fetching profiles updated between {} and {}", startDate, endDate);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir, sortBy));
        Page<CompleteProfileResponse> response = completeProfileService.getProfilesUpdatedBetween(
                startDate, endDate, pageable);
        
        return ResponseEntity.ok(ResponseDto.success("Profiles retrieved successfully", response));
    }

    @PostMapping("/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get profile analytics", 
               description = "Generate comprehensive profile analytics and reports (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Analytics generated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<CompleteProfileService.ProfileAnalyticsResponse>> getProfileAnalytics(
            @Valid @RequestBody ProfileAnalyticsRequest request) {
        
        log.debug("Admin generating profile analytics");
        
        CompleteProfileService.ProfileAnalyticsResponse response = completeProfileService.getProfileAnalytics(request);
        
        return ResponseEntity.ok(ResponseDto.success("Analytics generated successfully", response));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get profile completion statistics", 
               description = "Retrieve profile completion statistics and metrics (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<CompleteProfileService.ProfileCompletionStats>> getCompletionStatistics() {
        
        log.debug("Admin fetching profile completion statistics");
        
        CompleteProfileService.ProfileCompletionStats response = completeProfileService.getCompletionStatistics();
        
        return ResponseEntity.ok(ResponseDto.success("Statistics retrieved successfully", response));
    }

    @PostMapping("/user/{userId}/recalculate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Force recalculate profile by user ID", 
               description = "Manually trigger profile completeness recalculation for a specific user (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile recalculation triggered successfully"),
        @ApiResponse(responseCode = "404", description = "Complete profile not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<Object>> forceRecalculateProfileByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable Integer userId) {
        
        log.info("Admin force recalculating profile for user ID: {}", userId);
        
        completeProfileService.forceRecalculateProfile(userId);
        
        return ResponseEntity.ok(ResponseDto.success("Profile recalculation triggered successfully", null));
    }

    private Integer getCurrentUserId() {
        return null;
    }
}
