package com.spring.jwt.profile.controller;

import com.spring.jwt.aspect.Loggable;
import com.spring.jwt.aspect.RequiresSubscription;
import com.spring.jwt.dto.ResponseDto;
import com.spring.jwt.profile.ProfileService;
import com.spring.jwt.profile.dto.request.CreateProfileRequest;
import com.spring.jwt.profile.dto.request.ProfileSearchCriteria;
import com.spring.jwt.profile.dto.request.UpdateProfileRequest;
import com.spring.jwt.profile.dto.response.ProfileListView;
import com.spring.jwt.profile.dto.response.ProfileResponse;
import com.spring.jwt.profile.dto.response.PublicProfileView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for user profile management.
 * Implements RESTful API design with proper HTTP semantics, security, and
 * validation.
 *
 * Base path: /api/v1/profiles
 */
@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Profile Management", description = "APIs for managing user profiles")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    private final ProfileService profileService;

    /**
     * Create a new user profile.
     * POST /api/v1/profiles
     *
     * @param request the profile creation request
     * @return created profile with HTTP 201 status
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "profileApi")
    @Loggable(action = "CREATE_PROFILE")
    @Operation(summary = "Create user profile", description = "Create a new profile for the authenticated user. Each user can only have one profile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Profile created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or validation errors"),
            @ApiResponse(responseCode = "409", description = "Profile already exists for this user"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<ProfileResponse>> createProfile(
            @Valid @RequestBody @Parameter(description = "Profile creation request") CreateProfileRequest request) {

        log.info("Received create profile request");

        ProfileResponse response = profileService.createProfile(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDto.success("Profile created successfully", response));
    }

    /**
     * Get current authenticated user's profile.
     * GET /api/v1/profiles/me
     *
     * @return current user's profile
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "profileApi")
    @Operation(summary = "Get current user profile", description = "Retrieve the profile of the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Profile not found for current user"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<ProfileResponse>> getCurrentUserProfile() {

        ProfileResponse response = profileService.getCurrentUserProfile();

        return ResponseEntity.ok(
                ResponseDto.success("Profile retrieved successfully", response));
    }

    /**
     * Search profiles with various criteria.
     * GET /api/v1/profiles/search
     *
     * @param criteria  the search criteria
     * @param page      the page number
     * @param size      the page size
     * @param sort      the field to sort by
     * @param direction the sort direction
     * @return a page of profiles matching the criteria
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "profileApi")
    @RequiresSubscription
    @Loggable(action = "SEARCH_PROFILES")
    @Operation(summary = "Search profiles with criteria", description = "Search profiles using various filters like gender, religion, caste, age range, etc.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profiles retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search criteria or pagination parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<Page<ProfileListView>>> searchProfiles(
            @Valid @ModelAttribute @Parameter(description = "Search criteria") ProfileSearchCriteria criteria,
            @RequestParam(defaultValue = "0") @Min(0) @Parameter(description = "Page number") int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) @Parameter(description = "Page size") int size,
            @RequestParam(defaultValue = "userProfileId") @Parameter(description = "Sort field") String sort,
            @RequestParam(defaultValue = "DESC") @Parameter(description = "Sort direction") Sort.Direction direction) {

        if (!sort.matches("^(userProfileId|createdAt|age|height)$")) {
            throw new IllegalArgumentException("Invalid sort field: " + sort);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        Page<ProfileListView> profiles = profileService.searchProfiles(criteria, pageable);

        return ResponseEntity.ok(
                ResponseDto.success("Profiles retrieved successfully", profiles));
    }

    /**
     * Get profile by user ID (Admin only).
     * GET /api/v1/profiles/user/{userId}.
     *
     * @param userId the user ID
     * @return the profile details
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @RateLimiter(name = "profileApi")
    @Loggable(action = "ADMIN_VIEW_PROFILE")
    @Operation(summary = "Get profile by user ID (Admin only)", description = "Retrieve a user's profile by user ID. Only accessible by administrators.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Profile not found for the specified user"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User not authorized (Admin role required)"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<ProfileResponse>> getProfileByUserId(
            @PathVariable @NotNull @Positive @Parameter(description = "User ID") Integer userId) {

        ProfileResponse response = profileService.getProfileByUserId(userId);

        return ResponseEntity.ok(
                ResponseDto.success("Profile retrieved successfully", response));
    }

    // ...
    /**
     * Update current user's profile (partial update - PATCH semantics).
     * PATCH /api/v1/profiles/me
     *
     * Only updates fields that are provided in the request.
     * Requires version for optimistic locking.
     *
     * @param request the update request
     * @return updated profile
     */
    @PatchMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "profileApi")
    @Loggable(action = "UPDATE_PROFILE")
    @Operation(summary = "Update current user's profile", description = "Update the authenticated user's profile with partial data. Requires version for optimistic locking.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or validation errors"),
            @ApiResponse(responseCode = "404", description = "Profile not found for current user"),
            @ApiResponse(responseCode = "409", description = "Optimistic locking conflict - profile was modified by another transaction"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<ProfileResponse>> updateCurrentUserProfile(
            @Valid @RequestBody @Parameter(description = "Profile update request") UpdateProfileRequest request) {

        log.info("Received update request for current user's profile");

        ProfileResponse response = profileService.updateCurrentUserProfile(request);

        return ResponseEntity.ok(
                ResponseDto.success("Profile updated successfully", response));
    }

    /**
     * Delete current user's profile (soft delete).
     * DELETE /api/v1/profiles/me
     *
     * Marks profile as deleted but doesn't remove from database.
     * Idempotent - returns success even if already deleted.
     *
     * @return success response
     */
    @DeleteMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "profileApi")
    @Loggable(action = "DELETE_PROFILE")
    @Operation(summary = "Delete current user's profile", description = "Soft delete the authenticated user's profile. Profile is marked as deleted but preserved in database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Profile not found for current user"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User not authorized"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<Void>> deleteCurrentUserProfile() {

        log.info("Received delete request for current user's profile");

        profileService.deleteCurrentUserProfile();

        return ResponseEntity.ok(
                ResponseDto.success("Profile deleted successfully", null));
    }

    /**
     * Get profile count by gender.
     * GET /api/v1/profiles/stats/gender/{gender}
     *
     * @param gender the gender
     * @return count of active profiles
     */
    @GetMapping("/stats/gender/{gender}")
    @RateLimiter(name = "profileApi")
    @Operation(summary = "Get profile count by gender", description = "Get the total count of active profiles for a specific gender")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid gender parameter"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<Long>> getCountByGender(
            @PathVariable @NotNull @Parameter(description = "Gender (MALE/FEMALE/OTHER)") String gender) {

        long count = profileService.getProfileCountByGender(gender);

        return ResponseEntity.ok(
                ResponseDto.success("Count retrieved successfully", count));
    }
}
