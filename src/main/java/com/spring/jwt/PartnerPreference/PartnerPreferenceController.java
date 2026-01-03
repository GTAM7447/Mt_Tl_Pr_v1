package com.spring.jwt.PartnerPreference;

import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceCreateRequest;
import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceResponse;
import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceUpdateRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for partner preference management.
 * Implements secure endpoints with proper authorization and comprehensive API documentation.
 * All user operations are scoped to the authenticated user's data to prevent IDOR vulnerabilities.
 */
@RestController
@RequestMapping("/api/v1/partner-preferences")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Partner Preferences", description = "Partner preference management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class PartnerPreferenceController {

    private final PartnerPreferenceService partnerPreferenceService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "partnerPreferenceApi")
    @Operation(summary = "Create partner preferences", 
               description = "Create partner preferences for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Partner preferences created successfully",
                    content = @Content(schema = @Schema(implementation = PartnerPreferenceResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Partner preferences already exist for user"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "429", description = "Too many requests - Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<PartnerPreferenceResponse>> createPartnerPreferences(
            @Valid @RequestBody PartnerPreferenceCreateRequest request) {
        
        log.info("Creating partner preferences for authenticated user");
        
        PartnerPreferenceResponse response = partnerPreferenceService.createForCurrentUser(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success("Partner preferences created successfully", response));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "partnerPreferenceApi")
    @Operation(summary = "Get current user's partner preferences", 
               description = "Retrieve partner preferences for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partner preferences retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PartnerPreferenceResponse.class))),
        @ApiResponse(responseCode = "404", description = "Partner preferences not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "429", description = "Too many requests - Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<PartnerPreferenceResponse>> getCurrentUserPartnerPreferences() {
        
        log.debug("Fetching partner preferences for authenticated user");
        
        PartnerPreferenceResponse response = partnerPreferenceService.getCurrentUserPartnerPreferences();
        
        return ResponseEntity.ok(ResponseDto.success("Partner preferences retrieved successfully", response));
    }

    @PatchMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "partnerPreferenceApi")
    @Operation(summary = "Update current user's partner preferences", 
               description = "Update partner preferences for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partner preferences updated successfully",
                    content = @Content(schema = @Schema(implementation = PartnerPreferenceResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Partner preferences not found"),
        @ApiResponse(responseCode = "409", description = "Version conflict - Resource was modified by another transaction"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "429", description = "Too many requests - Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<PartnerPreferenceResponse>> updateCurrentUserPartnerPreferences(
            @Valid @RequestBody PartnerPreferenceUpdateRequest request) {
        
        log.info("Updating partner preferences for authenticated user");
        
        PartnerPreferenceResponse response = partnerPreferenceService.updateCurrentUserPartnerPreferences(request);
        
        return ResponseEntity.ok(ResponseDto.success("Partner preferences updated successfully", response));
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "partnerPreferenceApi")
    @Operation(summary = "Delete current user's partner preferences", 
               description = "Soft delete partner preferences for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partner preferences deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Partner preferences not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "429", description = "Too many requests - Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<Object>> deleteCurrentUserPartnerPreferences() {
        
        log.info("Deleting partner preferences for authenticated user");
        
        partnerPreferenceService.deleteCurrentUserPartnerPreferences();
        
        return ResponseEntity.ok(ResponseDto.success("Partner preferences deleted successfully", null));
    }


    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get partner preferences by user ID", 
               description = "Retrieve partner preferences for a specific user (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partner preferences retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PartnerPreferenceResponse.class))),
        @ApiResponse(responseCode = "404", description = "Partner preferences not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<PartnerPreferenceResponse>> getPartnerPreferencesByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable Integer userId) {
        
        log.debug("Admin fetching partner preferences for user ID: {}", userId);
        
        PartnerPreferenceResponse response = partnerPreferenceService.getByUserId(userId);
        
        return ResponseEntity.ok(ResponseDto.success("Partner preferences retrieved successfully", response));
    }

//    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    @Operation(summary = "Get all partner preferences",
//               description = "Retrieve all partner preferences with pagination (Admin only)")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Partner preferences retrieved successfully"),
//        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
//        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
//    })
//    public ResponseEntity<ResponseDto<Page<PartnerPreferenceResponse>>> getAllPartnerPreferences(
//            @Parameter(description = "Page number (0-based)", example = "0")
//            @RequestParam(defaultValue = "0") @Min(0) int page,
//
//            @Parameter(description = "Page size", example = "20")
//            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
//
//            @Parameter(description = "Sort field", example = "createdAt")
//            @RequestParam(defaultValue = "createdAt") String sortBy,
//
//            @Parameter(description = "Sort direction", example = "DESC")
//            @RequestParam(defaultValue = "DESC") Sort.Direction sortDir) {
//
//        log.debug("Admin fetching all partner preferences - page: {}, size: {}", page, size);
//
//        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir, sortBy));
//        Page<PartnerPreferenceResponse> response = partnerPreferenceService.getAllPartnerPreferences(pageable);
//
//        return ResponseEntity.ok(ResponseDto.success("Partner preferences retrieved successfully", response));
//    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Search partner preferences", 
               description = "Search partner preferences by various criteria (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<Page<PartnerPreferenceResponse>>> searchPartnerPreferences(
            @Parameter(description = "Religion filter", example = "Hindu")
            @RequestParam(required = false) String religion,
            
            @Parameter(description = "Caste filter", example = "Brahmin")
            @RequestParam(required = false) String caste,
            
            @Parameter(description = "Education filter", example = "Graduate")
            @RequestParam(required = false) String education,
            
            @Parameter(description = "Minimum income filter", example = "500000")
            @RequestParam(required = false) Integer minIncome,
            
            @Parameter(description = "Maximum income filter", example = "2000000")
            @RequestParam(required = false) Integer maxIncome,
            
            @Parameter(description = "Location filter", example = "Bangalore")
            @RequestParam(required = false) String location,
            
            @Parameter(description = "Marital status filter", example = "Never Married")
            @RequestParam(required = false) String maritalStatus,
            
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            
            @Parameter(description = "Sort field", example = "partnerIncome")
            @RequestParam(defaultValue = "partnerIncome") String sortBy,
            
            @Parameter(description = "Sort direction", example = "DESC")
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDir) {
        
        log.debug("Admin searching partner preferences with filters");
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir, sortBy));
        Page<PartnerPreferenceResponse> response = partnerPreferenceService.searchPartnerPreferences(
                religion, caste, education, minIncome, maxIncome, location, maritalStatus, pageable);
        
        return ResponseEntity.ok(ResponseDto.success("Search results retrieved successfully", response));
    }

    @GetMapping("/search/lifestyle")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Search partner preferences by lifestyle", 
               description = "Search partner preferences by lifestyle choices (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<Page<PartnerPreferenceResponse>>> searchByLifestyleChoices(
            @Parameter(description = "Eating habits filter", example = "Vegetarian")
            @RequestParam(required = false) String eatingHabits,
            
            @Parameter(description = "Drinking habits filter", example = "Non-drinker")
            @RequestParam(required = false) String drinkingHabits,
            
            @Parameter(description = "Smoking habits filter", example = "Non-smoker")
            @RequestParam(required = false) String smokingHabits,
            
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            
            @Parameter(description = "Sort field", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            
            @Parameter(description = "Sort direction", example = "DESC")
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDir) {
        
        log.debug("Admin searching partner preferences by lifestyle choices");
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir, sortBy));
        Page<PartnerPreferenceResponse> response = partnerPreferenceService.searchByLifestyleChoices(
                eatingHabits, drinkingHabits, smokingHabits, pageable);
        
        return ResponseEntity.ok(ResponseDto.success("Lifestyle search results retrieved successfully", response));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get partner preference statistics", 
               description = "Retrieve statistical information about partner preference data (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<PartnerPreferenceService.PartnerPreferenceStats>> getStatistics() {
        
        log.debug("Admin fetching partner preference statistics");
        
        PartnerPreferenceService.PartnerPreferenceStats stats = partnerPreferenceService.getStatistics();
        
        return ResponseEntity.ok(ResponseDto.success("Statistics retrieved successfully", stats));
    }

    @GetMapping("/compatibility/{userId1}/{userId2}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get compatibility score", 
               description = "Calculate compatibility score between two users (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Compatibility score calculated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<Integer>> getCompatibilityScore(
            @Parameter(description = "First user ID", required = true)
            @PathVariable Integer userId1,
            
            @Parameter(description = "Second user ID", required = true)
            @PathVariable Integer userId2) {
        
        log.debug("Admin calculating compatibility score between users {} and {}", userId1, userId2);
        
        Integer score = partnerPreferenceService.getCompatibilityScore(userId1, userId2);
        
        return ResponseEntity.ok(ResponseDto.success("Compatibility score calculated successfully", score));
    }
}