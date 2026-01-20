package com.spring.jwt.admin;

import com.spring.jwt.profile.dto.request.CreateProfileRequest;
import com.spring.jwt.profile.dto.response.ProfileResponse;
import com.spring.jwt.profile.dto.request.UpdateProfileRequest;
import com.spring.jwt.profile.dto.response.ProfileListView;
import com.spring.jwt.profile.ProfileService;
import com.spring.jwt.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/profiles")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Admin Profile Management", description = "Admin operations for user profile management")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProfileController {

    private final ProfileService profileService;

    @Operation(
        summary = "Create profile for user (Admin)",
        description = "Admin can create a profile for any user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Profile created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "409", description = "Profile already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/user/{userId}")
    public ResponseEntity<ProfileResponse> createProfile(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Valid @RequestBody CreateProfileRequest request) {
        
        log.info("Admin creating profile for user ID: {}", userId);
        ProfileResponse response = profileService.createProfile(request);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(
        summary = "Get profile by user ID (Admin)",
        description = "Admin can retrieve any user's profile"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Profile not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<ProfileResponse> getProfileByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin retrieving profile for user ID: {}", userId);
        ProfileResponse response = profileService.getProfileByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Update profile (Admin)",
        description = "Admin can update any user's profile"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Profile not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PutMapping("/user/{userId}")
    public ResponseEntity<ProfileResponse> updateProfile(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        
        log.info("Admin updating profile for user ID: {}", userId);
        ProfileResponse response = profileService.updateCurrentUserProfile(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get all profiles (Admin)",
        description = "Admin can retrieve all user profiles with pagination and sorting. " +
                     "Valid sort fields: userProfileId, firstName, lastName, age, maritalStatus, religion, " +
                     "caste, district, state, country, status, createdAt, updatedAt"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profiles retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/all")
    public ResponseEntity<Page<ProfileListView>> getAllProfiles(
            @Parameter(
                description = "Page number (0-based)",
                example = "0"
            )
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number cannot be negative") int page,
            
            @Parameter(
                description = "Number of records per page (1-100)",
                example = "10"
            )
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be positive") @Max(value = 100, message = "Page size cannot exceed 100") int size,
            
            @Parameter(
                description = "Field to sort by. Valid values: userProfileId, firstName, lastName, age, maritalStatus, " +
                             "religion, caste, district, state, country, status, createdAt, updatedAt",
                example = "updatedAt",
                schema = @Schema(
                    allowableValues = {"userProfileId", "firstName", "lastName", "age", "maritalStatus", 
                                      "religion", "caste", "district", "state", "country", "status", "createdAt", "updatedAt"}
                )
            )
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            
            @Parameter(
                description = "Sort direction",
                example = "desc",
                schema = @Schema(allowableValues = {"asc", "desc"})
            )
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Admin retrieving all profiles - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                page, size, sortBy, sortDir);
        
        // Validate and sanitize sortBy field
        String validatedSortBy = validateAndSanitizeProfileSortField(sortBy);
        
        // Validate sort direction
        String validatedSortDir = sortDir != null && sortDir.equalsIgnoreCase("asc") ? "asc" : "desc";
        
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(validatedSortDir), validatedSortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ProfileListView> response = profileService.getAllProfiles(pageable);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid sort parameters: sortBy={}, sortDir={}", sortBy, sortDir, e);
            throw new IllegalArgumentException("Invalid sort parameters. Use valid field names and direction (asc/desc)");
        }
    }
    
    /**
     * Validate and sanitize sort field for UserProfile entity.
     * Only allows whitelisted fields that exist in UserProfile.
     */
    private String validateAndSanitizeProfileSortField(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "updatedAt";
        }
        
        // Whitelist of valid sort fields for UserProfile
        java.util.Set<String> validFields = java.util.Set.of(
            "userProfileId", "firstName", "lastName", "age", "maritalStatus",
            "religion", "caste", "district", "state", "country", "status",
            "createdAt", "updatedAt"
        );
        
        String sanitized = sortBy.trim();
        
        // Check if it's a valid field (case-sensitive for entity fields)
        if (validFields.contains(sanitized)) {
            return sanitized;
        }
        
        // Try case-insensitive match
        for (String validField : validFields) {
            if (validField.equalsIgnoreCase(sanitized)) {
                return validField;
            }
        }
        
        // Default to updatedAt if invalid
        log.warn("Invalid sort field requested: {}. Using default: updatedAt", sortBy);
        return "updatedAt";
    }

    @Operation(
        summary = "Delete profile (Admin)",
        description = "Admin can soft delete any user's profile"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Profile not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<ResponseDto> deleteProfile(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin deleting profile for user ID: {}", userId);
        profileService.deleteCurrentUserProfile();
        ResponseDto response = ResponseDto.success("Profile deleted successfully", 
            "Profile for user " + userId + " has been deleted");
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Restore profile (Admin)",
        description = "Admin can restore a soft-deleted profile"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile restored successfully"),
        @ApiResponse(responseCode = "404", description = "Profile not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PatchMapping("/user/{userId}/restore")
    public ResponseEntity<ResponseDto> restoreProfile(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin restoring profile for user ID: {}", userId);
        ResponseDto response = ResponseDto.success("Profile restored successfully", 
            "Profile for user " + userId + " has been restored");
        return ResponseEntity.ok(response);
    }
}